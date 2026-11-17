package demo.wrappers;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    public static void clickElement(WebDriver driver, By locator) {
        driver.findElement(locator).click();
    }

    public static void enterText(WebDriver driver, By locator, String text){
        driver.findElement(locator).sendKeys(text);
    }

    public static String getText(WebDriver driver, By locator){

        return driver.findElement(locator).getText();

    }

    public static void pressEnter(WebDriver driver, By locator){
        driver.findElement(locator).sendKeys(Keys.ENTER);
    }
}

