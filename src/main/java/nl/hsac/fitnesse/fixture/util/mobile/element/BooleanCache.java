package nl.hsac.fitnesse.fixture.util.mobile.element;

import java.util.function.BooleanSupplier;

/**
 * A wrapper around a boolean that is valid for an amount of time before its supplier must be called again.
 */
public class BooleanCache {
    private static long maxCacheAge = 500;
    private static int ageFactor = 3;

    private final BooleanSupplier supplier;
    private boolean cachedValue;
    private long validUntil;

    public BooleanCache(BooleanSupplier supplier) {
        this.supplier = supplier;
    }

    public boolean getValue() {
        long start = System.currentTimeMillis();
        if (validUntil < start) {
            cachedValue = supplier.getAsBoolean();
            long end = System.currentTimeMillis();
            if (ageFactor > 0) {
                long autoAge = (end - start) * ageFactor;
                validUntil = end + autoAge;
            } else {
                validUntil = end + maxCacheAge;
            }
        }
        return cachedValue;
    }

    public static void setMaxCacheAge(long maxCacheAge) {
        BooleanCache.maxCacheAge = maxCacheAge;
    }

    public static long getMaxCacheAge() {
        return maxCacheAge;
    }

    public static int getAgeFactor() {
        return ageFactor;
    }

    public static void setAgeFactor(int ageFactor) {
        BooleanCache.ageFactor = ageFactor;
    }
}
