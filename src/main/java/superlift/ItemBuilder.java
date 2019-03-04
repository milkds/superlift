package superlift;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import superlift.entities.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private static final Logger logger = LogManager.getLogger(ItemBuilder.class.getName());

    public static SuperLiftItem buildItem(WebDriver driver) {
        SuperLiftItem item = new SuperLiftItem();

        String title = getTitle(driver);
        String wtf = getWTF(driver);
        String include = getInclude(driver);
        String partNo = getPartNo(driver);
        BigDecimal price = getPrice(driver);
        String imgLinks = getImgLinks(driver);
        List<Fitment> fits = getFits(driver);
        String keyDetails = getKeyDetails(driver);
        String notes = getNotes(driver);
        List<WheelData> wheelData = getWheelData(driver, partNo);
        String installInfo = getInstallInfo(driver, item);
        String category = getCategory(title);

        item.setTitle(title);
        item.setWtf(wtf);
        item.setInclude(include);
        item.setPartNo(partNo);
        item.setPrice(price);
        item.setImgLinks(imgLinks);
        item.setFits(fits);
        item.setKeyDetails(keyDetails);
        item.setNotes(notes);
        item.setWheelData(wheelData);
        item.setInstallInfo(installInfo);
        item.setCategory(category);
        item.setItemUrl(driver.getCurrentUrl());
        item.setStatus("ACTIVE");

        logger.info("Item built: " + item);

       /* //log section
        System.out.println("title: " + title);
        System.out.println("What is this: " + wtf);
        System.out.println("What does it include: "+ include);
        System.out.println("Part No: " + partNo);
        System.out.println("Price: "+ price);
        System.out.println("IMG Links " + imgLinks);
        fits.forEach(System.out::println);
        System.out.println("Key details: " + keyDetails);
        System.out.println("Notes: " + notes);
        wheelData.forEach(System.out::println);
        System.out.println("Installation info: " + installInfo);
        System.out.println("Category: " + category);*/


        return item;
    }

    private static String getCategory(String titleStr) {
        if (titleStr.contains("Shock Absorber")){
            return ("Shocks");
        }
        else if (titleStr.contains("U-Bolt")){
            return ("U-Bolts");
        }
        else if (titleStr.contains("Fender Flares")){
            return ("Fender Flares");
        }
        else if (titleStr.contains("Brake Hose")||titleStr.contains("Brakes Hose")){
            return ("Brake Lines");
        }
        else if (titleStr.contains("Rear Block Kit")){
            return ("Rear Lifts");
        }
        else if (titleStr.contains("Sway Bar")){
            return ("Sway bar");
        }
        else if (titleStr.contains("Bushing")){
            return ("Bushings");
        }
        else if (titleStr.contains("Pitman Arm")){
            return ("Pitman Arms");
        }
        else if (titleStr.contains("Leveling Kit")){
            return ("Front lifts and coilovers");
        }
        else if (titleStr.contains("Steering Stabilizer")){
            return ("Steering stabilizers");
        }
        else if (titleStr.contains("Driveshaft")||titleStr.contains("Drive shaft")||titleStr.contains("Drive Shaft")){
            return ("Driveshaft");
        }
        else if (titleStr.contains("Rear Block")){
            return ("Rear Lifts");
        }
        else if (titleStr.startsWith("Rear Add-a-Leafs")){
            return ("Rear Lifts");
        }
        else if (titleStr.startsWith("Front Add-a-Leafs")){
            return ("Front lifts and coilovers");
        }
        else if (titleStr.contains("Bearing Drop Kit")){
            return ("Carrier bearing drop kits");
        }
        else if (titleStr.startsWith("Radius Arms")){
            return ("Control Arms");
        }
        else if (titleStr.contains("Lift Kit")){
            return ("Lift Kits");
        }
        else if (titleStr.contains("Track Bar")){
            return ("Track Bar");
        }
        else if (titleStr.contains("Ctrl Arm")){
            return ("Control Arms");
        }
        else if (titleStr.contains("Shock Boots")){
            return ("Shock Boots");
        }
        else if (titleStr.contains("Steering")){
            return ("Steering parts");
        }
        else if (titleStr.contains("King")){
            return ("Shocks");
        }
        else if (titleStr.contains("Link Kit")){
            return ("Lift Kits");
        }
        else if (titleStr.contains("Shock")){
            return("Shocks");
        }
        else {
            return("Other");
        }
    }


    private static String getInstallInfo(WebDriver driver, SuperLiftItem item) {
        WebElement tabEl = null;
        WebElement instInfoEl = null;
        String tabID = "";

        //Checking if Install Tab is actually present.
        try {
            tabEl = driver.findElement(By.id("installation-notes-tab"));
            tabID = tabEl.getAttribute("aria-controls");
            tabEl.click();
        }
        catch (NoSuchElementException e){
            return "NO INSTALL INFO";
        }

        //activating Install Tab
        while (true){
            try {
                instInfoEl = driver.findElement(By.id(tabID));
                List<WebElement> tmpEls  = instInfoEl.findElements(By.tagName("ul"));
                if (tmpEls.size()>0){
                    break;
                }
                SileniumUtil.sleepForTimeout(100);
            }
            catch (NoSuchElementException e){
                return "NO INSTALL INFO";
            }
        }
        WebElement manualPathEl = null;
        String result = instInfoEl.getText();
        if (result.contains("DOWNLOAD INSTALL GUIDE")){
            result = result.replaceAll("DOWNLOAD INSTALL GUIDE", "");
            try {
                manualPathEl = instInfoEl.findElement(By.tagName("a"));
                item.setInstallGuideLink(manualPathEl.getAttribute("href"));
            }
            catch (NoSuchElementException ignored){
            }
        }

        return result;
    }

    private static List<WheelData> getWheelData(WebDriver driver, String partNo) {
        WebElement tabEl = null;
        WebElement tabInfoEl = null;
        String tabID = "";

        //Checking if Tire&Wheel Tab is actually present.
        try {
            tabEl = driver.findElement(By.id("tire-wheel-info-tab"));
            tabID = tabEl.getAttribute("aria-controls");
            tabEl.click();
        }
        catch (NoSuchElementException e){
            return new ArrayList<>();
        }

        //activating Tire&Wheel Tab
       while (true){
            try {
                tabInfoEl = driver.findElement(By.id(tabID));
                List<WebElement> tmpEls  = tabInfoEl.findElements(By.tagName("tbody"));
                if (tmpEls.size()>0){
                    break;
                }
                SileniumUtil.sleepForTimeout(100);
            }
            catch (NoSuchElementException e){
                return new ArrayList<>();
            }
       }

       //finding Table
       WebElement tableEl = null;
       try {
           tableEl = tabInfoEl.findElement(By.tagName("tbody"));
       }
       catch (NoSuchElementException e){
            logger.error("NO TABLE IN WHEEL INFO AT ITEM " + partNo);
            return new ArrayList<>();
       }
       List<WebElement> rowEls = tableEl.findElements(By.tagName("tr"));
       if (rowEls.size()<=1){
           logger.error("INSUFFICIENT TABLE SIZE AT ITEM " + partNo);
           return new ArrayList<>();
       }

       //getting column names
       WebElement titleEl = rowEls.get(0);
       List<WebElement> colNameEls = titleEl.findElements(By.tagName("td"));

       //iterating table
        List<WheelData> result = new ArrayList<>();
        for (int i = 1; i < rowEls.size(); i++){
            WheelData data = new WheelData();
            data.setItemSku(partNo);
            result.add(data);
        }
        for (int i = 0; i < colNameEls.size() ; i++) {
            String name = colNameEls.get(i).getText();
            name = name.trim();
            switch (name){
                case "Tire":{
                    for (int j = 1; j < rowEls.size(); j++){
                        result.get(j-1).setTire(rowEls.get(j).findElements(By.tagName("td")).get(i).getText());
                    }
                    break;
                }
                case "Wheel":{
                    for (int j = 1; j < rowEls.size(); j++){
                        result.get(j-1).setWheel(rowEls.get(j).findElements(By.tagName("td")).get(i).getText());
                    }
                    break;
                }
                case "Backspacing":{
                    for (int j = 1; j < rowEls.size(); j++){
                        result.get(j-1).setBackspacing(rowEls.get(j).findElements(By.tagName("td")).get(i).getText());
                    }
                    break;
                }
                case "Offset":{
                    for (int j = 1; j < rowEls.size(); j++){
                        result.get(j-1).setOffset(rowEls.get(j).findElements(By.tagName("td")).get(i).getText());
                    }
                    break;
                }
                case "Backspring":{
                    for (int j = 1; j < rowEls.size(); j++){
                        result.get(j-1).setBackspring(rowEls.get(j).findElements(By.tagName("td")).get(i).getText());
                    }
                    break;
                }
                case "Backspacing (INCH)":{
                    for (int j = 1; j < rowEls.size(); j++){
                        result.get(j-1).setBackspacingInch(rowEls.get(j).findElements(By.tagName("td")).get(i).getText());
                    }
                    break;
                }
                case "Offset (MM)":{
                    for (int j = 1; j < rowEls.size(); j++){
                        result.get(j-1).setOffsetMM(rowEls.get(j).findElements(By.tagName("td")).get(i).getText());
                    }
                    break;
                }
                default: logger.error("Unexpected field /"+name+"/ in wheel info at " + driver.getCurrentUrl());
            }
        }

        return result;
    }

    private static String getNotes(WebDriver driver) {
        WebElement noteKeeperEl = null;
        WebElement noteLabelEl = null;
        String noteLabel = "";
        try {
            noteKeeperEl = driver.findElement(By.cssSelector("form[id^='product-add-to-cart-form']"));
            noteKeeperEl = noteKeeperEl.findElement(By.className("product-detail-supplementary-message"));
        }
        catch (NoSuchElementException e){
            return "NO NOTES";
        }
        try {
            noteLabelEl = driver.findElement(By.className("product-detail-supplementary-message-label"));
            noteLabel = noteLabelEl.getAttribute("outerHTML");
        }
        catch (NoSuchElementException ignored){
        }
        String notes = noteKeeperEl.getAttribute("outerHTML");

        return noteLabel+notes;
    }

    private static String getKeyDetails(WebDriver driver) {
        WebElement detailKeepr = null;
        try {
            detailKeepr = driver.findElement(By.id("key-details-tab"));
            String att = detailKeepr.getAttribute("aria-controls");
            detailKeepr = driver.findElement(By.id(att));
        }
        catch (NoSuchElementException e){
            logger.error("No key details at " + driver.getCurrentUrl());
            return "NO KEY DETAILS";
        }
        return detailKeepr.getText();
    }

    private static List<Fitment> getFits(WebDriver driver) {
        List<Fitment> result = new ArrayList<>();
        WebElement fitKeeper = null;
        try {
            fitKeeper = driver.findElement(By.cssSelector("section[class='body-section product-detail-fitment-information-section']"));
        }
        catch (NoSuchElementException e){
            return result;
        }
        if (fitKeeper.getText().equals("Your Selected Product is a universal fitment")){
            return result;
        }
        List<WebElement> fitEls = fitKeeper.findElements(By.tagName("li"));
        fitEls.forEach(fitEl->{
            Fitment fitment = new Fitment();
            String fitString = fitEl.getText();
            if (fitString.length()>5){
                String yearStart = fitString.substring(0, 4);
                String yearFinish = "";
                String carStr = "";
                fitString = fitString.replace(yearStart, "");
                if (fitString.startsWith("-")){
                    yearFinish = fitString.substring(1,5);
                    carStr = fitString.substring(6);
                }
                else {
                    yearFinish = yearStart;
                    carStr = fitString.trim();
                }
                fitment.setCar(carStr);
                try {
                    fitment.setYearStart(Integer.parseInt(yearStart));
                    fitment.setYearFinish(Integer.parseInt(yearFinish));
                }
                catch (NumberFormatException e){
                    logger.error("wrong year format at " + driver.getCurrentUrl());
                }
                result.add(fitment);
            }
        });


        return result;
    }

    private static String getImgLinks(WebDriver driver) {
        StringBuilder linkBuilder = new StringBuilder();
        WebElement tumbnailEl = null;
        try {
            tumbnailEl = driver.findElement(By.cssSelector("div[id^='product-image-thumbnails']"));
        }
        catch (NoSuchElementException e){
            try{
                WebElement imgEl = driver.findElement(By.className("product-detail-images"));
                return imgEl.findElement(By.tagName("img")).getAttribute("src");
            }
            catch (NoSuchElementException e1){
                return "NO IMG";
            }
        }
        List<String> rawImgLinks = new ArrayList<>();
        List<WebElement> imgEls = tumbnailEl.findElements(By.tagName("img"));
        imgEls.forEach(imgEl->{
            rawImgLinks.add(imgEl.getAttribute("src"));
        });

        rawImgLinks.forEach(rawImgLink->{
            linkBuilder.append(rawImgLink.replaceAll("w125_h90","w700_h500"));
            linkBuilder.append(System.lineSeparator());
        });

        int length = linkBuilder.length();
        if (length!=0){
            linkBuilder.setLength(length-2);
        }

        return linkBuilder.toString();
    }

    private static BigDecimal getPrice(WebDriver driver) {
        WebElement priceEl = null;
        String priceStr = "";
        try {
            priceEl = driver.findElement(By.className("product-add-to-cart-form"));
            priceEl = priceEl.findElement(By.className("product-price"));
        }
        catch (NoSuchElementException e){
            return new BigDecimal(0);
        }
        priceStr = priceEl.getText();
        priceStr = StringUtils.substringAfter(priceStr, "$");
        if (priceStr.length()==0){
            return new BigDecimal(0);
        }
        priceStr = priceStr.replaceAll(",","");

        return new BigDecimal(priceStr);
    }

    private static String getPartNo(WebDriver driver) {
        WebElement partEl = null;
        try {
            partEl = driver.findElement(By.className("product-part-number"));
        }
        catch (NoSuchElementException e){
            logger.error("NO PartNo element found at " + driver.getCurrentUrl());
            return "";
        }

        return partEl.getText();
    }

    private static String getInclude(WebDriver driver) {
        List<WebElement> includeEls = driver.findElements(By.className("overview-list"));
        if (includeEls.size()==0){
            return "";
        }
        for (WebElement includeEl: includeEls){
            String txt = includeEl.getText();
            if (txt.startsWith("What does")){
                WebElement infoEl = includeEl.findElement(By.tagName("ul"));
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
               WebElement infoEl = wtfEl.findElement(By.tagName("ul"));
               return infoEl.getText();
           }
       }

        return "";
    }

    private static String getTitle(WebDriver driver) {
        WebElement titleEl = null;
        try {
            titleEl = driver.findElement(By.cssSelector("h1[class='product-detail-section-part-title']"));
        }
        catch (NoSuchElementException e){
            return "";
        }

        return titleEl.getText();
    }
}
