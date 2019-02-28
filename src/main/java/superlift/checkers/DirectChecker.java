package superlift.checkers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import superlift.Statistics;

import java.util.ArrayList;
import java.util.List;

public class DirectChecker extends SubCatChecker {

    public DirectChecker(String categoryID, WebDriver driver, Statistics statistics) {
        super(categoryID, driver, statistics);
    }

    @Override
    public void checkSubCategories() {
        WebDriver driver = getDriver();
        List<String> catLinks = getCatLink(driver);
        checkItemsBySubCategories(catLinks, driver);
    }

    private List<String> getCatLink(WebDriver driver) {
        List<String> result = new ArrayList<>();
        WebElement catEl = driver.findElement(By.id(getCategoryID()));
        WebElement linkEl = catEl.findElement(By.tagName("a"));
        result.add(linkEl.getAttribute("href"));

        return result;
    }
}
