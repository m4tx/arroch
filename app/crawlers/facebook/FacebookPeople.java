package crawlers.facebook;

import akka.parboiled2.RuleTrace;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FacebookPeople {
    static List<String> getFriendsIds(String id, FacebookSession session) throws IOException {
        int lastCrawled = 0;
        ArrayList<String> friends = new ArrayList<>();

        do {
            lastCrawled = friends.size();
            Document page = session.getDocument(Facebook.FB_URL + id + "/friends?startindex=" + friends.size());
            Element root = page.select("#root").first();
            Elements entries = root.select("table[role=presentation]");
            for (Element entry : entries) {
                Element link = entry.select("a").first();
                String friendId = link.attr("href");
                Pattern pattern;
                if (friendId.startsWith("/profile.php?")) {
                    pattern = Pattern.compile("(?<=id=)\\d*");
                } else {
                    pattern = Pattern.compile("(?<=\\/)[^?]*");
                }
                Matcher matcher = pattern.matcher(friendId);
                if (matcher.find()) {
                    friends.add(matcher.group());
                }
            }
        } while (lastCrawled != friends.size());

        return friends;
    }

    static String getMyId(FacebookSession session) throws IOException {
        Document page = session.getDocument(Facebook.FB_URL + "home.php");
        Element nav = page.select("div[role=navigation]").first();
        String myId = nav.select("a").get(1).attr("href");
        Pattern pattern;
        if (myId.startsWith("/profile.php?")) {
            pattern = Pattern.compile("(?<=id=)\\d*");
        } else {
            pattern = Pattern.compile("(?<=\\/)[^?]*");
        }
        Matcher matcher = pattern.matcher(myId);
        if (!matcher.find()) throw new RuntimeException("General Crawler Error");
        return matcher.group();
    }
}
