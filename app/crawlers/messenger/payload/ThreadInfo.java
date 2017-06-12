package crawlers.messenger.payload;

import java.util.Date;

public class ThreadInfo {
    public class ThreadParticipant {
        private String fbid;
        private String name;

        public String getFbid() {
            return fbid;
        }

        public String getName() {
            return name;
        }
    }

    public class Payload {
        public class Thread {
            private String threadId;
            private String threadFbid;
            private String name;
            private String[] participants;
            private Long messageCount;
            private Date lastMessageTimestamp;

            public String getThreadId() {
                return threadId;
            }

            public String getThreadFbid() {
                return threadFbid;
            }

            public String getName() {
                return name;
            }

            public String[] getParticipants() {
                return participants;
            }

            public Long getMessageCount() {
                return messageCount;
            }

            public Date getLastMessageTimestamp() {
                return lastMessageTimestamp;
            }
        }

        private Thread[] threads;
        private ThreadParticipant[] participants;

        private ThreadParticipant findParticipantWithId(String fbid) {
            for (ThreadParticipant participant : participants) {
                if (participant.getFbid().equals(fbid)) {
                    return participant;
                }
            }
            throw new IllegalArgumentException("There is no participant with given FB ID");
        }

        public ThreadParticipant[] getParticipants() {
            return participants;
        }

        public Thread[] getThreads() {
            return threads;
        }
    }

    private Payload payload;

    public ThreadParticipant[] getParticipants() {
        return payload.getParticipants();
    }

    public Payload.Thread[] getThreads() {
        return payload.getThreads();
    }
}

