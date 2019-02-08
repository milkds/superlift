package superlift;

import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import superlift.checkers.JsoupParser;
import superlift.entities.SuperLiftItem;

import java.io.IOException;
import java.util.List;

public class Controller {

    public static void main(String[] args) {
      //  superlift.TestClass.testDriver();
        //TestClass.getCats();
        new Controller().checkSite();
      //  TestClass.testItemTitles();
      //  TestClass.testCategoryFromTitle();
    }

    public void checkSite(){
        List<String> webItemUrls = getWebItemUrls();
        List<String> dbItemUrls = SuperliftDAO.getAllItemUrls();
        Statistics statistics = new Statistics();
        if (changesDetected(webItemUrls, dbItemUrls)){
            WebDriver driver = SileniumUtil.initDriver();
            deleteItems(webItemUrls, dbItemUrls, statistics);
            addNewItems(webItemUrls, dbItemUrls, statistics, driver);
            driver.quit();
        }
        checkPriceChanges(webItemUrls, statistics);
        finishCheck(statistics);
    }

    private void finishCheck(Statistics statistics) {
        HibernateUtil.shutdown();
    }

    private void checkPriceChanges(List<String> webItemUrls, Statistics statistics) {
        System.out.println("Changes checked");
        //impl
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
