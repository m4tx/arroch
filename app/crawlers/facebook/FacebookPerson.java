package crawlers.facebook;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FacebookPerson {
    String publicId;
    String id;
    Element about;
    String photoPageUrl;

    FacebookPerson(String publicId, Element about) {
        this.about = about.select("#objects_container").first();
        this.publicId = publicId;
        photoPageUrl = this.about.select("a").first().attr("href");
        Pattern pattern = Pattern.compile("(?<=(&id=)|(profile_id=))\\d*");
        Matcher matcher = pattern.matcher(photoPageUrl);
        id = (matcher.find()) ? matcher.group() : publicId;
    }

    static FacebookPerson getPerson(String publicId, FacebookSession session) throws IOException {
        Document page;
        try {
            Long.parseLong(publicId);
            page = session.getDocument(Facebook.FB_URL + "profile.php?v=info&id=" + publicId);
        } catch (NumberFormatException e) {
            page = session.getDocument(Facebook.FB_URL + publicId + "/about");
        }
        return new FacebookPerson(publicId, page);
    }
}
