package nl.hsac.fitnesse.fixture.util.mobile.element;

import io.appium.java_client.HasSessionDetails;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.internal.JsonToMobileElementConverter;
import io.appium.java_client.ios.IOSDriver;
import nl.hsac.fitnesse.fixture.util.selenium.caching.CachingRemoteWebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

public class MobileElementConverter extends JsonToMobileElementConverter {
    private final RemoteWebDriver driver;

    public MobileElementConverter(RemoteWebDriver driver, HasSessionDetails hasSessionDetails) {
        super(driver, hasSessionDetails);
        this.driver = driver;
    }

    @Override
    protected RemoteWebElement newRemoteWebElement() {
        // standard newMobileElement will get the session details from server multiple time for each element
        // to get the correct class to instantiate.
        // Lets not do that for iOS and Android
        if (driver instanceof IOSDriver) {
            return createIOSElement();
        } else if (driver instanceof AndroidDriver) {
            return createAndroidElement();
        } else {
            return createOtherNewElement();
        }
    }

    protected RemoteWebElement createIOSElement() {
        return new HsacIOSElement();
    }

    protected RemoteWebElement createAndroidElement() {
        return new HsacAndroidElement();
    }

    protected RemoteWebElement createOtherNewElement() {
        RemoteWebElement newMobileElement = super.newRemoteWebElement();
        if (!(newMobileElement instanceof MobileElement)) {
            newMobileElement = handleNonMobileElement(newMobileElement);
        }
        return newMobileElement;
    }

    protected RemoteWebElement handleNonMobileElement(RemoteWebElement element) {
        return element instanceof CachingRemoteWebElement ? element : new CachingRemoteWebElement(element);
    }
}
