package crawlers;

import models.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import utils.SimpleQuery;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Base class for any processors of retrieved people data. Provides a unified API
 * for invoking processing and helper methods that take care of merging the information
 * with existing data.
 *
 * @param <T> class that contains the person data, i.e. class that the processor will
 *            get the data from
 */
public abstract class PersonProcessor<T> {
    protected EntityManager em;

    public PersonProcessor(EntityManager entityManager) {
        this.em = entityManager;
    }

    /**
     * Process given {@code sourceObject} and add the data to
     * a newly created or existing {@link Person} object
     *
     * @param sourceObject an object to get the data from
     * @return created or modified Person object
     */
    public Person process(T sourceObject) {
        List<PersonAccount> accounts = getAccounts(sourceObject);
        Person person = null;
        for (PersonAccount account : accounts) {
            PersonAccount personAccount = em.find(PersonAccount.class, account);
            if (personAccount != null) {
                if (person != null && person != personAccount.getPerson()) {
                    throw new PersonAccountConflictException(person, personAccount.getPerson());
                }
                person = personAccount.getPerson();
            }
        }
        if (person == null) {
            person = new PersonFactory().build(em);
        }

        for (PersonAccount account : accounts) {
            if (!person.getAccounts().contains(account)) {
                account.setPerson(person);
                em.persist(account);
                person.getAccounts().add(account);
            }
        }

        processWithPerson(person, sourceObject);
        return person;
    }

    /**
     * Return list of PersonAccount objects that can be used to uniquely identify a person
     * <p>
     * This is used to avoid creating multiple Person objects with the same data
     *
     * @param sourceObject an object that the data should be retrieved from
     * @return list of PersonAccounts (not persisted)
     */
    protected abstract List<PersonAccount> getAccounts(T sourceObject);

    /**
     * Add data from given {@code sourceObject} to the provided {@code person}
     *
     * @param person       Person object to save the data into
     * @param sourceObject an object that the data should be retrieved from
     */
    protected abstract void processWithPerson(Person person, T sourceObject);

    /**
     * Add a {@link PersonInfo} object to {@link Person} if not exists yet
     *
     * @param person     Person instance to add the PersonInfo to
     * @param personInfo PersonInfo instance to add
     */
    protected void addPersonInfo(Person person, PersonInfo personInfo) {
        // Check if person info already exists
        if (new SimpleQuery<>(em, PersonInfo.class)
                .where("person", person)
                .where("type", personInfo.getType())
                .where("value", personInfo.getValue())
                .getResultList()
                .isEmpty()) {
            em.persist(personInfo);
            person.getInfo().add(personInfo);
        }
    }

    /**
     * Add given file if it does not yet exists for given {@link Person}
     * and additionally set is as a photo for if the Person does not have it yet
     *
     * @param person Person object to add the photo to
     * @param url URL of the photo to download and save
     * @return {@link FileMeta} object if the photo was added; null otherwise
     * @see #addFileIfNotExists(Person, URL)
     * @throws IOException if an error occurred when reading the photo
     */
    protected FileMeta addPhotoIfNotExists(Person person, URL url) throws IOException {
        FileMeta fileMeta = addFileIfNotExists(person, url);
        if (person.getPhoto() == null && fileMeta != null) {
            person.setPhoto(fileMeta);
        }
        return fileMeta;
    }

    /**
     * Add given file if it does not yet exists for given {@link Person}
     *
     * @param person Person object to add the file to
     * @param url URL of the file to download and save
     * @return {@link FileMeta} object if the file was added; null otherwise
     * @see #addPhotoIfNotExists(Person, URL)
     * @throws IOException if an error occurred when reading the file
     */
    protected FileMeta addFileIfNotExists(Person person, URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        byte[] fileData = IOUtils.toByteArray(urlConnection.getInputStream());
        String digestString = FileManager.getSha512Digest(fileData);

        if (person.getSelfGroup().getFiles().stream().noneMatch(x -> x.getDigest().equals(digestString))) {
            FileMeta fileMeta = FileManager.createFile(
                    FilenameUtils.getName(url.getPath()), urlConnection.getContentType(), em);
            FileUtils.writeByteArrayToFile(FileManager.getFile(fileMeta), fileData);

            person.getSelfGroup().getFiles().add(fileMeta);
            return fileMeta;
        }
        return null;
    }
}
