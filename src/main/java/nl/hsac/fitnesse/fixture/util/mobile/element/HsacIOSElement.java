package nl.hsac.fitnesse.fixture.util.mobile.element;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import nl.hsac.fitnesse.fixture.util.selenium.caching.BooleanCache;
import nl.hsac.fitnesse.fixture.util.selenium.caching.ObjectCache;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HsacIOSElement extends IOSElement {
    private BooleanCache isSelectedCache;
    private final BooleanCache isDisplayedCache = new BooleanCache(super::isDisplayed);
    private BooleanCache isEnabledCache;
    private ObjectCache<String> tagNameCache;
    private ObjectCache<String> textCache;
    private ObjectCache<Point> locationCache;
    private ObjectCache<Dimension> sizeCache;
    private ObjectCache<Rectangle> rectCache;

    private Map<String, ObjectCache<String>> attributesCache;
    private Function<String, ObjectCache<String>> attributeCacheCreationFunction;

    private Map<String, ObjectCache<String>> cssValuesCache;
    private Function<String, ObjectCache<String>> cssCacheCreationFunction;

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
    public boolean isSelected() {
        if (isSelectedCache == null) {
            isSelectedCache = new BooleanCache(super::isSelected);
        }
        return isSelectedCache.getBooleanValue();
    }

    @Override
    public boolean isDisplayed() {
        return isDisplayedCache.getBooleanValue();
    }

    @Override
    public boolean isEnabled() {
        if (isEnabledCache == null) {
            isEnabledCache = new BooleanCache(super::isEnabled);
        }
        return isEnabledCache.getBooleanValue();
    }

    @Override
    public String getTagName() {
        if (tagNameCache == null) {
            tagNameCache = new ObjectCache<>(super::getTagName);
        }
        return tagNameCache.getValue();
    }

    @Override
    public String getText() {
        if (textCache == null) {
            textCache = new ObjectCache<>(super::getText);
        }
        return textCache.getValue();
    }

    @Override
    public Point getLocation() {
        if (locationCache == null) {
            locationCache = new ObjectCache<>(super::getLocation);
        }
        return locationCache.getValue();
    }

    @Override
    public Dimension getSize() {
        if (sizeCache == null) {
            sizeCache = new ObjectCache<>(super::getSize);
        }
        return sizeCache.getValue();
    }

    @Override
    public Rectangle getRect() {
        if (rectCache == null) {
            rectCache = new ObjectCache<>(super::getRect);
        }
        return rectCache.getValue();
    }

    @Override
    public String getAttribute(String name) {
        if (attributesCache == null) {
            attributeCacheCreationFunction = x -> new ObjectCache<>(() -> super.getAttribute(x));
            attributesCache = new HashMap<>();
        }
        ObjectCache<String> cache = attributesCache.computeIfAbsent(name, attributeCacheCreationFunction);
        return cache.getValue();
    }

    @Override
    public String getCssValue(String propertyName) {
        if (cssValuesCache == null) {
            cssCacheCreationFunction = x -> new ObjectCache<>(() -> super.getCssValue(x));
            cssValuesCache = new HashMap<>();
        }
        ObjectCache<String> cache = cssValuesCache.computeIfAbsent(propertyName, cssCacheCreationFunction);
        return cache.getValue();
    }
}
