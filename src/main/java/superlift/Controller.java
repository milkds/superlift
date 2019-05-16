package superlift;

import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import superlift.entities.ItemGroup;
import superlift.entities.SuperLiftItem;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller {

    public static void main(String[] args) throws IOException, InterruptedException {
        new Controller().checkSite();
    }

    public void checkSite(){
        DBSaver.backupDB();
        Statistics statistics = new Statistics();
        ParseLauncher.launchParse(statistics);
       // List<String> urlsFromNewItemGroups = getUrlsFromNewItemGroups(statistics);
        List<String> webItemUrls = getWebItemUrls();
        /*if (urlsFromNewItemGroups.size()!=0){
            webItemUrls.addAll(urlsFromNewItemGroups);
        }*/
        List<String> dbItemUrls = SuperliftDAO.getAllItemUrls();
        if (changesDetected(webItemUrls, dbItemUrls)){
            WebDriver driver = SileniumUtil.initDriver();
            deleteItems(webItemUrls, dbItemUrls, statistics);
            addNewItems(webItemUrls, dbItemUrls, statistics, driver);
            driver.quit();
        }
        checkPriceChanges(webItemUrls, statistics);
        finishCheck(statistics);
    }

    private List<String> getUrlsFromNewItemGroups(Statistics statistics) {
        List<ItemGroup> newItemGroups = statistics.getAddedGroups();
        if (newItemGroups.size()==0){
            return new ArrayList<>();
        }


        return SileniumUtil.getUrlsFromNewItemGroups(newItemGroups);
    }

    private void finishCheck(Statistics statistics) {
        statistics.prepareReport();
        File report = statistics.getReportFile();
        File dbExcel = ExcelExporter.prepareReportForEmail();
        List<File> files = List.of(dbExcel, report);
        EmailSender.sendMail(files, statistics);
        //send files
        HibernateUtil.shutdown();
    }

    private File getReportFile(Statistics statistics) {
        //impl
        return null;
    }

    private void checkPriceChanges(List<String> webItemUrls, Statistics statistics) {
        Map<SuperLiftItem, BigDecimal> changedPricesMap = statistics.getChangedPricesMap();
        int size = webItemUrls.size();
        AtomicInteger counter = new AtomicInteger();
        webItemUrls.forEach(url->{
            counter.getAndIncrement();
            changedPricesMap.putAll(new PriceChangeChecker(url).checkPrice());
            System.out.println("Checked item " + counter + " of total " + size);
        });
    }


    private void deleteItems(List<String> webItemUrls, List<String> dbItemUrls, Statistics statistics) {
        List<SuperLiftItem> deletedItems = statistics.getDeletedItems();
        dbItemUrls.forEach(url->{
            if (!webItemUrls.contains(url)){
                deletedItems.add(SuperliftDAO.deleteItemByUrl(url));
            }
        });
    }
    private void addNewItems(List<String> webItemUrls, List<String> dbItemUrls, Statistics statistics, WebDriver driver) {
        List<SuperLiftItem> addedItems = statistics.getAddedItems();
        for (String url : webItemUrls) {
            if (!dbItemUrls.contains(url)) {
                try {
                    SileniumUtil.openItemUrl(driver, url);
                } catch (NotFoundException e) {
                    //that's for case when item is present in sitemap, but actually it doesn't open.
                    SuperLiftItem item = new SuperLiftItem();
                    item.setItemUrl(url);
                    item.setStatus("NOT AVAILABLE");
                    SuperliftDAO.saveItem(item);
                    continue;
                }
                catch (IOException e){
                    continue;
                }
                SuperLiftItem item = ItemBuilder.buildItem(driver);
                SuperliftDAO.saveItem(item);
                addedItems.add(item);
            }
        }
    }

    private boolean changesDetected(List<String> webItemUrls, List<String> dbItemUrls) {
        if (webItemUrls.size()!=dbItemUrls.size()){
            return true;
        }
        for (String url : webItemUrls) {
            if (!dbItemUrls.contains(url)) {
                return true;
            }
        }

        return false;
    }

    private List<String> getWebItemUrls() {
        return new JsoupParser().getXmlItemUrls();
    }
}
