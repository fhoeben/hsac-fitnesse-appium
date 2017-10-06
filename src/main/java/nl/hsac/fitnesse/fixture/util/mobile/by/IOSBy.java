package nl.hsac.fitnesse.fixture.util.mobile.by;

import io.appium.java_client.ios.IOSElement;
import nl.hsac.fitnesse.fixture.util.selenium.by.HeuristicBy;
import nl.hsac.fitnesse.fixture.util.selenium.by.XPathBy;
import org.openqa.selenium.By;

public class IOSBy {
    private static final String CONTAINS_EXACT = "[@name='%1$s' " +
            "or @hint='%1$s' " +
            "or @label='%1$s' " +
            "or @value='%1$s']";

    private static final String CONTAINS_PARTIAL = "[contains(@name,'%1$s') " +
            "or contains(@hint,'%1$s') " +
            "or contains(@label,'%1$s') " +
            "or contains(@value,'%1$s')]";

    public static By exactText(String text) {
        return new XPathBy(".//*" + CONTAINS_EXACT, text);
    }

    public static By partialText(String text) {
        return new XPathBy(".//*" + CONTAINS_PARTIAL, text);
    }

    public static By exactButtonText(String text) {
        return new XPathBy(".//XCUIElementTypeButton" + CONTAINS_EXACT, text);
    }

    public static By partialButtonText(String text) {
        return new XPathBy(".//XCUIElementTypeButton" + CONTAINS_PARTIAL, text);
    }

    public static HeuristicBy<IOSElement> heuristic(String text) {
        return new HeuristicBy<>(exactText(text), partialText(text));
    }

    public static HeuristicBy<IOSElement> buttonHeuristic(String text) {
        return new HeuristicBy<>(exactButtonText(text), partialButtonText(text));
    }
}
