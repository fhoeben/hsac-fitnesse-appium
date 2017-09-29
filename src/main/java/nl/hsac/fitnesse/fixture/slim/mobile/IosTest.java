package nl.hsac.fitnesse.fixture.slim.mobile;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import nl.hsac.fitnesse.fixture.util.mobile.IosHelper;

/**
 * Specialized class to test iOS applications using Appium.
 */
public class IosTest extends MobileTest<IOSElement, IOSDriver<IOSElement>> {
    public IosTest() {
        super();
    }

    public IosTest(int secondsBeforeTimeout) {
        super(secondsBeforeTimeout);
    }

    @Override
    protected IosHelper getMobileHelper() {
        return (IosHelper) super.getMobileHelper();
    }
}
