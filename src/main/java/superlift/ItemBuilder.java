package superlift;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import superlift.entities.SuperLiftItem;

import java.util.List;

public class ItemBuilder {
    public static SuperLiftItem buildItem(WebDriver driver) {
        String title = getTitle(driver);
        String wtf = getWTF(driver);
        String include = getInclude(driver);
        //impl


        System.out.println(title);
        System.out.println(wtf);
        System.out.println(include);

        return new SuperLiftItem();
    }

    private static String getInclude(WebDriver driver) {
        List<WebElement> includeEls = driver.findElements(By.className("overview-list-title"));
        if (includeEls.size()==0){
            return "";
        }
        for (WebElement includeEl: includeEls){
            String txt = includeEl.getText();
            if (txt.startsWith("What does")){
                By ulBy = By.tagName("ul");
                WebElement infoEl = SileniumUtil.waitForElement(ulBy, driver);
               // WebElement infoEl = includeEl.findElement(By.tagName("ul"));
                return infoEl.getText();
            }
        }
        return "";
    }

    private static String getWTF(WebDriver driver) {
       List<WebElement> wtfEls = driver.findElements(By.className("overview-list"));
       if (wtfEls.size()==0){
           return "";
       }
       for (WebElement wtfEl: wtfEls){
           String txt = wtfEl.getText();
           if (txt.startsWith("What is")){
            //   WebElement infoEl = wtfEl.findElement(By.tagName("ul"));
               By ulBy = By.tagName("ul");
               WebElement infoEl = SileniumUtil.waitForElement(ulBy, driver);
               return infoEl.getText();
           }
       }

        return "";
    }

    private static String getTitle(WebDriver driver) {
        WebElement titleEl = null;
        try {
            titleEl = driver.findElement(By.cssSelector("h2[class='product-detail-element product-detail-title']"));
        }
        catch (NoSuchElementException e){
            return "";
        }

        return titleEl.getText();
    }
}
