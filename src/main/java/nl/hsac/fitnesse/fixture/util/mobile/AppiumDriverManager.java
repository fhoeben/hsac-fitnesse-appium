package nl.hsac.fitnesse.fixture.util.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import nl.hsac.fitnesse.fixture.util.selenium.SeleniumHelper;
import nl.hsac.fitnesse.fixture.util.selenium.driverfactory.DriverFactory;
import nl.hsac.fitnesse.fixture.util.selenium.driverfactory.DriverManager;
import org.openqa.selenium.WebDriver;

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
        return helper;
    }
}
