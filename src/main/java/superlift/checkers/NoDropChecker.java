package superlift.checkers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import superlift.SileniumUtil;
import superlift.Statistics;

import java.util.ArrayList;
import java.util.List;

public class NoDropChecker extends SubCatChecker {

    private static final Logger logger = LogManager.getLogger(NoDropChecker.class.getName());

    public NoDropChecker(String categoryID, WebDriver driver, Statistics statistics) {
        super(categoryID, driver, statistics);
    }

    @Override
    public void checkSubCategories() {
        WebDriver driver = getDriver();
        openCategoryList(driver);
        List<String> subCatLinks = getSubCatLinks(driver);
        checkItemsBySubCategories(subCatLinks, driver);
    }

    private List<String> getSubCatLinks(WebDriver driver) {
        List<String> result = new ArrayList<>();
        List<WebElement> catEls = driver.findElements(By.className("category-tile-wrapper  "));
        for (WebElement catEl: catEls){
            WebElement linkEl = catEl.findElement(By.tagName("a"));
            result.add(linkEl.getAttribute("href"));
        }

        return result;
    }

    private void openCategoryList(WebDriver driver) {
        WebElement catEl = driver.findElement(By.id(getCategoryID()));
        catEl.click();
        By catKeeprBy = By.cssSelector("div[class='category-tiles-outer-wrap row ']");
        WebElement catListKeeprEl = null;
        try {
            catListKeeprEl = SileniumUtil.waitForElement(catKeeprBy, driver);
        }
        catch (NoSuchElementException e){
            logger.error("Couldn't get subcategories list for category id " + getCategoryID());
        }
    }
}
