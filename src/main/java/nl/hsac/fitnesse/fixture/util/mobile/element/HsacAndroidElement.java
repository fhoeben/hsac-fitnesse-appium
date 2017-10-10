package nl.hsac.fitnesse.fixture.util.mobile.element;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.SearchContext;

import java.net.URL;

public class HsacAndroidElement extends AndroidElement {
    private final BooleanCache isDisplayedCache = new BooleanCache(super::isDisplayed);
    private final BooleanCache isEnabledCache = new BooleanCache(super::isEnabled);

    @Override
    protected void setFoundBy(SearchContext foundFrom, String locator, String term) {
        if (foundFrom instanceof AndroidDriver) {
            // standard toString of diver will issue 2 calls to remote server to get session details
            URL url = ((AndroidDriver) foundFrom).getRemoteAddress();
            super.setFoundBy(new DummyContext("AndroidDriver on: " + url), locator, term);
        } else {
            super.setFoundBy(foundFrom, locator, term);
        }
    }

    @Override
    public boolean isDisplayed() {
        return isDisplayedCache.getValue();
    }

    @Override
    public boolean isEnabled() {
        return isEnabledCache.getValue();
    }
}
