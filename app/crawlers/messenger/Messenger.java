package crawlers.messenger;

import crawlers.messenger.payload.Messages;
import crawlers.messenger.payload.ThreadInfo;
import models.*;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import utils.SimpleQuery;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class Messenger {
    private MessengerSession session = new MessengerSession();
    private MessengerPersonProcessor personProcessor;

    @Transactional
    public void process(int numLastThreads) throws IOException, URISyntaxException {
        personProcessor = new MessengerPersonProcessor(JPA.em());
        ThreadInfo threadInfo = session.getThreadInfo(0, numLastThreads);
        Map<String, Person> participants = processParticipants(threadInfo.getParticipants());
        for (ThreadInfo.Payload.Thread thread : threadInfo.getThreads()) {
            processThread(participants, thread);
        }
    }

    private Map<String, Person> processParticipants(ThreadInfo.ThreadParticipant[] participants) {
        Map<String, Person> map = new HashMap<>();
        for (ThreadInfo.ThreadParticipant participant : participants) {
            Person person = personProcessor.process(participant);
            map.put(participant.getFbid(), person);
        }
        return map;
    }

    private void processThread(Map<String, Person> participants, ThreadInfo.Payload.Thread thread) throws IOException, URISyntaxException {
        Logger.info("Messenger: processing thread #" + thread.getThreadFbid());

        EntityManager em = JPA.em();
        ConversationGroup conversationGroup = getOrCreateGroup(thread);
        addGroupMembers(participants, thread, conversationGroup);
        setGroupName(thread, conversationGroup);
        Date start = getLastTimestamp(conversationGroup);

        if (start.equals(thread.getLastMessageTimestamp())) {
            return;
        }

        boolean messagesLeft = true;
        int messageLimit = 100;
        int messagesLoaded = 0;
        Date before = new Date();
        do {
            Messages messages = session.getMessages(thread.getThreadFbid(), before, messageLimit);
            messageLimit *= 2;
            messageLimit = messageLimit > 3200 ? 3200 : messageLimit;
            if (messages.getMessageList().length == 0) {
                messagesLeft = false;
            }

            for (Messages.MessageNode message : messages.getMessageList()) {
                if (message.getTimestamp().compareTo(start) < 0) {
                    messagesLeft = true;
                    break;
                }
                if (message.getTimestamp().compareTo(before) < 0) {
                    before = new Date(message.getTimestamp().getTime() - 1);
                }

                Post post = new Post();
                Message modelMessage = new Message();
                post.setThread(modelMessage);
                post.setGroup(conversationGroup);
                modelMessage.setPost(post);

                modelMessage.setAuthor(participants.get(message.getMessageSenderId()));
                if (message.getMessage() != null) {
                    modelMessage.setBody(message.getMessage().getText());
                }
                modelMessage.setTimestamp(message.getTimestamp());

                em.persist(post);
                em.persist(modelMessage);
            }

            messagesLoaded += messages.getMessageList().length;
            Logger.info("Loaded " + messagesLoaded + " messages, earliest at " + before);
            if (messagesLoaded >= 8000 && messagesLeft) {
                Logger.info("Over 8k messages loaded, aborting");
                messagesLeft = false;
            }
        } while (messagesLeft);
    }

    private ConversationGroup getOrCreateGroup(ThreadInfo.Payload.Thread thread) {
        EntityManager em = JPA.em();
        HashMap<String, Object> where = new HashMap<>(3);
        where.put("source", DataSource.DataSourceList.facebook);
        where.put("externalId", thread.getThreadFbid());
        List<ConversationGroup> groupList = new SimpleQuery<>(em, ConversationGroup.class)
                .whereAllEqual(where)
                .getResultList();
        if (groupList.isEmpty()) {
            ConversationGroup conversationGroup = new ConversationGroup();
            em.persist(conversationGroup);
            return conversationGroup;
        }
        return groupList.get(0);
    }

    private void addGroupMembers(Map<String, Person> participants, ThreadInfo.Payload.Thread thread, ConversationGroup conversationGroup) {
        for (String participantId : thread.getParticipants()) {
            participantId = participantId.replace("fbid:", "");
            Person person = participants.get(participantId);
            if (!conversationGroup.getMembers().contains(person)) {
                conversationGroup.getMembers().add(person);
                person.getInConversations().add(conversationGroup);
            }
        }
    }

    private void setGroupName(ThreadInfo.Payload.Thread thread, ConversationGroup conversationGroup) {
        if (thread.getName() != null && !Objects.equals(thread.getName(), "")) {
            conversationGroup.setName(thread.getName());
        } else {
            String name = conversationGroup.getMembers().stream()
                    .map(Person::getDisplayName)
                    .collect(Collectors.joining(", "));
            conversationGroup.setName(name);
        }
    }

    private Date getLastTimestamp(ConversationGroup conversationGroup) {
        Date date = new Date(Long.MIN_VALUE);
        for (Post post : conversationGroup.getPosts()) {
            Date timestamp = post.getThread().getTimestamp();
            if (timestamp.compareTo(date) > 0) {
                date = timestamp;
            }
        }
        return date;
    }
}
