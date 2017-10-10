package nl.hsac.fitnesse.fixture.util.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import nl.hsac.fitnesse.fixture.util.mobile.by.AppiumHeuristicBy;
import nl.hsac.fitnesse.fixture.util.mobile.scroll.ScrollHelper;
import nl.hsac.fitnesse.fixture.util.selenium.PageSourceSaver;
import nl.hsac.fitnesse.fixture.util.selenium.SeleniumHelper;
import nl.hsac.fitnesse.fixture.util.selenium.by.ConstantBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static nl.hsac.fitnesse.fixture.util.FirstNonNullHelper.firstNonNull;
import static nl.hsac.fitnesse.fixture.util.selenium.by.TechnicalSelectorBy.byIfStartsWith;

/**
 * Specialized helper to deal with appium's web driver.
 */
public class AppiumHelper<T extends MobileElement, D extends AppiumDriver<T>> extends SeleniumHelper<T> {
    private final static Function<String, By> ACCESSIBILITY_BY = byIfStartsWith("accessibility", MobileBy::AccessibilityId);
    private ScrollHelper<T, D> scrollHelper;

    @Override
    public D driver() {
        return (D) super.driver();
    }

    @Override
    public By placeToBy(String place) {
        return firstNonNull(place,
                super::placeToBy,
                ACCESSIBILITY_BY);
    }

    /**
     * Finds the first element matching the supplied criteria, without retrieving all and checking for their visibility.
     * Searching this way should be faster, when a hit is found. When no hit is found an exception is thrown (and swallowed)
     * which is bad Java practice, but not slow compared to Appium performance.
     * @param by criteria to search
     * @return <code>null</code> if no match found, first element returned otherwise.
     */
    public T findFirstElementOnly(By by) {
        return AppiumHeuristicBy.findFirstElementOnly(by, getCurrentContext());
    }

    /**
     * @return app page source, which is expected to be XML not HTML.
     */
    @Override
    public String getHtml() {
        return driver().getPageSource();
    }

    @Override
    public PageSourceSaver getPageSourceSaver(String baseDir) {
        return new PageSourceSaver(baseDir, this) {
            @Override
            protected List<WebElement> getFrames() {
                return Collections.emptyList();
            }

            @Override
            protected String getPageSourceExtension() {
                return "xml";
            }
        };
    }

    @Override
    public void setScriptWait(int scriptTimeout) {
        // Not setting script timeout as Appium does not support it
    }

    @Override
    public void setPageLoadWait(int pageLoadWait) {
        // Not setting page load timeout as Appium does not support it
    }

    @Override
    public Boolean isElementOnScreen(WebElement element) {
        return true;
    }

    @Override
    public T getElementToClick(String place) {
        return findByTechnicalSelectorOr(place, this::getClickBy);
    }

    protected By getClickBy(String place) {
        return ConstantBy.nothing();
    }

    @Override
    public T getElement(String place) {
        return findByTechnicalSelectorOr(place, this::getElementBy);
    }

    protected By getElementBy(String place) {
        return ConstantBy.nothing();
    }

    public T getContainer(String container) {
        Function<String, By> containerBy = this::getContainerBy;
        return findByTechnicalSelectorOr(container, containerBy);
    }

    protected By getContainerBy(String container) {
        return ConstantBy.nothing();
    }

    public T getElementToCheckVisibility(String place) {
        return findByTechnicalSelectorOr(place, this::getElementToCheckVisibilityBy);
    }

    protected By getElementToCheckVisibilityBy(String place) {
        return ConstantBy.nothing();
    }

    @Override
    public void scrollTo(WebElement element) {
        if (!element.isDisplayed()) {
            getScrollHelper().scrollTo(0.5, element.toString(), x -> (T) element);
        }
    }

    public boolean scrollTo(String place) {
        return getScrollHelper().scrollTo(0.5, place, this::getElementToCheckVisibility);
    }

    public ScrollHelper<T, D> getScrollHelper() {
        if (scrollHelper == null) {
            scrollHelper = new ScrollHelper<>(this);
        }
        return scrollHelper;
    }

    public void setScrollHelper(ScrollHelper<T, D> scrollHelper) {
        this.scrollHelper = scrollHelper;
    }

    public TouchAction getTouchAction() {
        return new TouchAction(driver());
    }
}
