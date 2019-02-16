package superlift;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JsoupParser {
    private static final String SUPERLIFT_URL = "http://superlift.com/";
    private static final String SITEMAP = "http://superlift.com/product_detail_pages_sitemap.xml";

    public BigDecimal getNewPrice(String itemLink) {
        setProxies();
        String priceStr = "";
        Document doc = getPage(itemLink);
        Element priceKeeprEl = null;
        try {
            priceKeeprEl = doc.getElementsByAttributeValueStarting("id", "product-add-to-cart-form").first();
            priceKeeprEl = priceKeeprEl.getElementsByClass("product-price").first();
            priceStr = priceKeeprEl.ownText();
            priceStr = priceStr.replace("$", "");
            priceStr = priceStr.replaceAll(",", "");
        }
        catch (NullPointerException e){
            return new BigDecimal(0);
        }


        return new BigDecimal(priceStr);
    }

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
        System.out.println("getting "+xmlItemUrl);
        setProxies();
        String title = "";
        Document doc = getPage(xmlItemUrl);
        if (doc!=null){
            while (true){
                Element upperEl = doc.getElementsByAttributeValue("class", "product-detail-section-part-title").first();
                if (upperEl!=null){
                    title = upperEl.text();
                    return title;
                }
                else {
                    doc = getPage(xmlItemUrl);
                }
            }
        }
        else {
            return "";
        }
    }

    private Document getPage(String xmlItemUrl) {
        Document doc = null;
        while (true){
            try {
                doc = Jsoup.connect(xmlItemUrl).timeout(20 * 1000).userAgent("Mozilla/5.0").get();
                if (doc!=null){
                    break;
                }
            } catch (IOException ignored){
            }
        }
        return doc;
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
