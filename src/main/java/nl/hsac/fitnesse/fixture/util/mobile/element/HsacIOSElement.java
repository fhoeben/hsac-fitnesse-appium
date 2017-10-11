package nl.hsac.fitnesse.fixture.util.mobile.element;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import nl.hsac.fitnesse.fixture.util.selenium.caching.BooleanCache;
import nl.hsac.fitnesse.fixture.util.selenium.caching.ObjectCache;
import org.openqa.selenium.SearchContext;

import java.net.URL;


public class HsacIOSElement extends IOSElement {
    private final BooleanCache isDisplayedCache = new BooleanCache(super::isDisplayed);
    private final BooleanCache isEnabledCache = new BooleanCache(super::isEnabled);
    private final ObjectCache<String> tagNameCache = new ObjectCache(super::getTagName);

    @Override
    protected void setFoundBy(SearchContext foundFrom, String locator, String term) {
        if (foundFrom instanceof IOSDriver) {
            URL url = ((IOSDriver) foundFrom).getRemoteAddress();
            super.setFoundBy(new DummyContext("IOSDriver on: " + url), locator, term);
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

    @Override
    public String getTagName() {
        return tagNameCache.getValue();
    }
}
