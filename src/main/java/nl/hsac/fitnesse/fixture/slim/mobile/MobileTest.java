package nl.hsac.fitnesse.fixture.slim.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import nl.hsac.fitnesse.fixture.slim.web.BrowserTest;
import nl.hsac.fitnesse.fixture.util.mobile.AppiumHelper;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import java.time.Duration;
import java.util.function.Function;

/**
 * Specialized class to test mobile applications using Appium.
 */
public class MobileTest<T extends MobileElement, D extends AppiumDriver<T>> extends BrowserTest<T> {
    public MobileTest() {
        super();
    }

    public MobileTest(int secondsBeforeTimeout) {
        super(secondsBeforeTimeout);
    }

    public boolean launchApp() {
        driver().launchApp();
        return true;
    }

    public boolean resetApp() {
        driver().resetApp();
        return true;
    }

    public boolean closeApp() {
        driver().closeApp();
        return true;
    }

    @Override
    public boolean ensureActiveTabIsNotClosed() {
        return true;
    }

    @Override
    protected T getElementToCheckVisibility(String place) {
        T result = getMobileHelper().getElementToCheckVisibility(place);
        return result;
    }

    @Override
    public String savePageSource() {
        String fileName = "xmlView_" + System.currentTimeMillis();
        return savePageSource(fileName, fileName + ".xml");
    }

    @Override
    public boolean scrollTo(String place) {
        return scrollTo(2, 0.5, place, this::getElementToCheckVisibility);
    }

    protected boolean scrollTo(int maxBumps, double swipeDistance, String place, Function<String, T> placeFinder) {
        MobileElement target = placeFinder.apply(place);
        if (target == null) {
            Dimension dimensions;
            Point center;
            MobileElement topScrollable = findByXPath("(//*[@scrollable='true'])[1]");

            System.out.println("Scroll to: " + place);
            int originalImplicitWait = 0;
            if (topScrollable == null) {
                dimensions = driver().manage().window().getSize();
                center = new Point(dimensions.getWidth() / 2, dimensions.getHeight() / 2);
            } else {
                dimensions = topScrollable.getSize();
                center = topScrollable.getCenter();
            }
            double heightDelta = dimensions.getHeight() / 2 * swipeDistance;
            int centerY = center.getY();

            String prevRefTag = null;
            String prevRefText = null;
            Dimension prevRefSize = null;
            Point prevRefLocation = null;

            Double startPos;
            Double endPos;
            int bumps = 0;
            getSeleniumHelper().setImplicitlyWait(50);
            while ((target == null || !target.isDisplayed()) && bumps < maxBumps) {
                System.out.println("Value not yet found, scroll");
                MobileElement refEl = findByXPath("(//*[@scrollable='true']//*[@clickable='true'])[1]");
                boolean sameEl = (null != prevRefTag &&
                        refEl.getTagName().equals(prevRefTag) &&
                        refEl.getText().equals(prevRefText) &&
                        refEl.getSize().equals(prevRefSize) &&
                        refEl.getLocation().equals(prevRefLocation));
                if (bumps > 0 || sameEl) {
                    System.out.println("Going down!");
                    startPos = centerY + heightDelta;
                    endPos = centerY - heightDelta;
                    if (sameEl) {
                        bumps++;
                    }
                } else {
                    System.out.println("Going up!");
                    startPos = centerY - heightDelta;
                    endPos = centerY + heightDelta;
                }
                prevRefTag = refEl.getTagName();
                prevRefText = refEl.getText();
                prevRefSize = refEl.getSize();
                prevRefLocation = refEl.getLocation();
                int scrollStart = startPos.intValue();
                int scrollEnd = endPos.intValue();

                TouchAction swipeList = new TouchAction(driver());
                swipeList.press(center.getX(), scrollStart)
                        .waitAction(Duration.ofMillis(400))
                        .moveTo(0, scrollEnd - scrollStart)
                        .waitAction(Duration.ofMillis(200))
                        .release()
                        .perform();

                target = placeFinder.apply(place);
            }
            getSeleniumHelper().setImplicitlyWait(originalImplicitWait);
        }
        return target != null && target.isDisplayed();
    }

    @Override
    protected T getElement(String place) {
        return super.getElement(place);
    }

    @Override
    protected T getContainerImpl(String container) {
        return getMobileHelper().getContainer(container);
    }

    protected D driver() {
        return getMobileHelper().driver();
    }

    protected AppiumHelper<T, D> getMobileHelper() {
        return (AppiumHelper<T, D>) super.getSeleniumHelper();
    }
}
