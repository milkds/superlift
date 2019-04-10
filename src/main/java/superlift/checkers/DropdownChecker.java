package superlift.checkers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import superlift.*;

import java.util.ArrayList;
import java.util.List;


public class DropdownChecker extends SubCatChecker {

    private static final Logger logger = LogManager.getLogger(DropdownChecker.class.getName());

    public DropdownChecker(String categoryID, WebDriver driver, Statistics statistics) {
        super(categoryID, driver, statistics);
    }

    @Override
    public void checkSubCategories() {
        WebDriver driver = getDriver();
        waitTillDropOpened(driver);
        List<String> subCatLinks = getSubCatLinks(driver);
        checkItemsBySubCategories(subCatLinks, driver);
    }

    private List<String> getSubCatLinks(WebDriver driver) {
        List<String> subCatLinks = new ArrayList<>();
        WebElement openedDropEl = driver.findElement(By.cssSelector("li[class$='hovered']"));
        List<WebElement> columnEls = openedDropEl.findElements(By.className("sub-item-column"));
        for (WebElement columnEl: columnEls){
            List<WebElement> subCatEls = columnEl.findElements(By.tagName("a"));
            for (WebElement element: subCatEls){
                subCatLinks.add(element.getAttribute("href"));
            }
        }

        return subCatLinks;
    }

    private void waitTillDropOpened(WebDriver driver) {
        //this needed for case, if other drop already opened at the moment.
        try {
            WebElement hoverEl = driver.findElement(By.cssSelector("li[class$='hovered']"));
            hoverEl.click();
            while (true){
                try {
                    hoverEl = driver.findElement(By.cssSelector("li[class$='hovered']"));
                    SileniumUtil.sleepForTimeout(1000);
                }
                catch (NoSuchElementException ed){
                    break;
                }
            }
        }
        catch (NoSuchElementException ignored){
        }


        int attempts = 0;
        while(true){
            WebElement catEl = driver.findElement(By.id(getCategoryID()));
            catEl.click();
            By openCatBy = By.cssSelector("li[class$='hovered']");
            try {
                SileniumUtil.waitForElement(openCatBy, driver);
                break;
            }
            catch(NoSuchElementException|TimeoutException e){
                attempts++;
                if (attempts==10){
                    logger.error("could not open category: " + getCategoryID()+". Check site code.");
                    driver.quit();
                    HibernateUtil.shutdown();
                    System.exit(1);
                }
            }
        }

    }


}
