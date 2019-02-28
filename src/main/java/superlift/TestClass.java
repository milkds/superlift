package superlift;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.WebDriver;
import superlift.entities.SuperLiftItem;
import superlift.entities.Title;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class TestClass {


    public static void testItemGroup(){
         ParseLauncher.launchParse(statistics);
         HibernateUtil.shutdown();
    }

    public static void setNotAvailable(){
        List<SuperLiftItem> items = SuperliftDAO.getAllItems();
        items.forEach(item->{
            if (item.getTitle()==null){
              // SuperliftDAO.updatePriceForItem(item.getItemID(), new BigDecimal(0));
            }
        });
        System.out.println("finished");
        HibernateUtil.shutdown();
    }

    public static void testExcel(){
        try {
            ExcelExporter.saveToExcel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    public static void testDriver(){
        WebDriver driver = SileniumUtil.initDriver();
        SileniumUtil.sleepForTimeout(5000);
        driver.quit();
    }

    public static void getCats(){
       ParseLauncher.launchParse(statistics);
    }

    public static void testItemBuild(){
        WebDriver driver = SileniumUtil.initDriver();
      //  driver.get("https://superlift.com/product-detail/k897");
        driver.get("https://superlift.com/product-detail/K445");
       // driver.get("https://superlift.com/product-detail/K931");
       // driver.get("https://superlift.com/product-detail/4310");
       // driver.get("https://superlift.com/product-detail/95030");
        SileniumUtil.sleepForTimeout(10000);
        SuperliftDAO.saveItem(ItemBuilder.buildItem(driver));
        driver.close();
        HibernateUtil.shutdown();
    }

    public static void testItemTitles(){
        List<String> itemUrls = new JsoupParser().getXmlItemUrls();
        List<String> dbItemUrls = SuperliftDAO.getUrlsToReparse();
        itemUrls.removeAll(dbItemUrls);
        itemUrls.remove("https://superlift.com/product-detail/2114");
        itemUrls.remove("https://superlift.com/product-detail/K274");
        itemUrls.remove("https://superlift.com/product-detail/K274B");
        itemUrls.remove("https://superlift.com/product-detail/4600");
        itemUrls.remove("https://superlift.com/product-detail/4040");
        itemUrls.remove("https://superlift.com/product-detail/85231");
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

    public static void testCategoryFromTitle(){
        List<Title> titles = SuperliftDAO.getAllTitles();
        titles.forEach(title->{
            String titleStr = title.getTitle();
            if (titleStr.contains("Shock Absorber")){
                title.setCategory("Shocks");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("U-Bolt")){
                title.setCategory("U-Bolts");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Fender Flares")){
                title.setCategory("Fender Flares");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Brake Hose")||titleStr.contains("Brakes Hose")){
                title.setCategory("Brake Lines");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Rear Block Kit")){
                title.setCategory("Rear Lifts");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Sway Bar")){
                title.setCategory("Sway bar");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Bushing")){
                title.setCategory("Bushings");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Pitman Arm")){
                title.setCategory("Pitman Arms");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Leveling Kit")){
                title.setCategory("Front lifts and coilovers");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Steering Stabilizer")){
                title.setCategory("Steering stabilizers");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Driveshaft")||titleStr.contains("Drive shaft")||titleStr.contains("Drive Shaft")){
                title.setCategory("Driveshaft");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Rear Block")){
                title.setCategory("Rear Lifts");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.startsWith("Rear Add-a-Leafs")){
                title.setCategory("Rear Lifts");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.startsWith("Front Add-a-Leafs")){
                title.setCategory("Front lifts and coilovers");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Bearing Drop Kit")){
                title.setCategory("Carrier bearing drop kits");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.startsWith("Radius Arms")){
                title.setCategory("Control Arms");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Lift Kit")){
                title.setCategory("Lift Kits");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Track Bar")){
                title.setCategory("Track Bar");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Ctrl Arm")){
                title.setCategory("Control Arms");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Shock Boots")){
                title.setCategory("Shock Boots");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Steering")){
                title.setCategory("Steering parts");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("King")){
                title.setCategory("Shocks");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Link Kit")){
                title.setCategory("Lift Kits");
                SuperliftDAO.updateTitleCategory(title);
            }
            else if (titleStr.contains("Shock")){
                title.setCategory("Shocks");
                SuperliftDAO.updateTitleCategory(title);
            }
            else {
                title.setCategory("Other");
                SuperliftDAO.updateTitleCategory(title);
            }
        });
        System.out.println("finished");
    }

    public static void testWrongLinkDr(){
        WebDriver driver = SileniumUtil.initDriver();
        driver.get("https://superlift.com/product-detail/4040");
        SileniumUtil.sleepForTimeout(10000);
        System.out.println(driver.getCurrentUrl());
        driver.quit();
    }

    public static void testPrice(){
       BigDecimal decimal = new JsoupParser().getNewPrice("https://superlift.com/product-detail/k897");
     //   BigDecimal decimal = new JsoupParser().getNewPrice("https://superlift.com/product-detail/4040");
    //    System.out.println(decimal.toPlainString());
        System.out.println(decimal.intValue());
    }
}
