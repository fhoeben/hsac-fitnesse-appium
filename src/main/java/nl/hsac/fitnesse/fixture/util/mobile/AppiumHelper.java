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
    protected Duration waitBetweenScrollPressAndMove = Duration.ofMillis(400);
    protected Duration waitBetweenScrollMoveAndRelease = Duration.ofMillis(200);

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
            scrollTo(0.5, element.toString(), x -> (T) element);
        }
    }

    public boolean scrollTo(String place) {
        return scrollTo(0.5, place, this::getElementToCheckVisibility);
    }

    public boolean scrollTo(double swipeDistance, String place, Function<String, T> placeFinder) {
        MobileElement target = placeFinder.apply(place);
        if (target == null) {
            LOGGER.debug("Scroll to: {}", place);
            Dimension dimensions;
            Point center;
            MobileElement topScrollable = findTopScrollable();

            if (topScrollable == null) {
                dimensions = getWindowSize();
                center = new Point(dimensions.getWidth() / 2, dimensions.getHeight() / 2);
            } else {
                dimensions = topScrollable.getSize();
                center = topScrollable.getCenter();
            }
            int centerX = center.getX();

            int heightDelta = Double.valueOf(dimensions.getHeight() / 2 * swipeDistance).intValue();
            int centerY = center.getY();
            int lowPoint = centerY + heightDelta;
            int highPoint = centerY - heightDelta;

            ElementProperties prevRef = null;

            // counter for hitting top/bottom: 0=no hit yet, 1=hit top, 2=hit bottom
            int bumps = 0;
            while ((target == null || !target.isDisplayed()) && bumps < 2) {
                MobileElement refEl = findScrollRefElement();
                ElementProperties currentRef = refEl != null ? new ElementProperties(refEl) : null;
                int scrollStart;
                int scrollEnd;
                if (bumps == 0) {
                    // did not hit top of screen, yet
                    LOGGER.debug("Going up!");
                    scrollStart = highPoint;
                    scrollEnd = lowPoint;
                } else {
                    // hit top already
                    LOGGER.debug("Going down!");
                    scrollStart = lowPoint;
                    scrollEnd = highPoint;
                }
                if (currentRef != null && currentRef.equals(prevRef)) {
                    // element remained same, we didn't actually scroll since last iteration
                    // this means we either hit top (if we were going up) or botton (if we were going down)
                    bumps++;
                }
                performScroll(centerX, scrollStart, scrollEnd);

                prevRef = currentRef;
                target = placeFinder.apply(place);
            }
        }
        return target != null && target.isDisplayed();
    }

    protected MobileElement findTopScrollable() {
        return findByXPath("(.//*[@scrollable='true'])[1]");
    }

    protected T findScrollRefElement() {
        return findByXPath("(.//*[@scrollable='true']//*[@clickable='true'])[1]");
    }

    protected TouchAction performScroll(int centerX, int scrollStart, int scrollEnd) {
        return getTouchAction()
                .press(centerX, scrollStart)
                .waitAction(waitBetweenScrollPressAndMove)
                .moveTo(0, scrollEnd - scrollStart)
                .waitAction(waitBetweenScrollMoveAndRelease)
                .release()
                .perform();
    }

    protected TouchAction getTouchAction() {
        return new TouchAction(driver());
    }

    /**
     * Container for properties of an element that will be compared to determine whether it is considered
     * the same when scrolling.
     */
    protected static class ElementProperties {
        private String tag;
        private String text;
        private Dimension size;
        private Point location;

        public ElementProperties(WebElement element) {
            tag = element.getTagName();
            text = element.getText();
            size = element.getSize();
            location = element.getLocation();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ElementProperties that = (ElementProperties) o;

            if (!tag.equals(that.tag)) return false;
            if (!text.equals(that.text)) return false;
            if (!size.equals(that.size)) return false;
            return location.equals(that.location);
        }

        @Override
        public int hashCode() {
            int result = tag.hashCode();
            result = 31 * result + text.hashCode();
            result = 31 * result + size.hashCode();
            result = 31 * result + location.hashCode();
            return result;
        }
    }
}
