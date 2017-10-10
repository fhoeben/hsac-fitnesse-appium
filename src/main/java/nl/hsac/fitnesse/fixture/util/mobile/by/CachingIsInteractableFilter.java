package nl.hsac.fitnesse.fixture.util.mobile.by;

import nl.hsac.fitnesse.fixture.util.selenium.by.IsInteractableFilter;
import org.openqa.selenium.WebElement;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class CachingIsInteractableFilter<T extends WebElement> implements Function<T, T>, Supplier<T> {
  private T firstFound;

  /**
   * Filters out non-interactable elements.
   *
   * @param webElement element to check.
   * @return webElement if it is interactable, null otherwise.
   */
  @Override
  public T apply(T webElement) {
    if (firstFound == null) {
      firstFound = webElement;
    }
    return mayPass(webElement) ? webElement : null;
  }

  /**
   * @return first non-null element encountered by filter (may or may not be interactable);
   */
  @Override
  public T get() {
    return firstFound;
  }

  private static long lastCheckTs;
  private static Optional<WebElement> lastCheckElem = Optional.empty();
  private static boolean lastCheckResult;

  /**
   * Checks whether element is interactable.
   *
   * @param element element to check.
   * @return true for interactable elements, false otherwise.
   */
  public static boolean mayPass(WebElement element) {
    Optional<WebElement> current = Optional.ofNullable(element);
    if (!lastCheckElem.equals(current) || lastCheckTs < System.currentTimeMillis() - 1000) {
      lastCheckResult = IsInteractableFilter.mayPass(element);
      lastCheckElem = current;
      lastCheckTs = System.currentTimeMillis();
    }
    return lastCheckResult;
  }

}
