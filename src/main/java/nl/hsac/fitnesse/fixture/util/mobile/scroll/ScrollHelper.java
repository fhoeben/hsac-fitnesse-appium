package nl.hsac.fitnesse.fixture.util.mobile.scroll;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import nl.hsac.fitnesse.fixture.util.mobile.AppiumHelper;
import nl.hsac.fitnesse.fixture.util.mobile.by.IsDisplayedFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

/**
 * Helper to deal with scrolling.
 */
public class ScrollHelper<T extends MobileElement, D extends AppiumDriver<T>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected final AppiumHelper<T, D> helper;

    private Duration waitBetweenScrollPressAndMove = Duration.ofMillis(10);
    private Duration waitAfterMoveDuration = Duration.ofMillis(10);


    public ScrollHelper(AppiumHelper<T, D> helper) {
        this.helper = helper;
    }

    public boolean scrollTo(double swipeDistance, String place, Function<String, ? extends T> placeFinder) {
        T target = placeFinder.apply(place);
        boolean isReached = targetIsReached(target);
        if (!isReached) {
            LOGGER.debug("Scroll to: {}", place);
            Dimension dimensions;
            Point center;
            T topScrollable = findTopScrollable();

            if (topScrollable == null) {
                dimensions = helper.getWindowSize();
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

            Optional<?> prevRef = Optional.empty();

            // counter for hitting top/bottom: 0=no hit yet, 1=hit top, 2=hit bottom
            int bumps = 0;
            while (!isReached && bumps < 2) {
                T refEl = findScrollRefElement(topScrollable);
                Optional<?> currentRef = createHashForElement(refEl);
                if (bumps == 0) {
                    // did not hit top of screen, yet
                    scrollUp(centerX, lowPoint, highPoint);
                } else {
                    scrollDown(centerX, lowPoint, highPoint);
                }

                if (currentRef.equals(prevRef)) {
                    // we either are: unable to find a reference element OR
                    // element remained same, we didn't actually scroll since last iteration
                    // this means we either hit top (if we were going up) or botton (if we were going down)
                    bumps++;
                }
                prevRef = currentRef;
                target = placeFinder.apply(place);
                isReached = targetIsReached(target);
            }
        }
        return isReached;
    }

    protected boolean targetIsReached(T target) {
        return IsDisplayedFilter.mayPass(target);
    }

    protected Optional<?> createHashForElement(T refEl) {
        return refEl != null ? Optional.of(new ElementProperties(refEl)) : Optional.empty();
    }

    protected T findTopScrollable() {
        return helper.findByXPath("(.//*[@scrollable='true' or @type='UIAScrollView'])[1]");
    }

    protected T findScrollRefElement(T topScrollable) {
        T result;
        if (topScrollable == null || !topScrollable.isDisplayed()) {
            result = helper.findByXPath("(.//*[@scrollable='true' or @type='UIAScrollView']//*[@clickable='true' or @type='UIAStaticText'])[1]");
        } else {
            result = helper.findElement(topScrollable, By.xpath("(.//*[@clickable='true' or @type='UIAStaticText'])[1]"));
        }
        return result;
    }

    public void scrollUp(int centerX, int lowPoint, int highPoint) {
        LOGGER.debug("Going up!");
        performScroll(centerX, highPoint, lowPoint);
    }

    public void scrollDown(int centerX, int lowPoint, int highPoint) {
        LOGGER.debug("Going down!");
        performScroll(centerX, lowPoint, highPoint);
    }

    public void performScroll(int centerX, int scrollStart, int scrollEnd) {
        TouchAction ta = helper.getTouchAction()
                .press(PointOption.point(centerX, scrollStart))
                .waitAction(WaitOptions.waitOptions(getWaitBetweenScrollPressAndMove()))
                .moveTo(PointOption.point(0, scrollEnd - scrollStart));

        Duration waitAfterMove = getWaitAfterMoveDuration();
        if (waitAfterMove != null) {
            ta = ta.waitAction(WaitOptions.waitOptions(waitAfterMove));
        }

        ta.release()
            .perform();
    }

    public Duration getWaitBetweenScrollPressAndMove() {
        return waitBetweenScrollPressAndMove;
    }

    public void setWaitBetweenScrollPressAndMove(Duration waitBetweenScrollPressAndMove) {
        this.waitBetweenScrollPressAndMove = waitBetweenScrollPressAndMove;
    }

    public Duration getWaitAfterMoveDuration() {
        return waitAfterMoveDuration;
    }

    public void setWaitAfterMoveDuration(Duration waitAfterMoveDuration) {
        this.waitAfterMoveDuration = waitAfterMoveDuration;
    }

    /**
     * Container for properties of an element that will be compared to determine whether it is considered
     * the same when scrolling.
     */
    protected static class ElementProperties {
        private String tag;
        private Optional<String> text;
        private Dimension size;
        private Point location;

        public ElementProperties(WebElement element) {
            tag = element.getTagName();
            text = Optional.ofNullable(element.getText());
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
