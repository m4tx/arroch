package crawlers.messenger.payload;

import java.util.Date;

public class MessageQuery {
    private static final String DOC_ID = "1549485615075443";

    public class MessageQueryParams {
        private String id;
        private int messageLimit = 20;
        private int loadMessages = 1;
        private boolean loadReadReceipts = false;
        private Date before;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getMessageLimit() {
            return messageLimit;
        }

        public void setMessageLimit(int messageLimit) {
            this.messageLimit = messageLimit;
        }

        public int getLoadMessages() {
            return loadMessages;
        }

        public void setLoadMessages(int loadMessages) {
            this.loadMessages = loadMessages;
        }

        public boolean isLoadReadReceipts() {
            return loadReadReceipts;
        }

        public void setLoadReadReceipts(boolean loadReadReceipts) {
            this.loadReadReceipts = loadReadReceipts;
        }

        public Date getBefore() {
            return before;
        }

        public void setBefore(Date before) {
            this.before = before;
        }
    }

    public class MessageQueryPayload {
        private String docId = DOC_ID;
        private MessageQueryParams queryParams = new MessageQueryParams();
    }

    private MessageQueryPayload o0 = new MessageQueryPayload();


    public MessageQueryParams getQueryParams() {
        return o0.queryParams;
    }
}
