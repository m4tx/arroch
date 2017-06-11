package crawlers.messenger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crawlers.messenger.payload.Messages;
import crawlers.messenger.payload.ThreadInfo;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class ThreadParser {
    private class MercuryInputStreamReader extends InputStreamReader {
        public MercuryInputStreamReader(InputStream in) throws IOException {
            super(in);
            // Ignore "for (;;);"
            byte[] b = new byte[9];
            in.read(b);
        }
    }

    public static Gson createMessengerGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateAdapter())
                .create();
    }

    public ThreadInfo getThreads(InputStream is) throws IOException {
        Gson gson = createMessengerGson();
        MercuryInputStreamReader reader = new MercuryInputStreamReader(is);
        return gson.fromJson(reader, ThreadInfo.class);
    }

    private String readMessagesJson(InputStream is) throws IOException {
        String content = IOUtils.toString(is, "UTF-8");
        return content.substring(0, content.lastIndexOf('{'));
    }

    public Messages getMessages(InputStream is) throws IOException {
        Gson gson = createMessengerGson();
        return gson.fromJson(readMessagesJson(is), Messages.class);
    }
}
