package superlift;

import superlift.checkers.JsoupParser;
import org.openqa.selenium.WebDriver;
import superlift.entities.Title;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestClass {


    public static void testDriver(){
        WebDriver driver = SileniumUtil.initDriver();
        SileniumUtil.sleepForTimeout(5000);
        driver.quit();
    }

    public static void getCats(){
       ParseLauncher.launchParse();
    }

    public static void testItemBuild(){
        WebDriver driver = SileniumUtil.initDriver();
        driver.get("https://superlift.com/superide-shock-absorber-12-42-extended-8-22-collapsed");
        SileniumUtil.sleepForTimeout(10000);
        ItemBuilder.buildItem(driver);
        driver.close();
    }

    public static void testItemTitles(){
        List<String> itemUrls = new JsoupParser().getXmlItemUrls();
        int total = itemUrls.size();
        final Integer[] current = {0};
        itemUrls.forEach(url->{
            String titleStr = new JsoupParser().getItemLongTitle(url);
            Title title = new Title();
            title.setTitle(titleStr);
            title.setItemUrl(url);
            System.out.println(title);
            new Thread(() -> SuperliftDAO.saveTitle(title)).start();
            current[0]++;
            System.out.printf("Completed %d of %d total.", current[0], total);
        });
        SileniumUtil.sleepForTimeout(15000);
        HibernateUtil.shutdown();
    }
}
