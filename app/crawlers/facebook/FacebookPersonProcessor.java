package crawlers.facebook;

import crawlers.PersonProcessor;
import models.DataSource;
import models.Person;
import models.PersonAccount;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class FacebookPersonProcessor extends PersonProcessor<FacebookPerson> {
    public FacebookPersonProcessor(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected List<PersonAccount> getAccounts(FacebookPerson sourceObject) {
        ArrayList<PersonAccount> accounts = new ArrayList<>();
        PersonAccount publicId = new PersonAccount();
        publicId.setSource(DataSource.DataSourceList.facebook);
        publicId.setAccount(sourceObject.publicId);
        accounts.add(publicId);
        if (!sourceObject.publicId.equals(sourceObject.id)) {
            PersonAccount id = new PersonAccount();
            id.setSource(DataSource.DataSourceList.facebook);
            id.setAccount(sourceObject.id);
            accounts.add(publicId);
        }
        return accounts;
    }

    @Override
    protected void processWithPerson(Person person, FacebookPerson sourceObject) {
        processName(person, sourceObject);
    }

    private void processName(Person person, FacebookPerson sourceObject) {
        String name = sourceObject.about.select("strong").first().text();
        person.setDisplayName(name);
        person.setLastName(name.substring(name.lastIndexOf(" ") + 1));
        person.setFirstName(name.substring(0, name.lastIndexOf(" ")));
    }
}
