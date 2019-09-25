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
        System.setProperty("http.proxyHost", "164.68.105.235");
        System.setProperty("http.proxyPort", "3128");
        System.setProperty("https.proxyHost", "164.68.105.235");
        System.setProperty("https.proxyPort", "3128");
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
        List<String> notInSiteMapUrls = getNotInSiteMapUrls();
        urls.addAll(notInSiteMapUrls);
        urls.forEach(System.out::println);

        return urls;
    }

    private List<String> getNotInSiteMapUrls() {
        List<String> result = new ArrayList<>();

        result.add("https://superlift.com/product-detail/40043");
        result.add("https://superlift.com/product-detail/40044");
        result.add("https://superlift.com/product-detail/40045");
        result.add("https://superlift.com/product-detail/40046");
        result.add("https://superlift.com/product-detail/40047");
        result.add("https://superlift.com/product-detail/40048");

        result.add("https://superlift.com/product-detail/K173");
        result.add("https://superlift.com/product-detail/K173B");
        result.add("https://superlift.com/product-detail/K174B");
        result.add("https://superlift.com/product-detail/K178");
        result.add("https://superlift.com/product-detail/K178B");
        result.add("https://superlift.com/product-detail/K179");
        result.add("https://superlift.com/product-detail/K179B");

        return result;
    }
}
