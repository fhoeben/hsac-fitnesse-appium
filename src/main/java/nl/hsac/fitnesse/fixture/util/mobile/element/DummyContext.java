package nl.hsac.fitnesse.fixture.util.mobile.element;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

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
