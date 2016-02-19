package io.github.shadowchild.hrsetup;


import java.util.Locale;

/**
 * Created by Zach Piddock on 05/12/2015.
 */
public class MathUtils {

    private static final String[] SI_UNITS = { "B", "kB", "MB", "GB", "TB", "PB", "EB" };
    private static final String[] BINARY_UNITS = { "B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB" };

    public static Pair<Float, String> humanReadableByteCount(long bytes, boolean useSIUnits, Locale locale) {

        final String[] units = useSIUnits ? SI_UNITS : BINARY_UNITS;
        final int base = useSIUnits ? 1000 : 1024;

        // When using the smallest unit no decimal point is needed, because it's the exact number.
        if(bytes < base) {

            return new Pair<>((float)bytes, units[0]);
        }

        final int exponent = (int)(Math.log(bytes) / Math.log(base));
        final String unit = units[exponent];
        float size = Float.parseFloat(
                String.format(locale, "%.1f", bytes / Math.pow(base, exponent)));
        return new Pair<>(size, unit);
    }

    public static Pair<Float, String> humanReadableByteCount(long bytes, boolean useSIUnits) {

        return humanReadableByteCount(bytes, useSIUnits, Locale.ENGLISH);
    }

    public static Pair<Float, String> humanReadableByteCount(long bytes) {

        return humanReadableByteCount(bytes, true);
    }

    public static float getPercentage(float score, float maxScore) {

        return (score * 100) / maxScore;
    }

    public static float toDecimal(float value) {

        if(value < 100) return value / 100;

        while(value > 1) {

            value /= 10;
        }
        return value;
    }

    public static float percentageAsDecimal(float score, float maxScore) {

        return toDecimal(getPercentage(score, maxScore));
    }
}
