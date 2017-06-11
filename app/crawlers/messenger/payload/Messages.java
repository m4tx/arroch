package crawlers.messenger.payload;

import java.util.Date;

public class Messages {
    public class MessageSender {
        private String id;
    }

    public class Message {
        private String text;

        public String getText() {
            return text;
        }
    }

    public class MessageNode {
        private Date timestampPrecise;
        private Message message;
        private MessageSender messageSender;

        public Date getTimestamp() {
            return timestampPrecise;
        }

        public String getMessageSenderId() {
            return messageSender.id;
        }

        public Message getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return messageSender.id + " at " + timestampPrecise + ": " + message.text;
        }
    }

    public class MessageList {
        private MessageNode[] nodes;
    }

    private class MessageThread {
        private MessageList messages;
    }

    private class MessageDataPayload {
        private MessageThread messageThread;
    }

    private class QueryDataPayload {
        private MessageDataPayload data;
    }

    private QueryDataPayload o0;

    public MessageNode[] getMessageList() {
        return o0.data.messageThread.messages.nodes;
    }
}
