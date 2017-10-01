package nl.hsac.fitnesse.fixture.slim.mobile;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import nl.hsac.fitnesse.fixture.Environment;
import nl.hsac.fitnesse.fixture.slim.web.SeleniumDriverSetup;
import nl.hsac.fitnesse.fixture.util.mobile.AppiumDriverManager;
import nl.hsac.fitnesse.fixture.util.selenium.driverfactory.DriverManager;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * Fixture to connect FitNesse to appium.
 */
public class AppiumDriverSetup extends SeleniumDriverSetup {
    static {
        // ensure our helpers are used for Appium WebDrivers
        DriverManager manager = Environment.getInstance().getSeleniumDriverManager();
        AppiumDriverManager appiumDriverManager = new AppiumDriverManager(manager);
        Environment.getInstance().setSeleniumDriverManager(appiumDriverManager);
    }

    public boolean connectToAndroidDriverAtWithCapabilities(String url, Map<String, Object> capabilities)
            throws MalformedURLException {
        return createAndSetRemoteWebDriver(AndroidDriver::new, url, new DesiredCapabilities(capabilities));
    }

    public boolean connectToIosDriverAtWithCapabilities(String url, Map<String, Object> capabilities)
            throws MalformedURLException {
        return createAndSetRemoteWebDriver(IOSDriver::new, url, new DesiredCapabilities(capabilities));
    }
}
