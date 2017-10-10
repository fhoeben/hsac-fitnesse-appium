package nl.hsac.fitnesse.fixture.util.mobile.element;

import java.util.function.BooleanSupplier;

/**
 * A wrapper around a boolean that is valid for an amount of time before its supplier must be called again.
 */
public class BooleanCache {
    private static long maxCacheAge = 500;

    private final BooleanSupplier supplier;
    private boolean cachedValue;
    private long validUntil;

    public BooleanCache(BooleanSupplier supplier) {
        this.supplier = supplier;
    }

    public boolean getValue() {
        if (validUntil < System.currentTimeMillis()) {
            cachedValue = supplier.getAsBoolean();
            validUntil = System.currentTimeMillis() + maxCacheAge;
        }
        return cachedValue;
    }

    public static void setMaxCacheAge(long maxCacheAge) {
        BooleanCache.maxCacheAge = maxCacheAge;
    }

    public static long getMaxCacheAge() {
        return maxCacheAge;
    }
}
