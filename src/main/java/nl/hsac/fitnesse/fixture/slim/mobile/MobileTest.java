package nl.hsac.fitnesse.fixture.slim.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import nl.hsac.fitnesse.fixture.slim.web.BrowserTest;
import nl.hsac.fitnesse.fixture.util.mobile.AppiumHelper;

/**
 * Specialized class to test mobile applications using Appium.
 */
public class MobileTest<T extends MobileElement, D extends AppiumDriver<T>> extends BrowserTest<T> {
    public MobileTest() {
        super();
        setImplicitFindInFramesTo(false);
    }

    public MobileTest(int secondsBeforeTimeout) {
        super(secondsBeforeTimeout);
        setImplicitFindInFramesTo(false);
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
        return getMobileHelper().scrollTo(place);
    }

    @Override
    public boolean scrollToIn(String place, String container) {
        return doInContainer(container, () -> scrollTo(place));
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
