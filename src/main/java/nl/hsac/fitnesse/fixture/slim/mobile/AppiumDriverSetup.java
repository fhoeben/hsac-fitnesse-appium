package nl.hsac.fitnesse.fixture.slim.mobile;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import nl.hsac.fitnesse.fixture.Environment;
import nl.hsac.fitnesse.fixture.slim.web.SeleniumDriverSetup;
import nl.hsac.fitnesse.fixture.util.mobile.AppiumDriverManager;
import nl.hsac.fitnesse.fixture.util.selenium.driverfactory.DriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Fixture to connect FitNesse to appium.
 */
public class AppiumDriverSetup extends SeleniumDriverSetup {
    private static final String APP_CAPABILITY_NAME = "app";
    private final List<String> secretCapabilities = new ArrayList<>();

    static {
        // ensure our helpers are used for Appium WebDrivers
        DriverManager manager = Environment.getInstance().getSeleniumDriverManager();
        AppiumDriverManager appiumDriverManager = new AppiumDriverManager(manager);
        Environment.getInstance().setSeleniumDriverManager(appiumDriverManager);
    }

    public AppiumDriverSetup() {
        getSecretCapabilities().add("testobject_api_key");
    }

    public boolean connectToAndroidDriverAtWithCapabilities(String url, Map<String, Object> capabilities)
            throws MalformedURLException {
        return createAndSetRemoteWebDriver(AndroidDriver::new, url, new DesiredCapabilities(capabilities));
    }

    public boolean connectToIosDriverAtWithCapabilities(String url, Map<String, Object> capabilities)
            throws MalformedURLException {
        return createAndSetRemoteWebDriver(IOSDriver::new, url, new DesiredCapabilities(capabilities));
    }

    @Override
    protected boolean createAndSetRemoteWebDriver(BiFunction<URL, Capabilities, ? extends RemoteWebDriver> constr,
                                                  String url,
                                                  DesiredCapabilities desiredCapabilities)
            throws MalformedURLException {
        Object appValue = desiredCapabilities.getCapability(APP_CAPABILITY_NAME);
        if (appValue instanceof String) {
            String appLocation = (String) appValue;
            String fullPath = getFilePathFromWikiUrl(appLocation);
            desiredCapabilities.setCapability(APP_CAPABILITY_NAME, fullPath);
        }
        return super.createAndSetRemoteWebDriver(constr, url, desiredCapabilities);
    }

    @Override
    protected String describeCapabilities(RemoteWebDriver remoteDriver) {
        Capabilities capabilities = remoteDriver.getCapabilities();
        Map<String, ?> capaToShow = getCapabilitiesToDescribe(capabilities);
        StringBuilder result = new StringBuilder("<table><tbody>");
        for (Map.Entry<String, ?> entry : capaToShow.entrySet()) {
            result.append("<tr><th>");
            result.append(entry.getKey());
            result.append("</th><td>");
            result.append(entry.getValue());
            result.append("</td></tr>");
        }
        result.append("</tbody></table>");
        return result.toString();
    }

    protected Map<String, ?> getCapabilitiesToDescribe(Capabilities capabilities) {
        List<String> secrets = getSecretCapabilities();

        Map<String, Object> capaToShow = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : capabilities.asMap().entrySet()) {
            String key = entry.getKey();
            if (secrets.contains(key)) {
                capaToShow.put(key, "*****");
            } else {
                capaToShow.put(key, entry.getValue());
            }
        }
        return capaToShow;
    }

    public List<String> getSecretCapabilities() {
        return secretCapabilities;
    }
}
