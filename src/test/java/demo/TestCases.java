package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.regex.*;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.*;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.FileInputStream;
import java.time.Duration;
import java.time.Duration;
import java.util.logging.Level;

import demo.utils.ExcelDataProvider;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases extends ExcelDataProvider { // Lets us read the data
        ChromeDriver driver;

        /*
         * TODO: Write your tests here with testng @Test annotation.
         * Follow `testCase01` `testCase02`... format or what is provided in
         * instructions
         */

        /*
         * Do not change the provided methods unless necessary, they will help in
         * automation and assessment
         */
        @BeforeTest
        public void startBrowser() {
                System.setProperty("java.util.logging.config.file", "logging.properties");

                // NOT NEEDED FOR SELENIUM MANAGER
                // WebDriverManager.chromedriver().timeout(30).setup();

                ChromeOptions options = new ChromeOptions();
                LoggingPreferences logs = new LoggingPreferences();

                logs.enable(LogType.BROWSER, Level.ALL);
                logs.enable(LogType.DRIVER, Level.ALL);
                options.setCapability("goog:loggingPrefs", logs);
                options.addArguments("--remote-allow-origins=*");

                System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

                driver = new ChromeDriver(options);

                driver.manage().window().maximize();
        }

        @Test
        public void testCase01() throws InterruptedException {
                System.out.println("Test Case 01");
                driver.get("https://www.youtube.com/");
                Thread.sleep(1000);

                Assert.assertTrue(driver.getCurrentUrl().contains("youtube.com"));

                JavascriptExecutor js = (JavascriptExecutor) driver;
                WebElement footer = driver.findElement(By.xpath("//div[@id='guide-links-primary']"));

                js.executeScript("window.scrollTo(0,document.body.scrollHeight);");

                Wrappers.clickElement(driver, By.xpath("//a[text()='About']"));

                // driver.findElement(By.xpath("//a[text()='About']")).click();

                Thread.sleep(2000);

                js.executeScript("window.scrollBy(0,300);");

                // WebElement
                // content=driver.findElement(By.xpath("//div[contains(@class,'glue-content')]"));

                String contentText = Wrappers.getText(driver, By.xpath("//div[contains(@class,'glue-content')]"));

                System.out.println(contentText);

                System.out.println("End Testcase 01");

        }

      
      
       @Test
public void testCase02() throws InterruptedException {
    System.out.println("Test Case 02 ");
    SoftAssert assert1 = new SoftAssert();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    driver.get("https://www.youtube.com/");

   
    By moviesMenu = By.xpath(
        "//ytd-guide-entry-renderer[.//yt-formatted-string[contains(normalize-space(.), 'Movies')]] | " +
        "//tp-yt-paper-item[.//yt-formatted-string[contains(normalize-space(.), 'Movies')]]"
    );

    WebElement moviesLink = wait.until(ExpectedConditions.elementToBeClickable(moviesMenu));
    moviesLink.click();

    Thread.sleep(3000);

    JavascriptExecutor js = (JavascriptExecutor) driver;

    By topSellingTitle = By.xpath("//span[normalize-space()='Top selling']");
    WebElement topSellingElement = wait.until(ExpectedConditions.visibilityOfElementLocated(topSellingTitle));
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", topSellingElement);

    By movieCards = By.xpath(
        "//ytd-shelf-renderer[.//span[@id='title' and normalize-space()='Top selling']]//ytd-grid-movie-renderer"
    );

    List<WebElement> movies = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(movieCards, 0));

// Click the right arrow of the shelf until it disappears (scroll to the extreme right)
By nextArrow = By.xpath(
    "//ytd-shelf-renderer[.//span[@id='title' and normalize-space()='Top selling']]" +
    "//button[@aria-label='Next' or contains(@aria-label,'Next')]"
);
for (int i = 0; i < 15; i++) {
    List<WebElement> arrows = driver.findElements(nextArrow);
    if (arrows.isEmpty() || !arrows.get(0).isDisplayed()) break;
    js.executeScript("arguments[0].click();", arrows.get(0));
    Thread.sleep(700);
}

// Re-fetch after scrolling, since more cards may have rendered
movies = driver.findElements(movieCards);

int matureCount = 0;
for (int i = 0; i < movies.size(); i++) {
    WebElement movie = movies.get(i);
    js.executeScript("arguments[0].scrollIntoView({inline: 'center', block: 'nearest'});", movie);
    Thread.sleep(300);

    // textContent works even when the element is off-screen; getText() does not
    String fullText = movie.getAttribute("textContent").replaceAll("\\s+", " ").trim();
   // System.out.println("Movie " + (i + 1) + ": " + fullText);

    // Rating badge: look for a badge whose text is exactly "A"
    boolean isMature = false;
    for (WebElement badge : movie.findElements(By.xpath(".//*[contains(@class,'ytBadgeShapeText') or contains(@class,'badge')]"))) {
        if (badge.getAttribute("textContent").trim().equals("A")) {
            isMature = true;
            break;
        }
    }
    if (isMature) matureCount++;

    // Category check on the whole card text
    boolean hasCategory = fullText.contains("Comedy") || fullText.contains("Animation")
            || fullText.contains("Drama") || fullText.contains("Action")
            || fullText.contains("Thriller") || fullText.contains("Horror")
            || fullText.contains("Romance") || fullText.contains("Sci-Fi");
    assert1.assertTrue(hasCategory, "Movie category not found for movie " + (i + 1) + ": " + fullText);
}

//System.out.println("Movies marked A: " + matureCount + " of " + movies.size());
assert1.assertTrue(matureCount > 0, "No movie in Top selling is marked A for Mature");
}





       @Test
public void testCase03() throws InterruptedException {
    System.out.println("Test Case 03");
    SoftAssert assert1 = new SoftAssert();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    driver.manage().window().maximize();
    driver.get("https://www.youtube.com/");

    // 1. Click Music
    By musicLink = By.xpath("//ytd-guide-entry-renderer[.//yt-formatted-string[normalize-space()='Music']]");
    wait.until(ExpectedConditions.elementToBeClickable(musicLink)).click();

    // 2. Wait for the first section's playlists
    By playlists = By.xpath("(//ytd-rich-shelf-renderer)[1]//yt-lockup-view-model");
    wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(playlists, 0));

    // 3. Rightmost = last item in the section
    List<WebElement> items = driver.findElements(playlists);
    System.out.println("Number of playlists: " + items.size());
    WebElement rightMost = items.get(items.size() - 1);

    // 4. Print name (textContent works even if the item is hidden)
    String name = rightMost.findElement(By.xpath(".//h3")).getAttribute("textContent").trim();
    System.out.println("Rightmost playlist: " + name);

    // 5. Track count, e.g. "50 songs" -> 50
    String text = rightMost.getAttribute("textContent");
    int trackCount = -1;
    java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)\\s*songs?").matcher(text);
    if (m.find()) {
        trackCount = Integer.parseInt(m.group(1));
    }
    System.out.println("Number of tracks: " + trackCount);

    // 6. Soft assert
    assert1.assertTrue(trackCount != -1 && trackCount <= 55,
            "Track count should be <= 50. Actual count: " + trackCount);
    assert1.assertAll();
}
@Test
public void testCase04() throws InterruptedException {

    System.out.println("Test Case 04");

    driver.get("https://www.youtube.com/feed/news_destination");

    SoftAssert assert1 = new SoftAssert();

    Thread.sleep(3000);

    // Locate Latest News Posts
    List<WebElement> newsPosts = driver.findElements(
        By.xpath("(//ytd-rich-section-renderer[.//*[normalize-space()='Latest news posts']]//ytd-rich-item-renderer)[position() <= 3]")
    );

    int totalLikes = 0;

    System.out.println("Number of News Posts: " + newsPosts.size());

    for (int i = 0; i < Math.min(3, newsPosts.size()); i++) {

        WebElement post = newsPosts.get(i);

        // Get post title/body text
        String body = post.getText();

        System.out.println("\nNews Post " + (i + 1));
        System.out.println("Title/Body: " + body);

        // Get likes
        List<WebElement> likesElement = post.findElements(
            By.cssSelector("#vote-count-middle")
        );

        int likes = 0;

        if (!likesElement.isEmpty()) {
            String likeText = likesElement.get(0).getText().replace(",", "").trim();

            if (!likeText.isEmpty()) {
                likes = Integer.parseInt(likeText);
            }
        }

        System.out.println("Likes: " + likes);

        totalLikes = totalLikes + likes;
    }

    System.out.println("\nTotal Likes: " + totalLikes);

    assert1.assertEquals(newsPosts.size() >= 3, true,
        "Less than 3 news posts found");

    assert1.assertAll();
}
   @Test
public void testCase05() throws InterruptedException {

    System.out.println("Test Case 05");

    // Read search items from Excel
    List<String> searchItems = new ArrayList<>();

    try (FileInputStream fis = new FileInputStream(
            "src/test/resources/data.xlsx");
         Workbook workbook = WorkbookFactory.create(fis)) {

        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            searchItems.add(sheet.getRow(i).getCell(0).getStringCellValue());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    JavascriptExecutor js = (JavascriptExecutor) driver;

    for (String item : searchItems) {

        System.out.println("\nSearching for: " + item);

        driver.get("https://www.youtube.com/");

        WebElement searchBox = driver.findElement(By.name("search_query"));
        searchBox.sendKeys(item);
        searchBox.sendKeys(Keys.ENTER);

        Thread.sleep(3000);

        long totalViews = 0;
        Set<String> processedVideos = new HashSet<>();
        int scrollCount = 0;
        int noNewVideos = 0;

        // Scroll until 10 Crore views
        while (totalViews < 100000000 && scrollCount < 50
                && noNewVideos < 5) {

            int previousSize = processedVideos.size();

            List<WebElement> videos = driver.findElements(
                By.xpath("//ytd-video-renderer")
            );

            for (WebElement video : videos) {

                WebElement titleElement = video.findElement(
                    By.xpath(".//a[@id='video-title']")
                );

                String title = titleElement.getAttribute("title");

                if (title == null || title.isEmpty()) {
                    title = titleElement.getText();
                }

                if (processedVideos.contains(title)) {
                    continue;
                }

                List<WebElement> metadata = video.findElements(
                    By.cssSelector("#metadata-line span")
                );

                for (WebElement data : metadata) {

                    String viewText = data.getText().toLowerCase();

                    if (viewText.contains("view")) {

                        long views = convertViews(viewText);

                        totalViews += views;
                        processedVideos.add(title);

                        System.out.println(title + " : " + viewText);

                        break;
                    }
                }
            }

            if (processedVideos.size() == previousSize) {
                noNewVideos++;
            } else {
                noNewVideos = 0;
            }

            System.out.println("Current Total Views: " + totalViews);

            js.executeScript(
                "window.scrollTo(0, document.documentElement.scrollHeight)"
            );

            Thread.sleep(2000);
            scrollCount++;
        }

        System.out.println("Search Item: " + item);
        System.out.println("Total Views: " + totalViews);
        System.out.println("Videos Processed: " + processedVideos.size());

        if (totalViews >= 100000000) {
            System.out.println("Target of 10 Crore reached!");
        } else {
            System.out.println("Could not reach 10 Crore within scroll limit.");
        }
    }
}public long convertViews(String text) {

    text = text.toLowerCase().replace(",", "").trim();

    java.util.regex.Matcher matcher =
        java.util.regex.Pattern.compile(
            "([\\d.]+)\\s*(k|m|b|lakh|lakhs|crore|cr)?"
        ).matcher(text);

    if (!matcher.find()) {
        return 0;
    }

    double number = Double.parseDouble(matcher.group(1));
    String unit = matcher.group(2);

    if (unit == null) return (long) number;

    switch (unit) {
        case "k":
            return (long) (number * 1000);
        case "m":
            return (long) (number * 1000000);
        case "b":
            return (long) (number * 1000000000);
        case "lakh":
        case "lakhs":
            return (long) (number * 100000);
        case "crore":
        case "cr":
            return (long) (number * 10000000);
        default:
            return (long) number;
    }
}
   


        @AfterTest
        public void endTest() {
                driver.close();
                driver.quit();

        }
}