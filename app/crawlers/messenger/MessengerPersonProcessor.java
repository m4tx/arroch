package crawlers.messenger;

import crawlers.PersonProcessor;
import crawlers.messenger.payload.ThreadInfo;
import models.DataSource;
import models.Person;
import models.PersonAccount;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class MessengerPersonProcessor extends PersonProcessor<ThreadInfo.ThreadParticipant> {
    public MessengerPersonProcessor(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected List<PersonAccount> getAccounts(ThreadInfo.ThreadParticipant sourceObject) {
        PersonAccount personAccount = new PersonAccount();
        personAccount.setSource(DataSource.DataSourceList.facebook);
        personAccount.setAccount(sourceObject.getFbid());
        return Collections.singletonList(personAccount);
    }

    @Override
    protected void processWithPerson(Person person, ThreadInfo.ThreadParticipant sourceObject) {
        if (person.getDisplayName() == null || person.getDisplayName().equals("")) {
            String name = sourceObject.getName();
            int spacePos = name.lastIndexOf(" ");
            person.setLastName(name.substring(spacePos + 1));
            if (spacePos != -1) {
                person.setFirstName(name.substring(0, name.lastIndexOf(" ")));
            }
            person.setDisplayName(name);
        }
    }
}
