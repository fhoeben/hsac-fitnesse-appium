package nl.hsac.fitnesse.fixture.util.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
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

    protected Duration waitBetweenScrollPressAndMove = Duration.ofMillis(400);

    public ScrollHelper(AppiumHelper<T, D> helper) {
        this.helper = helper;
    }

    public boolean scrollTo(double swipeDistance, String place, Function<String, T> placeFinder) {
        MobileElement target = placeFinder.apply(place);
        if (target == null) {
            LOGGER.debug("Scroll to: {}", place);
            Dimension dimensions;
            Point center;
            MobileElement topScrollable = findTopScrollable();

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
            while ((target == null || !target.isDisplayed()) && bumps < 2) {
                MobileElement refEl = findScrollRefElement();
                Optional<?> currentRef = createHashForElement(refEl);
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
                if (currentRef.equals(prevRef)) {
                    // we either are: unable to find a reference element OR
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

    protected Optional<?> createHashForElement(MobileElement refEl) {
        return refEl != null ? Optional.of(new ElementProperties(refEl)) : Optional.empty();
    }

    protected MobileElement findTopScrollable() {
        return helper.findByXPath("(.//*[@scrollable='true'])[1]");
    }

    protected T findScrollRefElement() {
        return helper.findByXPath("(.//*[@scrollable='true']//*[@clickable='true'])[1]");
    }

    protected TouchAction performScroll(int centerX, int scrollStart, int scrollEnd) {
        return helper.getTouchAction()
                .press(centerX, scrollStart)
                .waitAction(waitBetweenScrollPressAndMove)
                .moveTo(0, scrollEnd - scrollStart)
                .release()
                .perform();
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
