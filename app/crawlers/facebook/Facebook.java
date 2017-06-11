package crawlers.facebook;

import models.DataSource;
import models.Person;
import models.PersonAccount;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;

public class Facebook {
    public static final String FB_URL = "https://mbasic.facebook.com/";
    static String myId;

    private FacebookSession session = new FacebookSession();

    @Transactional
    public int processPeople() throws IOException {
        FacebookPersonProcessor processor = new FacebookPersonProcessor(JPA.em(), session);
        processMe(processor);
        return processFriendsOf(myId, processor);
    }

    @Transactional
    private void processMe(FacebookPersonProcessor processor) throws IOException {
        EntityManager em = JPA.em();
        Person me = em.find(Person.class, Person.DefaultPersons.me.getId());
        myId = FacebookPeople.getMyId(session);
        PersonAccount meFb = new PersonAccount();
        meFb.setAccount(myId);
        meFb.setSource(DataSource.DataSourceList.facebook);
        meFb.setPerson(me);
        PersonAccount old = em.find(PersonAccount.class, meFb);
        if(old == null) {
            em.persist(meFb);
            me.getAccounts().add(meFb);
        }
        processor.process(FacebookPerson.getPerson(myId, session));
    }

    @Transactional
    private int processFriendsOf(String personId, FacebookPersonProcessor processor) throws IOException {
        List<String> ids = FacebookPeople.getFriendsIds(personId, session);
        for (String id : ids) {
            processor.process(FacebookPerson.getPerson(id, session));
        }
        return ids.size();
    }

}
