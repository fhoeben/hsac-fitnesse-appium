package nl.hsac.fitnesse.fixture.util.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import nl.hsac.fitnesse.fixture.Environment;
import nl.hsac.fitnesse.fixture.util.mobile.element.MobileElementConverter;
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
            helper = createHelperForIos();
        } else if (driver instanceof AndroidDriver) {
            helper = createHelperForAndroid();
        } else if (driver instanceof AppiumDriver) {
            helper = createGenericAppiumHelper();
        } else {
            helper = super.createHelper(driver);
        }
        if (driver instanceof AppiumDriver) {
            AppiumDriver d = (AppiumDriver) driver;
            setBestFunction(d);
            setMobileElementConverter(d);
        }
        return helper;
    }

    protected void setBestFunction(AppiumDriver d) {
        // selecting the 'best macth' should not be done by checking whats on top via Javascript
        BestMatchBy.setBestFunction(this::selectBestElement);
    }

    protected void setMobileElementConverter(AppiumDriver d) {
        MobileElementConverter converter = createMobileElementConverter(d);
        Environment.getInstance().getReflectionHelper().setField(d, "converter", converter);
    }

    protected MobileElementConverter createMobileElementConverter(AppiumDriver d) {
        return new MobileElementConverter(d, d);
    }

    protected SeleniumHelper createHelperForIos() {
        return new IosHelper();
    }

    protected SeleniumHelper createHelperForAndroid() {
        return new AndroidHelper();
    }

    protected SeleniumHelper createGenericAppiumHelper() {
        return new AppiumHelper();
    }

    protected WebElement selectBestElement(SearchContext sc, List<WebElement> elements) {
        WebElement element = elements.get(0);
        if (!element.isDisplayed()) {
            for (int i = 1; i < elements.size(); i++) {
                WebElement otherElement = elements.get(i);
                if (otherElement.isDisplayed()) {
                    element = otherElement;
                    break;
                }
            }
        }
        return element;
    }

}
