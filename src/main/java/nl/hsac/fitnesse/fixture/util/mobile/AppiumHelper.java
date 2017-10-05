package nl.hsac.fitnesse.fixture.util.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import nl.hsac.fitnesse.fixture.util.selenium.PageSourceSaver;
import nl.hsac.fitnesse.fixture.util.selenium.SeleniumHelper;
import nl.hsac.fitnesse.fixture.util.selenium.by.ConstantBy;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static nl.hsac.fitnesse.fixture.util.FirstNonNullHelper.firstNonNull;
import static nl.hsac.fitnesse.fixture.util.selenium.by.TechnicalSelectorBy.byIfStartsWith;

/**
 * Specialized helper to deal with appium's web driver.
 */
public class AppiumHelper<T extends MobileElement, D extends AppiumDriver<T>> extends SeleniumHelper<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final static Function<String, By> ACCESSIBILITY_BY = byIfStartsWith("accessibility", MobileBy::AccessibilityId);

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
            scrollTo(2, 0.5, element.toString(), x -> (T) element);
        }
    }

    public boolean scrollTo(String place) {
        return scrollTo(2, 0.5, place, this::getElementToCheckVisibility);
    }

    public boolean scrollTo(int maxBumps, double swipeDistance, String place, Function<String, T> placeFinder) {
        MobileElement target = placeFinder.apply(place);
        if (target == null) {
            Dimension dimensions;
            Point center;
            MobileElement topScrollable = findByXPath("(.//*[@scrollable='true'])[1]");

            LOGGER.debug("Scroll to: {}", place);
            if (topScrollable == null) {
                dimensions = driver().manage().window().getSize();
                center = new Point(dimensions.getWidth() / 2, dimensions.getHeight() / 2);
            } else {
                dimensions = topScrollable.getSize();
                center = topScrollable.getCenter();
            }
            int heightDelta = Double.valueOf(dimensions.getHeight() / 2 * swipeDistance).intValue();
            int centerX = center.getX();
            int centerY = center.getY();

            String prevRefTag = null;
            String prevRefText = null;
            Dimension prevRefSize = null;
            Point prevRefLocation = null;

            int bumps = 0;
            while ((target == null || !target.isDisplayed()) && bumps < maxBumps) {
                MobileElement refEl = findByXPath("(.//*[@scrollable='true']//*[@clickable='true'])[1]");
                int scrollStart;
                int scrollEnd;
                boolean sameEl = (null != prevRefTag &&
                        refEl.getTagName().equals(prevRefTag) &&
                        refEl.getText().equals(prevRefText) &&
                        refEl.getSize().equals(prevRefSize) &&
                        refEl.getLocation().equals(prevRefLocation));
                if (bumps > 0 || sameEl) {
                    LOGGER.debug("Going down!");
                    scrollStart = centerY + heightDelta;
                    scrollEnd = centerY - heightDelta;
                    if (sameEl) {
                        bumps++;
                    }
                } else {
                    LOGGER.debug("Going up!");
                    scrollStart = centerY - heightDelta;
                    scrollEnd = centerY + heightDelta;
                }
                prevRefTag = refEl.getTagName();
                prevRefText = refEl.getText();
                prevRefSize = refEl.getSize();
                prevRefLocation = refEl.getLocation();

                getTouchAction()
                        .press(centerX, scrollStart)
                        .waitAction(Duration.ofMillis(400))
                        .moveTo(0, scrollEnd - scrollStart)
                        .waitAction(Duration.ofMillis(200))
                        .release()
                        .perform();

                target = placeFinder.apply(place);
            }
        }
        return target != null && target.isDisplayed();
    }

    protected TouchAction getTouchAction() {
        return new TouchAction(driver());
    }
}
