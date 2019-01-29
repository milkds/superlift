package superlift;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import superlift.checkers.DirectChecker;
import superlift.checkers.DropdownChecker;
import superlift.checkers.NoDropChecker;
import superlift.checkers.SubCatChecker;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class ParseLauncher {

    public static void launchParse(){
        Statistics statistics = new Statistics();
        WebDriver driver = SileniumUtil.initDriver();

        List<SubCatChecker> checkers = getCheckers(driver, statistics);
        checkAllSubcats(checkers);

        driver.quit();
    }

    private static void checkAllSubcats(List<SubCatChecker> checkers) {
        for (SubCatChecker checker: checkers){
            checker.checkSubCategories();
        }
    }

    private static List<SubCatChecker> getCheckers(WebDriver driver, Statistics statistics) {
        List<WebElement> categoryEls = driver.findElements(By.cssSelector("li[id^='nav-item-3']"));
        List<SubCatChecker> checkers = new ArrayList<>();
        for (WebElement categoryEl: categoryEls){
            String catID = categoryEl.getAttribute("id");
            if (directCat(categoryEl)){
                checkers.add(new DirectChecker(catID, driver, statistics));
            }
            else if (dropDown(categoryEl)){
              //  checkers.add(new DropdownChecker(catID, driver, statistics));
            }
            else {
                checkers.add(new NoDropChecker(catID, driver, statistics));
            }
        }

        return checkers;
    }

    private static boolean dropDown(WebElement categoryEl) {
        String att = categoryEl.getAttribute("class");
        return att.startsWith("mega");
    }

    private static boolean directCat(WebElement categoryEl) {
        String att = categoryEl.getAttribute("class");
        return att.length()==0;
    }
}
