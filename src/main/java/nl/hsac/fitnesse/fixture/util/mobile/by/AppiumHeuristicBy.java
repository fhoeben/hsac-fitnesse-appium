package nl.hsac.fitnesse.fixture.util.mobile.by;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import nl.hsac.fitnesse.fixture.util.selenium.by.HeuristicBy;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Function;

import static nl.hsac.fitnesse.fixture.util.FirstNonNullHelper.firstNonNull;

public class AppiumHeuristicBy<T extends MobileElement> extends HeuristicBy<T> {
  public AppiumHeuristicBy(By firstNested, By... extraNestedBys) {
    super(firstNested, extraNestedBys);
  }

  public AppiumHeuristicBy(Function<? super T, ? extends T> postProcessor, By firstNested, By... extraNestedBys) {
    super(postProcessor, firstNested, extraNestedBys);
  }

  @Override
  public T findElement(SearchContext context) {
    Function<? super T, ? extends T> postProcessor = getPostProcessor();
    List<By> byList = getByList();
    return firstNonNull(by -> postProcessor.apply(getMobileWebElement(by, context)), byList);
  }

  private T getMobileWebElement(By by, SearchContext searchContext) {
    T element;
    if (by instanceof MobileBy.ByAccessibilityId) {
      element = findFirstElementOnly(by, searchContext);
    } else {
      element = getWebElement(by, searchContext);
    }
    return element;
  }

  /**
   * Finds the first element matching the supplied criteria, without retrieving all and checking for their visibility.
   * Searching this way should be faster, when a hit is found. When no hit is found an exception is thrown (and swallowed)
   * which is bad Java practice, but not slow compared to Appium performance.
   * @param by criteria to search
   * @param searchContext context to search in
   * @param <X> expected subclass of WebElement
   * @return <code>null</code> if no match found, first element returned otherwise.
   */
  public static <X extends WebElement> X findFirstElementOnly(By by, SearchContext searchContext) {
    X element = null;
    try {
      element = (X) searchContext.findElement(by);
    } catch (NoSuchElementException e) {
      // ignore, we will return null
    }
    return element;
  }
}
