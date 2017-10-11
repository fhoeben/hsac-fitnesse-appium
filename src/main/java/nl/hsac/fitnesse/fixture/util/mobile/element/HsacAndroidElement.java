package nl.hsac.fitnesse.fixture.util.mobile.element;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import nl.hsac.fitnesse.fixture.util.selenium.caching.BooleanCache;
import nl.hsac.fitnesse.fixture.util.selenium.caching.ObjectCache;
import org.openqa.selenium.SearchContext;

import java.net.URL;

public class HsacAndroidElement extends AndroidElement {
    private final BooleanCache isDisplayedCache = new BooleanCache(super::isDisplayed);
    private final BooleanCache isEnabledCache = new BooleanCache(super::isEnabled);
    private final ObjectCache<String> tagNameCache = new ObjectCache(super::getTagName);
    private final ObjectCache<String> textCache = new ObjectCache(super::getText);

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

    @Override
    public String getTagName() {
        return tagNameCache.getValue();
    }

    @Override
    public String getText() {
        return textCache.getValue();
    }
}
