package superlift.checkers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsoupParser {
    private static final String SUPERLIFT_URL = "http://superlift.com/";
    private static final String SITEMAP = "http://superlift.com/product_detail_pages_sitemap.xml";

    public String getItemTitle(String xmlItemUrl){
        setProxies();

        String title = "";
        Document doc = getPage(xmlItemUrl);
        if (doc!=null){
            Element upperEl = doc.getElementsByAttributeValue("class", "product-detail-wrapper").first();
            upperEl = upperEl.getElementsByAttributeValueEnding("class", "product-detail-title").first();
            title = upperEl.text();
            return title;
        }
        else {
            return "";
        }
    }

    public String getItemLongTitle(String xmlItemUrl){
        setProxies();

        String title = "";
        Document doc = getPage(xmlItemUrl);
        if (doc!=null){
            Element upperEl = doc.getElementsByAttributeValue("class", "product-detail-section-part-title").first();
            title = upperEl.text();
            return title;
        }
        else {
            return "";
        }
    }

    private Document getPage(String xmlItemUrl) {
        try {
            return Jsoup.connect(xmlItemUrl).timeout(20 * 1000).userAgent("Mozilla/5.0").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setProxies() {
        System.setProperty("http.proxyHost", "24.225.1.149");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "24.225.1.149");
        System.setProperty("https.proxyPort", "8080");
    }

    public String getItemUrlForSil(String hmlItemUrl) {
        String rawTitle = getItemTitle(hmlItemUrl);

        return processTitle(rawTitle);
    }

    public String processTitle(String rawTitle) {
        if (rawTitle.length()==0){
            return SUPERLIFT_URL;
        }
        String title = rawTitle.replaceAll("w/", "with");
        title = title.replaceAll(" ", "-");
        title = SUPERLIFT_URL+title;

        return title;
    }

    public List<String> getXmlItemUrls(){
        setProxies();
        List<String> urls = new ArrayList<>();
        Document doc = getPage(SITEMAP);
        if (doc!=null){
            Elements urlEls = doc.getElementsByTag("loc");
            for (Element urlEl: urlEls){
                urls.add(urlEl.text());
            }
        }

        urls.forEach(System.out::println);

        return urls;
    }
}
