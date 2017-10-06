package nl.hsac.fitnesse.fixture.util.mobile;

import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import nl.hsac.fitnesse.fixture.util.mobile.by.AndroidBy;
import nl.hsac.fitnesse.fixture.util.mobile.scroll.AndroidScrollHelper;
import org.openqa.selenium.By;

import java.util.function.Function;

import static nl.hsac.fitnesse.fixture.util.FirstNonNullHelper.firstNonNull;
import static nl.hsac.fitnesse.fixture.util.selenium.by.TechnicalSelectorBy.byIfStartsWith;

/**
 * Specialized helper to deal with appium's Android web driver.
 */
public class AndroidHelper extends AppiumHelper<AndroidElement, AndroidDriver<AndroidElement>> {
	private static final Function<String, By> ANDROID_UI_AUTOMATOR_BY = byIfStartsWith("uiAutomator", MobileBy::AndroidUIAutomator);

	public AndroidHelper() {
		setScrollHelper(new AndroidScrollHelper(this));
	}

	@Override
	public By placeToBy(String place) {
		return firstNonNull(place,
				super::placeToBy,
				ANDROID_UI_AUTOMATOR_BY);
		}

	@Override
	protected By getElementBy(String place) {
		return AndroidBy.heuristic(place);
	}

	@Override
	protected By getClickBy(String place) {
		return AndroidBy.clickableHeuristic(place);
	}

	@Override
	protected By getContainerBy(String container) {
		return AndroidBy.heuristic(container);
	}

    @Override
    protected By getElementToCheckVisibilityBy(String text) {
        return AndroidBy.partialText(text);
    }
}
