package nl.hsac.fitnesse.fixture.util.mobile.scroll;

import io.appium.java_client.MobileBy;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import nl.hsac.fitnesse.fixture.util.mobile.IosHelper;
import nl.hsac.fitnesse.fixture.util.mobile.by.IOSBy;

/**
 * Helper to deal with scrolling for iOS.
 */
public class IosScrollHelper extends ScrollHelper<IOSElement, IOSDriver<IOSElement>> {

    public IosScrollHelper(IosHelper helper) {
        super(helper);
        setWaitAfterMoveDuration(null);
    }

    @Override
    protected IOSElement findTopScrollable() {
        return helper.findElement(MobileBy.iOSNsPredicateString("type=='XCUIElementTypeScrollView' AND visible==1]"));
    }

    @Override
    protected IOSElement findScrollRefElement(IOSElement topScrollable) {
        IOSElement result;
        if (topScrollable == null || !topScrollable.isDisplayed()) {
            result = helper.findElement(MobileBy.iOSClassChain("**/XCUIElementTypeScrollView[`visible == 1`]/**/XCUIElementTypeStaticText[`visible == 1`][1]"));
        } else {
            result = helper.findElement(topScrollable, new IOSBy.Predicate("type=='XCUIElementTypeStaticText' AND visible==1"));
        }
        return result;
    }
}
