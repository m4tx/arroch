package crawlers.facebook;

import crawlers.PersonProcessor;
import models.*;
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

    FacebookPersonProcessor(EntityManager entityManager, FacebookSession session) {
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
        for (Element email : emails) {
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
        Logger.info("Processing " + person.getDisplayName());
        processPhoto(person, sourceObject, session);
        if (sourceObject.publicId.equals(Facebook.myId)) return;
        processBasicInfo(person, sourceObject);
        processContactInfo(person, sourceObject);
        processPlaces(person, sourceObject);
        processEducation(person, sourceObject);
        processWork(person, sourceObject);
    }

    private void processName(Person person, FacebookPerson sourceObject) {
        String name = sourceObject.about.select("strong").first().text();
        name = name.replaceAll("\\([^)]*\\)", "");
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

    private void processBasicInfo(Person person, FacebookPerson sourceObject) {
        Element info = sourceObject.about.select("#basic-info").first();
        if (info == null) return;

        processInfoEntity(person, info, PropertyType.PropertyTypeList.birthdate, "Birthday");
        processInfoEntity(person, info, PropertyType.PropertyTypeList.gender, "Gender");
        processInfoEntity(person, info, PropertyType.PropertyTypeList.orientation, "Interested in");
        processInfoEntity(person, info, PropertyType.PropertyTypeList.languages, "Languages");
        processInfoEntity(person, info, PropertyType.PropertyTypeList.political, "Political Views");
    }

    private void processContactInfo(Person person, FacebookPerson sourceObject) {
        Element info = sourceObject.about.select("#contact-info").first();
        if (info == null) return;

        processInfoEntity(person, info, PropertyType.PropertyTypeList.website, "Websites");
        processInfoEntity(person, info, PropertyType.PropertyTypeList.contactAddress, "Address");
    }

    private void processPlaces(Person person, FacebookPerson sourceObject) {
        Element info = sourceObject.about.select("#living").first();
        if (info == null) return;

        processInfoEntity(person, info, PropertyType.PropertyTypeList.address, "Current City");
        processInfoEntity(person, info, PropertyType.PropertyTypeList.homeAddress, "Home Town");
    }

    private void processWork(Person person, FacebookPerson sourceObject) {
        Element info = sourceObject.about.select("#work").first();
        if (info == null) return;
        processInfoRichEntity(person, info, PropertyType.PropertyTypeList.company);
    }

    private void processEducation(Person person, FacebookPerson sourceObject) {
        Element info = sourceObject.about.select("#education").first();
        if (info == null) return;
        processInfoRichEntity(person, info, PropertyType.PropertyTypeList.education);
    }

    private void processInfoEntity(Person person, Element data, PropertyType type, String title) {
        Elements rows = data.select("div[title=" + title + "]");
        for (Element row : rows) {
            String val = row.select("span, div, a").last().text();
            if (val == null) continue;
            addPersonInfo(person, new PersonInfo(person, type, val));
        }
    }

    private void processInfoRichEntity(Person person, Element data, PropertyType type) {
        Elements rows = data.select("div[id^=u_]");
        for (Element row : rows) {
            Element valSpan = row.select("span").first();
            String val = valSpan.select("a").first().text();
            if (val == null) continue;
            addPersonInfo(person, new PersonInfo(person, type, val));
        }
    }
}
