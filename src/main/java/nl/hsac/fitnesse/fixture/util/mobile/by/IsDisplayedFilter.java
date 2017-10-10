package nl.hsac.fitnesse.fixture.util.mobile.by;

import org.openqa.selenium.WebElement;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Function to be used as post-processor when finding elements.
 * It will filter out non-visible elements.
 * @param <T> type of element to return.
 */
public class IsDisplayedFilter<T extends WebElement> implements Function<T, T>, Supplier<T> {
    private T firstFound;

    /**
     * Filters out non-displayed elements.
     * @param webElement element to check.
     * @return webElement if it is displayed, null otherwise.
     */
    @Override
    public T apply(T webElement) {
        if (firstFound == null) {
            firstFound = webElement;
        }
        return mayPass(webElement) ? webElement : null;
    }

    /**
     * @return first non-null element encountered by filter (may or may not be displayed);
     */
    @Override
    public T get() {
        return firstFound;
    }

    private static long lastCheckTs;
    private static Optional<WebElement> lastCheckElem = Optional.empty();
    private static boolean lastCheckResult;

    /**
     * Checks whether element is displayed.
     * @param element element to check.
     * @return true for visible elements, false otherwise.
     */
    public static boolean mayPass(WebElement element) {
        Optional<WebElement> current = Optional.ofNullable(element);
        if (!lastCheckElem.equals(current) || lastCheckTs < System.currentTimeMillis() - 500) {
            lastCheckResult = mayPassImpl(element);
            lastCheckElem = current;
            lastCheckTs = System.currentTimeMillis();
        }
        return lastCheckResult;
    }

    /**
     * Checks whether element is displayed, without caching.
     * @param element element to check.
     * @return true for visible elements, false otherwise.
     */
    public static boolean mayPassImpl(WebElement element) {
        return element != null && element.isDisplayed();
    }
}
