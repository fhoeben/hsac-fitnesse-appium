package nl.hsac.fitnesse.fixture.util.mobile.by;

import io.appium.java_client.android.AndroidElement;
import nl.hsac.fitnesse.fixture.util.selenium.by.HeuristicBy;
import nl.hsac.fitnesse.fixture.util.selenium.by.XPathBy;
import org.openqa.selenium.By;

public class AndroidBy {
    private static final String CONTAINS_EXACT = "[@enabled='true' " +
            "and (@text= '%1$s' " +
            "or @name='%1$s' " +
            "or @content-desc='%1$s' " +
            "or @resource-id='%1$s')]";

    private static final String CONTAINS_PARTIAL = "[@enabled='true' " +
            "and (contains(@text,'%1$s') " +
            "or contains(@name,'%1$s') " +
            "or contains(@content-desc,'%1$s') " +
            "or contains(@resource-id,'%1$s')]";

    public static By exactText(String text) {
        return new XPathBy("//*" + CONTAINS_EXACT, text);
    }

    public static By partialText(String text) {
        return new XPathBy("//*" + CONTAINS_PARTIAL, text);
    }

    public static HeuristicBy<AndroidElement> heuristic(String text) {
        return new HeuristicBy<>(exactText(text), partialText(text));
    }
}
