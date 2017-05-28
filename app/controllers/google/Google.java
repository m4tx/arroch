package controllers.google;

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
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Google extends Controller {
    // fixme hardcoded server URL
    private static final String REDIRECT_URL = "http://localhost:9000/google/authenticated";
    private static final Collection<String> SCOPES = Collections.singletonList(PeopleServiceScopes.CONTACTS_READONLY);

    private HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
            new InputStreamReader(Google.class.getResourceAsStream("/google_client_secrets.json")));

    public Google() throws GeneralSecurityException, IOException {
    }

    private GoogleAuthorizationCodeFlow getAuthCodeFlow() {
        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, SCOPES).setAccessType("offline").build();
    }

    public Result redirectToGoogle() {
        String requestUrl = getAuthCodeFlow().newAuthorizationUrl().setRedirectUri(REDIRECT_URL).build();
        return redirect(requestUrl);
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

    private String getRequestMaskIncludField() {
        // Check available fields at: https://developers.google.com/people/api/rest/v1/people#resource-person

        String[] fields = {
                "names",
                "emailAddresses",
                "phoneNumbers"
        };
        return Arrays.stream(fields).map(x -> "person." + x).collect(Collectors.joining(","));
    }

    @Transactional
    public Result authenticated() throws IOException {
        GooglePersonProcessor processor = new GooglePersonProcessor(JPA.em());
        PeopleService service = createPeopleService(request().getQueryString("code"));

        ListConnectionsResponse response = service.people().connections()
                .list("people/me")
                .setRequestMaskIncludeField(getRequestMaskIncludField())
                .setPageSize(2000)
                .execute();
        List<Person> connections = response.getConnections();

        for (Person person : connections) {
            processor.processGooglePerson(person);
        }

        return ok("Processed " + connections.size() + " people");
    }
}
