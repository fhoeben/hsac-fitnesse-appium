package nl.hsac.fitnesse.fixture.util.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import nl.hsac.fitnesse.fixture.util.selenium.SeleniumHelper;
import nl.hsac.fitnesse.fixture.util.selenium.by.BestMatchBy;
import nl.hsac.fitnesse.fixture.util.selenium.driverfactory.DriverFactory;
import nl.hsac.fitnesse.fixture.util.selenium.driverfactory.DriverManager;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Driver manager which creates platform specific SeleniumHelpers.
 */
public class AppiumDriverManager extends DriverManager {
    public AppiumDriverManager(DriverManager manager) {
        DriverFactory factory = manager.getFactory();
        setFactory(factory);
        setDefaultTimeoutSeconds(manager.getDefaultTimeoutSeconds());
    }

    @Override
    protected SeleniumHelper createHelper(WebDriver driver) {
        SeleniumHelper helper;
        if (driver instanceof IOSDriver) {
            helper = new IosHelper();
        } else if (driver instanceof AndroidDriver) {
            helper = new AndroidHelper();
        } else if (driver instanceof AppiumDriver) {
            helper = new AppiumHelper();
        } else {
            helper = super.createHelper(driver);
        }
        if (driver instanceof AppiumDriver) {
            // selecting the 'best macth' should not be done by checking whats on top via Javascript
            BestMatchBy.setBestFunction(this::selectBestElement);
        }
        return helper;
    }

    protected WebElement selectBestElement(SearchContext sc, List<WebElement> elements) {
        WebElement element = elements.get(0);
        if (!element.isDisplayed()) {
            WebElement firstDisplayed = null;
            for (int i = 1; i < elements.size(); i++) {
                WebElement otherElement = elements.get(i);
                if (otherElement.isDisplayed()) {
                    if (firstDisplayed == null) {
                        firstDisplayed = otherElement;
                        break;
                    }
                }
            }
            element = firstDisplayed;
        }
        return element;
    }

}
