package crawlers.facebook;

import crawlers.PersonProcessor;
import models.DataSource;
import models.Person;
import models.PersonAccount;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import play.Logger;

public class FacebookPersonProcessor extends PersonProcessor<FacebookPerson> {
    private FacebookSession session;

    public FacebookPersonProcessor(EntityManager entityManager, FacebookSession session) {
        super(entityManager);
        this.session = session;
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
            accounts.add(id);
        }
        Elements emails = sourceObject.about.select("a[href^=mailto]");
        for(Element email : emails) {
            PersonAccount e = new PersonAccount();
            e.setSource(DataSource.DataSourceList.email);
            e.setAccount(email.attr("href").substring(7));
            accounts.add(e);
        }
        return accounts;
    }

    @Override
    protected void processWithPerson(Person person, FacebookPerson sourceObject) {
        processName(person, sourceObject);
        processPhoto(person, sourceObject, session);
    }

    private void processName(Person person, FacebookPerson sourceObject) {
        String name = sourceObject.about.select("strong").first().text();
        person.setDisplayName(name);
        int spacePos = name.lastIndexOf(" ");
        person.setLastName(name.substring(spacePos + 1));
        if (spacePos != -1) {
            person.setFirstName(name.substring(0, name.lastIndexOf(" ")));
        }
    }

    private void processPhoto(Person person, FacebookPerson sourceObject, FacebookSession session) {
        try {
            Document picPage = session.getDocument(Facebook.FB_URL + sourceObject.photoPageUrl);
            Element imgElement = picPage.select("#objects_container").first().select("img").first();
            URL pic = new URL(imgElement.attr("src"));
            addPhotoIfNotExists(person, pic);
        } catch (IOException e) {
            Logger.warn("Cannot download facebook photo for " + sourceObject.id);
        }
    }
}
