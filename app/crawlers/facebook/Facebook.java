package crawlers.facebook;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import java.io.IOException;
import java.util.List;

public class Facebook {
    public static final String FB_URL = "https://mbasic.facebook.com/";

    private FacebookSession session = new FacebookSession();

    @Transactional
    public int processPeople() throws IOException {
        FacebookPersonProcessor processor = new FacebookPersonProcessor(JPA.em());
        String myId = processMe(processor);
        return processFriendsOf(myId, processor);
    }

    private String processMe(FacebookPersonProcessor processor) throws IOException {
        String myId = FacebookPeople.getMyId(session);
        processor.process(FacebookPerson.getPerson(myId, session));
        return myId;
    }

    private int processFriendsOf(String personId, FacebookPersonProcessor processor) throws IOException {
        List<String> ids = FacebookPeople.getFriendsIds(personId, session);
        for (String id : ids) {
            processor.process(FacebookPerson.getPerson(id, session));
        }
        return ids.size();
    }

}
