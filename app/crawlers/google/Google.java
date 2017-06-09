package crawlers.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Google {
    // fixme hardcoded server URL
    private static final String REDIRECT_URL = "http://localhost:9000" +
            controllers.crawlers.routes.Google.authenticated();
    private static final Collection<String> SCOPES = Collections.singletonList(PeopleServiceScopes.CONTACTS_READONLY);
    private static final String SECRETS_FILENAME = "/google_client_secrets.json";

    private HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private GoogleClientSecrets clientSecrets = loadClientSecrets();

    public Google() throws GeneralSecurityException, IOException {
    }

    private GoogleClientSecrets loadClientSecrets() throws IOException {
        InputStream secretsStream = getClass().getResourceAsStream(SECRETS_FILENAME);
        if (secretsStream == null) {
            Logger.warn("Google: Could not load " + SECRETS_FILENAME);
            return null;
        }
        return GoogleClientSecrets.load(jsonFactory, new InputStreamReader(secretsStream));
    }

    private GoogleAuthorizationCodeFlow getAuthCodeFlow() {
        if (clientSecrets == null) {
            throw new RuntimeException(
                    "Secrets file was not loaded. Make sure there is " + SECRETS_FILENAME + " in conf directory");
        }
        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, SCOPES).setAccessType("offline").build();
    }

    public String getAuthorizationUrl() {
        return getAuthCodeFlow().newAuthorizationUrl().setRedirectUri(REDIRECT_URL).build();
    }

    private PeopleService createPeopleService(String code) throws IOException {
        GoogleTokenResponse tokenResponse = getAuthCodeFlow().newTokenRequest(code).setRedirectUri(REDIRECT_URL).execute();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(clientSecrets)
                .build()
                .setFromTokenResponse(tokenResponse);

        return new PeopleService.Builder(httpTransport, jsonFactory, credential).build();
    }

    private String getRequestMaskIncludeField() {
        // Check available fields at: https://developers.google.com/people/api/rest/v1/people#resource-person

        String[] fields = {
                "names",
                "organizations",
                "urls",
                "emailAddresses",
                "phoneNumbers",
                "addresses",
                "birthdays",
                "photos"
        };
        return Arrays.stream(fields).map(x -> "person." + x).collect(Collectors.joining(","));
    }

    @Transactional
    public int processPeople(String code) throws IOException {
        GooglePersonProcessor processor = new GooglePersonProcessor(JPA.em());
        PeopleService service = createPeopleService(code);

        ListConnectionsResponse response = service.people().connections()
                .list("people/me")
                .setRequestMaskIncludeField(getRequestMaskIncludeField())
                .setPageSize(2000)
                .execute();
        List<Person> connections = response.getConnections();

        for (Person person : connections) {
            processor.process(person);
        }

        return connections.size();
    }
}
