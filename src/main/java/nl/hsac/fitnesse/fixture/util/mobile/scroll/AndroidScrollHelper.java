package nl.hsac.fitnesse.fixture.util.mobile.scroll;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import nl.hsac.fitnesse.fixture.util.mobile.AndroidHelper;

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
    protected AndroidElement findScrollRefElement() {
        return helper.findByXPath("(.//*[@scrollable='true']//*[@clickable='true'])[1]");
    }
}
