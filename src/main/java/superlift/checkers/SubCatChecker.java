package superlift.checkers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import superlift.ItemBuilder;
import superlift.SileniumUtil;
import superlift.Statistics;
import org.openqa.selenium.WebDriver;
import superlift.SuperliftDAO;
import superlift.entities.SuperLiftItem;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCatChecker {

    private String categoryID;
    private WebDriver driver;
    private Statistics statistics;

    private static final Logger logger = LogManager.getLogger(SubCatChecker.class.getName());

    public SubCatChecker(String categoryID, WebDriver driver, Statistics statistics) {
        this.categoryID = categoryID;
        this.driver = driver;
        this.statistics = statistics;
    }

    public abstract void checkSubCategories();

    public String getCategoryID() {
        return categoryID;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    void checkItemsBySubCategories(List<String> subCatLinks, WebDriver driver) {
        for (String subCatLink: subCatLinks){
            driver.get(subCatLink);
         //   By waitBy = By.cssSelector("span[id^='category-sort-select']");
            By waitBy = By.className("price-bar");
            SileniumUtil.waitForElement(waitBy, driver);
            String subCatName = driver.findElement(By.className("active")).getText();
            List<String> linksWeb = getLinksFromWeb(driver);
            List<String> linksDB = SuperliftDAO.getItemsBySubcategory(subCatName);
            if (changesPresent(linksWeb, linksDB)){
                deleteItems(linksWeb, linksDB);
                addItems(linksWeb, linksDB, driver);
            }
            //switching to base page, to avoid staleElementException
            driver.get("https://www.superlift.com/");
            waitBy = By.id("body");
            SileniumUtil.waitForElement(waitBy, driver);
        }
    }

    private List<String> getLinksFromWeb(WebDriver driver) {
        List<String> result = new ArrayList<>();
        List<WebElement> linkEls = driver.findElements(By.className("category-tile-image-link"));
        for (WebElement linkEl: linkEls){
            result.add(linkEl.getAttribute("href"));
        }
        return result;
    }

    private boolean changesPresent(List<String> linksWeb, List<String> linkDB) {
        if (linksWeb.size()!=linkDB.size()){
            return true;
        }

        for (String link: linksWeb){
            if (!linkDB.contains(link)){
                return true;
            }
        }

        return false;
    }

    private void deleteItems(List<String> linksWeb, List<String> linksDB) {
        Statistics statistics = getStatistics();
        List<SuperLiftItem> deletedItems = statistics.getDeletedItems();
        for (String dbLink: linksDB ){
            if (!linksWeb.contains(dbLink)){
                SuperLiftItem deletedItem = SuperliftDAO.markItemDeleted(dbLink);
                deletedItems.add(deletedItem);
            }
        }
    }

    private void addItems(List<String> linksWeb, List<String> linksDB, WebDriver driver) {
        Statistics statistics = getStatistics();
        List<SuperLiftItem> addedItems = statistics.getAddedItems();
        for (String webLink: linksWeb){
            if (!linksDB.contains(webLink)){
                getItemPage(driver, webLink);
                SuperLiftItem newItem = ItemBuilder.buildItem(driver);
                SuperliftDAO.saveItem(newItem);
                addedItems.add(newItem);
                logger.info("New Item added: " + webLink);
            }
        }
    }

    private void getItemPage(WebDriver driver, String webLink) {
        driver.get(webLink);
        //impl
    }

}
