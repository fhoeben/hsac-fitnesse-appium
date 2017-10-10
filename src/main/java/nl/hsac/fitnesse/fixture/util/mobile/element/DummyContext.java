package nl.hsac.fitnesse.fixture.util.mobile.element;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Normal 'setFoundBy' logic in RemoteWebDriver calls toString() on SearchContext. For AppiumDriver and its subclasses
 * that means 2 remote calls getting all session details.
 * This context can be used instead as 'foundBy-provider' which just returns a String without any remote calls needed.
 */
public class DummyContext implements SearchContext {
    private final String displayName;

    DummyContext(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public <T extends WebElement> List<T> findElements(By by) {
        return null;
    }

    @Override
    public <T extends WebElement> T findElement(By by) {
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
