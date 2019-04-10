package superlift.checkers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import superlift.ItemBuilder;
import superlift.SileniumUtil;
import superlift.Statistics;
import superlift.SuperliftDAO;
import superlift.entities.ItemGroup;
import superlift.entities.SuperLiftItem;

import java.sql.Time;
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
            int attempts = 0;
           while (true){
               try {
                   SileniumUtil.waitForElement(waitBy, driver);
                   break;
               }
               catch (NoSuchElementException|TimeoutException e){
                   if (SileniumUtil.hasConnection()){
                       driver.get(subCatLink);
                       attempts++;
                       if (attempts==5){
                           logger.error("No Price element for category " + subCatLink);
                           driver.get(subCatLink);
                           attempts=0;
                       }
                   }
               }
           }
            String subCatName = driver.findElement(By.className("active")).getText();
            List<String> linksWeb = getLinksFromWeb(driver);
            List<String> linksDB = SuperliftDAO.getItemsBySubcategory(subCatName);
            if (changesPresent(linksWeb, linksDB)){
                deleteItems(linksWeb, linksDB);
                addItems(linksWeb, linksDB, driver, subCatName);
            }
            //switching to base page, to avoid staleElementException
            driver.get("https://www.superlift.com/");
            waitBy = By.id("body");
            while (true){
                try {
                    SileniumUtil.waitForElement(waitBy, driver);
                    break;
                }
                catch (TimeoutException e){
                   logger.error("waiting for category to load");
                    driver.get("https://www.superlift.com/");
            }
            }
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
        List<ItemGroup> deletedItems = statistics.getDeletedGroups();
        for (String dbLink: linksDB ){
            if (!linksWeb.contains(dbLink)){
                ItemGroup deletedItem = SuperliftDAO.markItemDeleted(dbLink);
                deletedItems.add(deletedItem);
            }
        }
    }

    private void addItems(List<String> linksWeb, List<String> linksDB, WebDriver driver, String subCatName) {
        Statistics statistics = getStatistics();
        List<ItemGroup> addedItems = statistics.getAddedGroups();
        for (String webLink: linksWeb){
            if (!linksDB.contains(webLink)){
                ItemGroup item = new ItemGroup();
                item.setStatus("ACTIVE");
                item.setSubCatName(subCatName);
                item.setGroupUrl(webLink);
                SuperliftDAO.saveItemGroup(item);
                addedItems.add(item);
                logger.info("New ItemGroup added: " + webLink);
            }
        }
    }

    private void getItemPage(WebDriver driver, String webLink) {
        driver.get(webLink);
        //impl
    }

}
