package crawlers.messenger;

import crawlers.messenger.payload.MessageQuery;
import crawlers.messenger.payload.Messages;
import crawlers.messenger.payload.ThreadInfo;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import play.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static crawlers.messenger.ThreadParser.createMessengerGson;

public class MessengerSession {
    private static final String COOKIES_FILENAME = "/messenger_cookies";

    private static final String MESSENGER_SCHEME = "https";
    private static final String MESSENGER_HOST = "www.messenger.com";

    private static final String BASE_URL = "/";
    private static final String THREAD_LIST_URL = "/ajax/mercury/threadlist_info.php";
    private static final String MESSAGES_URL = "/api/graphqlbatch/";

    private static final Pattern USER_ID_REGEX = Pattern.compile("\"USER_ID\":\"(.+?)\"");
    private static final Pattern CLIENT_REV_REGEX = Pattern.compile("\"client_revision\":(\\d+)");
    private static final Pattern DTSG_REGEX = Pattern.compile("\"DTSGInitialData\".*?,\\{\"token\":\"(.+?)\"}");
    private static final String ACCEPT_LANGUAGE = "en-US,en;q=0.5";
    private static final String USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36";

    private CloseableHttpClient httpClient;
    private ThreadParser parser = new ThreadParser();
    private String cookies;

    private int requestCounter = 1;
    private String userId;
    private String clientRev;
    private String fbDtsg;

    MessengerSession() {
        httpClient = HttpClients.custom()
                .setMaxConnPerRoute(1000)
                .setConnectionTimeToLive(15, TimeUnit.SECONDS)
                .build();

        InputStream secretsStream = getClass().getResourceAsStream(COOKIES_FILENAME);

        if(secretsStream==null) {
            Logger.warn("Cannot find Messenger cookies file " + COOKIES_FILENAME);
            return;
        }

        try {
            cookies = IOUtils.toString(secretsStream, "UTF-8").trim();
        } catch (IOException e) {
            Logger.warn("Cannot open Messenger cookies file " + COOKIES_FILENAME);
            return;
        }

        try {
            initializeSession();
        } catch (IOException | URISyntaxException | MessengerSessionFieldNotFoundException e) {
            throw new RuntimeException("Could not initialize MessengerSession", e);
        }
    }

    private URIBuilder buildUrl(String path) {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(MESSENGER_SCHEME).setHost(MESSENGER_HOST).setPath(path);
        return builder;
    }

    private void prepareRequest(HttpMessage request) {
        request.addHeader("Accept-Language", ACCEPT_LANGUAGE);
        request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("Cookie", cookies);
    }

    private void initializeSession() throws IOException, URISyntaxException, MessengerSessionFieldNotFoundException {
        HttpGet request = new HttpGet(buildUrl(BASE_URL).build());
        prepareRequest(request);
        HttpResponse response = httpClient.execute(request);
        String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");

        Matcher userIdMatcher = USER_ID_REGEX.matcher(content);
        if (!userIdMatcher.find()) {
            throw new MessengerSessionFieldNotFoundException("user_id");
        }
        userId = userIdMatcher.group(1);

        Matcher clientRevMatcher = CLIENT_REV_REGEX.matcher(content);
        if (!clientRevMatcher.find()) {
            throw new MessengerSessionFieldNotFoundException("client_revision");
        }
        clientRev = clientRevMatcher.group(1);

        Matcher dtsgMatcher = DTSG_REGEX.matcher(content);
        if (!dtsgMatcher.find()) {
            throw new MessengerSessionFieldNotFoundException("fb_dtsg");
        }
        fbDtsg = dtsgMatcher.group(1);
    }

    private List<NameValuePair> getQueryParameters() {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("client", "web_messenger"));
        params.add(new BasicNameValuePair("__user", userId));
        params.add(new BasicNameValuePair("__a", "1"));
        params.add(new BasicNameValuePair("__af", "iw"));
        params.add(new BasicNameValuePair("__req", Integer.toString(requestCounter++, 36)));
        params.add(new BasicNameValuePair("__be", "-1"));
        params.add(new BasicNameValuePair("__pc", "PHASED:messengerdotcom_pkg"));
        params.add(new BasicNameValuePair("__rev", clientRev));
        params.add(new BasicNameValuePair("fb_dtsg", fbDtsg));
        return params;
    }

    public ThreadInfo getThreadInfo(int offset, int limit) throws IOException, URISyntaxException {
        HttpPost request = new HttpPost(buildUrl(THREAD_LIST_URL).build());
        List<NameValuePair> queryParameters = getQueryParameters();
        queryParameters.add(new BasicNameValuePair("inbox[offset]", "" + offset));
        queryParameters.add(new BasicNameValuePair("inbox[limit]", "" + limit));
        request.setEntity(new UrlEncodedFormEntity(queryParameters));
        prepareRequest(request);

        HttpResponse response = httpClient.execute(request);
        ThreadInfo threads = parser.getThreads(response.getEntity().getContent());
        request.releaseConnection();
        return threads;
    }

    private String createMessageQuery(String threadFbid, Date before, int messageLimit) {
        MessageQuery query = new MessageQuery();
        MessageQuery.MessageQueryParams queryParams = query.getQueryParams();
        queryParams.setId(threadFbid);
        queryParams.setBefore(before);
        queryParams.setMessageLimit(messageLimit);
        return createMessengerGson().toJson(query);
    }

    public Messages getMessages(String threadId, Date before, int messageLimit) throws IOException, URISyntaxException {
        HttpPost request = new HttpPost(buildUrl(MESSAGES_URL).build());
        List<NameValuePair> queryParameters = getQueryParameters();
        queryParameters.add(new BasicNameValuePair("queries", createMessageQuery(threadId, before, messageLimit)));
        request.setEntity(new UrlEncodedFormEntity(queryParameters));
        prepareRequest(request);

        CloseableHttpResponse response = httpClient.execute(request);
        Messages messages = parser.getMessages(response.getEntity().getContent());
        request.releaseConnection();
        return messages;
    }
}
