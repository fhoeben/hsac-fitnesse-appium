package nl.hsac.fitnesse.fixture.util.mobile.scroll;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import nl.hsac.fitnesse.fixture.util.mobile.AndroidHelper;
import org.openqa.selenium.By;

/**
 * Helper to deal with scrolling for Android.
 */
public class AndroidScrollHelper extends ScrollHelper<AndroidElement, AndroidDriver<AndroidElement>> {
    public AndroidScrollHelper(AndroidHelper helper) {
        super(helper);
    }

    @Override
    protected AndroidElement findTopScrollable() {
        return helper.findByXPath("(.//*[@scrollable='true'])[1]");
    }

    @Override
    protected AndroidElement findScrollRefElement(AndroidElement topScrollable) {
        AndroidElement result;
        if (topScrollable == null || !topScrollable.isDisplayed()) {
            result = helper.findByXPath("(.//*[@scrollable='true']//*[@clickable='true'])[1]");
        } else {
            result = helper.findElement(topScrollable, By.xpath("(.//*[@clickable='true'])[1]"));
        }
        return result;
    }
}
