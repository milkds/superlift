package superlift;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import superlift.entities.ItemGroup;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SileniumUtil {

    private static final String SUPERLIFT_URL = "https://superlift.com/";
    private static final Logger logger = LogManager.getLogger(SileniumUtil.class.getName());

    public static WebDriver initDriver(){
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
      //  ChromeOptions options = new ChromeOptions().addArguments("--proxy-server=http://" + "24.225.1.149:8080"); //old
      //  ChromeOptions options = new ChromeOptions().addArguments("--proxy-server=http://" + "212.83.162.199:54321"); //very slow
        ChromeOptions options = new ChromeOptions().addArguments("--proxy-server=http://" + "145.253.253.52:8080"); //for tests

        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);
        driver.get(SUPERLIFT_URL);
        //waiting till page loaded.
        String navByStr = "ul[class='nav navbar-nav mega-menu']";
        try{
            By navBy = By.cssSelector(navByStr);
            waitForElement(navBy, driver);
        }
        catch (NoSuchElementException e){
            logger.error("Check site for changes. No element at " + navByStr);
            driver.quit();
            System.exit(1);
        }
        logger.info("Driver successfully initiated");

        return driver;
    }
    public static WebElement waitForElement(By by, WebDriver driver) {
        return  new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofMillis(2))
                .ignoring(WebDriverException.class)
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }
    public static boolean hasConnection(){
        System.setProperty("http.proxyHost", "24.225.1.149");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "24.225.1.149");
        System.setProperty("https.proxyPort", "8080");
        URL url= null;
        try {
            url = new URL(SUPERLIFT_URL);
            URLConnection con=url.openConnection();
            con.getInputStream();
        } catch (Exception e) {
            logger.error("No connection available");
            return false;
        }

        return true;
    }
    public static void sleepForTimeout(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }


    public static void openItemUrl(WebDriver driver, String url) throws IOException {
        switchToBasicUrl(driver);
        logger.info("Opening item " + url);
        while(true){
            try {
                driver.get(url);
                break;
            }
            catch (TimeoutException e){
             logger.error("timeout while getting page " + url);
             SileniumUtil.sleepForTimeout(5000);
            }
        }

        int attempts = 0;
        while (true){
            try {
                driver.findElement(By.cssSelector("h2[class='product-detail-element product-detail-title']"));
                logger.info("Opened item " + url);
                break;

            }
            catch (NoSuchElementException e){
                attempts++;
                if (attempts==300){
                    if (!hasConnection()){
                        attempts = 0;
                    }
                    else if (driver.getCurrentUrl().equals(SUPERLIFT_URL)) {
                        logger.error("couldn't open url " + url);
                        throw new NotFoundException();
                    }
                    else{
                        String currentUrl = driver.getCurrentUrl();
                        if (url.equals(currentUrl)){
                            break;
                        }
                        logger.error("unexpected problem while getting " + url + " --- " + currentUrl);
                        throw new IOException();
                    }
                }
                SileniumUtil.sleepForTimeout(100);
            }
        }
    }

    private static void switchToBasicUrl(WebDriver driver) {
        int attempts = 0;
        while(true){
            driver.get("https://www.google.com/");
            try {
                WebElement checkEl = new FluentWait<>(driver)
                        .withTimeout(Duration.ofSeconds(30))
                        .pollingEvery(Duration.ofMillis(2))
                        .ignoring(WebDriverException.class)
                        .until(ExpectedConditions.presenceOfElementLocated(By.id("tophf")));
                break;
            }
            catch (TimeoutException e){
              attempts++;
              if (attempts==10){
                  logger.error("google page is not available");
                  break;
              }
            }


        }


    }

    public static List<String> getUrlsFromNewItemGroups(List<ItemGroup> newItemGroups) {
        WebDriver driver = SileniumUtil.initDriver();
        List<String> result = new ArrayList<>();
        newItemGroups.forEach(itemGroup -> {
            String itemGroupUrl = itemGroup.getGroupUrl();
            try {
                openItemUrl(driver, itemGroupUrl);
            } catch (IOException e) {
                return;
            }
            List<String> urlFromItemGroup = getUrlsFromItemGroup(driver);
            switchToBasicUrl(driver);
        });

        driver.close();
        return new ArrayList<>();
    }

    private static List<String> getUrlsFromItemGroup(WebDriver driver) {
       WebElement firstOptionBoxEl = null;
       try {
           firstOptionBoxEl = waitForElement(By.cssSelector("span[class='selectbox vehicle-url-selectbox  default-selected']"), driver);
        }
       catch (TimeoutException e){
           logger.error("couldn't get first searchbox at " +driver.getCurrentUrl());
           return new ArrayList<>();
       }
       /*try {
           firstOptionBoxEl = firstOptionBoxEl.findElement(By.tagName("a"));
       }
       catch (NoSuchElementException e){
           logger.error("couldn't find clickable element at first options box at " + driver.getCurrentUrl());
           return new ArrayList<>();
       }*/
       List<WebElement> options = firstOptionBoxEl.findElements(By.className("selectbox-option"));
       int opSize = options.size();
       if (opSize<2){
           logger.debug(opSize);
           logger.debug(firstOptionBoxEl.getAttribute("innerHTML"));
           logger.error("No available options for first option box at " + driver.getCurrentUrl());
       }
        List<String> parts = new ArrayList<>();
        for (int i = 1; i < opSize ; i++) {
            List<Integer> chooseIndexes = new ArrayList<>();
            chooseIndexes.add(i);
            parts.addAll(browseOptions(driver, chooseIndexes, new ArrayList<>()));
        }

        parts.forEach(part-> logger.debug("part: " + part));

        return new ArrayList<>();
    }

    private static List<String> browseOptions(WebDriver driver, List<Integer> chooseIndexes,  List<String> parts){
        if (partAvailable(driver)){
            parts.add(getPartNo(driver));
            return parts;
        }
        if (vehicleSelected(driver)){
            if (!resetCar(driver)){
                return new ArrayList<>();
            }
        }
        setOptionsByIndexes(driver, chooseIndexes);
        if (partAvailable(driver)){
            parts.add(getPartNo(driver));
            return parts;
        }
        WebElement activeSelectEl = null;
        while (true){
            try {
               // activeSelectEl =  driver.findElement(By.cssSelector("span[class='selectbox   default-selected expanded']"));
                activeSelectEl = getElementLocatedBy(driver, By.cssSelector("span[class='selectbox   default-selected expanded']"));
                break;
            }
            catch (TimeoutException e){
                logger.error("no element at span[class='selectbox   default-selected expanded']");
            }
        }
        logger.debug("active select el found");
        List<WebElement> selectOptions = activeSelectEl.findElements(By.className("selectbox-option"));
        int size = selectOptions.size();
        if (size==0){
            logger.error("No Select options at box " + activeSelectEl.getText() + " at url " + driver.getCurrentUrl());
            return parts;
        }

        for (int i = 1; i < size; i++) {
            List<Integer> newChooses = new ArrayList<>(chooseIndexes);
            newChooses.add(i);
            parts.addAll(browseOptions(driver, newChooses, new ArrayList<>()));
        }

        return parts;
    }

    private static boolean vehicleSelected(WebDriver driver) {
        WebElement selectedVehicleEl = null;
        try {
            selectedVehicleEl = driver.findElement(By.className("product-selected-vehicle"));
        }
        catch (NoSuchElementException e){
            logger.debug("no vehicle selected at " + driver.getCurrentUrl());
            return false;
        }
        return true;
    }

    private static String getPartNo(WebDriver driver) {
        WebElement partEl = null;
        try {
            partEl = driver.findElement(By.className("product-part-number"));
            return partEl.getText();
        }
        catch (NoSuchElementException e){
            logger.error("No partNo available at part element at " + driver.getCurrentUrl());
            return "";
        }
    }

    private static boolean partAvailable(WebDriver driver) {
        try {
            driver.findElement(By.className("product-part-number"));
            return true;
        }
        catch (NoSuchElementException e){
            return false;
        }
    }

    private static void setOptionsByIndexes(WebDriver driver, List<Integer> chooseIndexes) {
       if (!setFirstIndex(driver, chooseIndexes.get(0))){
           return;
       }
        int size = chooseIndexes.size();
        if (size>1){
            for (int i = 1; i < size ; i++) {
                //we make sure this element exists. Otherwie we can't get there
                WebElement selectBoxEl = driver.findElement(By.cssSelector("span[class='selectbox   default-selected expanded']"));
                List<WebElement> options = selectBoxEl.findElements(By.className("selectbox-option"));
                int opSize = options.size();
                int index = chooseIndexes.get(i);
                if (opSize<index||opSize<2){
                  logger.error("unexpected options quantity at " + selectBoxEl.getText()+" at url " + driver.getCurrentUrl());
                  return;
                }
                WebElement optionToSelectEl = options.get(index);
                optionToSelectEl.click();
                logger.debug("clicked on option el");
                //wait 1 sec to make sure option element is not present any more
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                while (true){
                   try {
                        new FluentWait<>(driver)
                               .withTimeout(Duration.ofSeconds(180))
                               .pollingEvery(Duration.ofMillis(100))
                               .ignoring(WebDriverException.class)
                               .until(ExpectedConditions.or(
                                       ExpectedConditions.presenceOfElementLocated(By.className("product-part-number")),
                                       ExpectedConditions.presenceOfElementLocated(By.cssSelector("span[class='selectbox   default-selected expanded']"))));
                        break;
                   }
                   catch (TimeoutException e){
                       if (hasConnection()){
                           logger.error("No option or part element available at " +  selectBoxEl.getText() +"at "+ driver.getCurrentUrl());
                           return;
                       }
                   }
               }
            }
        }
    }

    private static boolean setFirstIndex(WebDriver driver, Integer index) {
        WebElement searchBlockEl = null;
        try {
            searchBlockEl = getElementLocatedBy(driver, By.cssSelector("span[class='selectbox vehicle-url-selectbox  default-selected']"));
            searchBlockEl = searchBlockEl.findElement(By.tagName("a"));
        }
        catch (TimeoutException|NoSuchElementException e){
            logger.error("couldn't find search field at " + driver.getCurrentUrl());
            return false;
        }
        searchBlockEl.click();
       while (true){
           try {
               logger.debug("waiting for element at span[class='selectbox vehicle-url-selectbox  default-selected expanded']");
               searchBlockEl = getElementLocatedBy(driver, By.cssSelector("span[class='selectbox vehicle-url-selectbox  default-selected expanded']"));
               logger.debug("searching for options list at selectbox-option-list");
               searchBlockEl = searchBlockEl.findElement(By.className("selectbox-option-list"));
               break;
           }
           catch (TimeoutException|NoSuchElementException e){
               logger.error("couldn't find search field at " + driver.getCurrentUrl());
           }
       }
        logger.debug("search block element found");
        List<WebElement> options = searchBlockEl.findElements(By.className("selectbox-option"));
        logger.debug("options got");
        int opSize = options.size();
        if (opSize==0||index>=opSize){
            return false;
        }
        options.get(index).click();
        logger.debug("options element clicked");
        while (true){
            try {
                new FluentWait<>(driver)
                        .withTimeout(Duration.ofSeconds(180))
                        .pollingEvery(Duration.ofMillis(100))
                        .ignoring(WebDriverException.class)
                        .until(ExpectedConditions.or(
                                ExpectedConditions.presenceOfElementLocated(By.className("product-part-number")),
                                ExpectedConditions.presenceOfElementLocated(By.cssSelector("span[class='selectbox   default-selected expanded']"))));
                logger.debug("part No or options element found");
                return true;
            }
            catch (TimeoutException e){
                if (hasConnection()){
                    logger.error("couldn't select first value at " + driver.getCurrentUrl());
                    return false;
                }
            }
        }
    }

    private static boolean resetCar(WebDriver driver) {
        WebElement changeCarButton = null;
        try {
            changeCarButton = driver.findElement(By.id("vehicle-search-menu-link"));
        }
        catch (NoSuchElementException e){
            logger.error("Couldn't find car reset button at " + driver.getCurrentUrl());
            return false;
        }
        try {
            changeCarButton.click();
        }
        catch (StaleElementReferenceException e){
            logger.error("Change button element doesn't exist any more at " + driver.getCurrentUrl());
            return false;
        }
        WebElement clearButton = null;
        while (true){
            try {
                clearButton = waitForElement(By.cssSelector("a[class='vehicle-clear btn']"), driver);
                break;
            }
            catch (TimeoutException e){
              if (hasConnection()){
                  logger.error("couldn't find clear car button");
                  return false;
              }
            }
        }
        try {
            clearButton.click();
        }
        catch (StaleElementReferenceException e){
            logger.error("Clear button element doesn't exist any more at " + driver.getCurrentUrl());
            return false;
        }

        while (true){
            try {
                waitForElement(By.cssSelector("span[class='selectbox vehicle-url-selectbox  default-selected']"), driver);
                break;
            }
            catch (TimeoutException e){
                if (hasConnection()){
                    logger.error("couldn't load page after clearance car button clicked at " + driver.getCurrentUrl());
                    return false;
                }
            }
        }

        return true;
    }

    public static WebElement getElementLocatedBy(WebDriver driver, By by){
        return  new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofMillis(2))
                .ignoring(WebDriverException.class)
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public static List<WebElement> waitForSelect(Select select) {
        List<WebElement> options = select.getOptions();
        int retries = 0;
        while (options.size()<2){
            options = select.getOptions();
            try {
                Thread.sleep(100);
            }
            catch (Exception ignored) {
            }
            retries++;
            //we*ve waited for select more than a minute
            if (retries>600){
                if (hasConnection()){
                    logger.error("Requested Select element has no available options.");
                    break;
                }
                else {
                    retries = 0;
                }
            }
        }

        return options;
    }


    private static void codeDump(){
        /*List<WebElement>selectFieldElements = driver.findElements(By.className("qualifier-search-field"));
        int size = selectFieldElements.size();
        if (size<2){
            logger.error("no search fields in search block at " + driver.getCurrentUrl());
            return new ArrayList<>();
        }
        WebElement firstSearchDrop = selectFieldElements.get(1);
        Select select = new Select(firstSearchDrop);
        List<WebElement> firstDropElements = waitForSelect(select);
        size = firstDropElements.size();
        if (size<2){
            logger.error("Couldn't get drop elements for first search field at " + driver.getCurrentUrl());
            return new ArrayList<>();
        }
        logger.debug("got drops from first search field at " + driver.getCurrentUrl());
        for (int i = 1; i < size; i++) {
            logger.debug("selecting option " + firstDropElements.get(i).getText());
            select.selectByIndex(i);
            int selectedFieldsQty = 1;
            int attempts = 0;
            while (true) {
                List<WebElement> selectFieldsEls = driver.findElements(By.className("qualifiers"));
                if (selectFieldsEls.size()==selectedFieldsQty){
                    selectedFieldsQty++;
                    break;
                }
                else {
                    attempts++;
                    if (attempts==5){
                      if (partElementFound(driver)){
                          break;
                      }
                      else {
                          logger.error("couldn't find qualifier Element in select field at " + driver.getCurrentUrl());
                          return new ArrayList<>();
                      }
                    }
                    try {
                        Thread.sleep(10*1000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
        logger.debug("item selected");*/
    }
}
