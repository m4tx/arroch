package crawlers.facebook;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

import play.Logger;

class FacebookSession {
    private static final String COOKIES_FILENAME = "/facebook_cookies";

    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private String cookies;

    FacebookSession() {
        InputStream secretsStream = getClass().getResourceAsStream(COOKIES_FILENAME);

        try {
            cookies = IOUtils.toString(secretsStream, "utf8").trim();
        } catch (IOException e) {
            Logger.warn("Cannot open Facebook cookies file " + COOKIES_FILENAME);
        }
    }

    HttpResponse get(HttpGet request) throws IOException {
        request.addHeader("user-agent",
                "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        request.addHeader("cookie", cookies);
        return httpClient.execute(request);
    }

    String fetchPage(String uri) throws IOException {
        HttpGet request = new HttpGet(uri);
        HttpResponse response = get(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
