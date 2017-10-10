package nl.hsac.fitnesse.fixture.util.mobile.by;

import io.appium.java_client.android.AndroidElement;
import nl.hsac.fitnesse.fixture.util.selenium.by.XPathBy;
import org.openqa.selenium.By;

public class AndroidBy {
    private static final String CLICKABLE = "[@clickable='true']";

    private static final String CONTAINS_EXACT = "[@enabled='true' " +
            "and (@text='%1$s' " +
            "or @name='%1$s' " +
            "or @content-desc='%1$s' " +
            "or @resource-id='%1$s')]";
    private static final String EXACT_ANY_XPATH = ".//*" + CONTAINS_EXACT;
    private static final String EXACT_CLICKABLE_ANY_XPATH = EXACT_ANY_XPATH + CLICKABLE;
    private static final String EXACT_BUTTON_XPATH = ".//android.widget.Button" + CONTAINS_EXACT;
    private static final String EXACT_CLICKABLE_BUTTON_XPATH = EXACT_BUTTON_XPATH + CLICKABLE;

    private static final String CONTAINS_PARTIAL = "[@enabled='true' " +
            "and (contains(@text,'%1$s') " +
            "or contains(@name,'%1$s') " +
            "or contains(@content-desc,'%1$s') " +
            "or contains(@resource-id,'%1$s'))]";
    private static final String PARTIAL_ANY_XPATH = ".//*" + CONTAINS_PARTIAL;
    private static final String PARTIAL_CLICKABLE_ANY_XPATH = PARTIAL_ANY_XPATH + CLICKABLE;
    private static final String PARTIAL_BUTTON_XPATH = ".//android.widget.Button" + CONTAINS_PARTIAL;
    private static final String PARTIAL_CLICKABLE_BUTTON_XPATH = PARTIAL_BUTTON_XPATH + CLICKABLE;


    public static By exactText(String text) {
        return new XPathBy(EXACT_ANY_XPATH, text);
    }

    public static By partialText(String text) {
        return new XPathBy(PARTIAL_ANY_XPATH, text);
    }

    public static By exactButtonText(String text) {
        return new XPathBy(EXACT_BUTTON_XPATH, text);
    }

    public static By partialButtonText(String text) {
        return new XPathBy(PARTIAL_BUTTON_XPATH, text);
    }

    protected static XPathBy exactClickable(String text) {
        return new XPathBy(EXACT_CLICKABLE_ANY_XPATH, text);
    }

    protected static XPathBy partialClickable(String text) {
        return new XPathBy(PARTIAL_CLICKABLE_ANY_XPATH, text);
    }

    protected static XPathBy exactClickableButton(String text) {
        return new XPathBy(EXACT_CLICKABLE_BUTTON_XPATH, text);
    }

    protected static XPathBy partialClickableButton(String text) {
        return new XPathBy(PARTIAL_CLICKABLE_BUTTON_XPATH, text);
    }

    public static AppiumHeuristicBy<AndroidElement> heuristic(String text) {
        return new AppiumHeuristicBy<>(exactText(text), partialText(text));
    }

    public static AppiumHeuristicBy<AndroidElement> buttonHeuristic(String text) {
        return new AppiumHeuristicBy<>(exactButtonText(text), partialButtonText(text));
    }

    public static AppiumHeuristicBy<AndroidElement> clickableHeuristic(String text) {
        return new AppiumHeuristicBy<>(
                exactClickableButton(text), exactClickable(text),
                partialClickableButton(text), partialClickable(text),
                buttonHeuristic(text), heuristic(text)
        );
    }
}
