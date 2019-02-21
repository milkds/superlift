package superlift;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class SileniumUtil {

    private static final String SUPERLIFT_URL = "https://superlift.com/";
    private static final Logger logger = LogManager.getLogger(SileniumUtil.class.getName());

    public static WebDriver initDriver(){
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions().addArguments("--proxy-server=http://" + "24.225.1.149:8080");
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
        int retries = 0;
        WebElement result = null;
        while (true){
            try {
                WebDriverWait wait = new WebDriverWait(driver, 10);
                result = wait.until(ExpectedConditions.presenceOfElementLocated(by));
                break;
            }
            catch (TimeoutException e){
                retries++;
                if (retries>6){
                   if (hasConnection()){
                       logger.error("No Element in searched location");
                       throw new NoSuchElementException("No Element in searched location");
                   }
                   else {
                       retries=0;
                   }
                }
            }
        }

        return result;
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
        driver.get(url);
        int attempts = 0;
        while (true){
            try {
                driver.findElement(By.cssSelector("h2[class='product-detail-element product-detail-title']"));
                logger.info("Opened item " + url);
                break;

            }
            catch (NoSuchElementException e){
                attempts++;
                if (attempts==60){
                    if (!hasConnection()){
                        attempts = 0;
                    }
                    else if (driver.getCurrentUrl().equals(SUPERLIFT_URL)) {
                        logger.error("couldn't open url " + url);
                        throw new NotFoundException();
                    }
                    else{
                        logger.error("connection problem " + url + " --- " + driver.getCurrentUrl());
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
}
