package superlift;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class SileniumUtil {

    private static final String SUPERLIFT_URL = "http://superlift.com";
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
        logger.info("Opening item " + url);
        String currentUrl = driver.getCurrentUrl();
        String title = "";
        if (currentUrl.contains("product-detail")){
            title = driver.findElement(By.cssSelector("h1[class='product-detail-section-part-title']")).getText();
        }
        driver.get(url);
        int attempts = 0;
        while (true){
            try {
                WebElement titleEl = driver.findElement(By.cssSelector("h2[class='product-detail-element product-detail-title']"));
                if (!titleEl.getText().equals(title)){
                    logger.info("Opened item " + url);
                    break;
                }
            }
            catch (NoSuchElementException e){
                attempts++;
                if (attempts==100){
                    if (!hasConnection()){
                        attempts = 0;
                    }
                    else if (driver.getCurrentUrl().equals(SUPERLIFT_URL)) {
                        logger.error("couldn't open url " + url);
                        throw new NotFoundException();
                    }
                    else{
                        logger.error("couldn't open url " + url);
                        throw new IOException();
                    }
                }
                SileniumUtil.sleepForTimeout(100);
            }
        }
    }
}
