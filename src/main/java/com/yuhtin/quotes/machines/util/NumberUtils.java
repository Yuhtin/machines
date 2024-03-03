package com.yuhtin.quotes.machines.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtils {

    private static final Pattern PATTERN = Pattern.compile("^(\\d+\\.?\\d*)(\\D+)");

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.#");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.##");

    private static final List<String> CURRENCY_FORMAT = Arrays.asList(
            "", "K", "M", "B", "T",
            "Q", "QQ", "S", "SS",
            "O", "N", "D", "UN",
            "DD", "TR", "QT", "QN",
            "SD", "SPD", "OD", "ND",
            "VG", "UVG", "DVG", "TVG",
            "QTV", "QNV", "SEV", "SPV",
            "OVG", "NVG", "TG"
    );

    public static String format(double value) {
        if (isInvalid(value)) return "0";

        int index = 0;

        double tmp;
        while ((tmp = value / 1000) >= 1) {
            if (index + 1 == CURRENCY_FORMAT.size()) break;
            value = tmp;
            ++index;
        }

        return NUMBER_FORMAT.format(value) + CURRENCY_FORMAT.get(index);
    }

    public static double parse(String string) {
        try {

            double value = Double.parseDouble(string);
            return isInvalid(value) ? 0 : value;

        } catch (Exception ignored) {
        }

        Matcher matcher = PATTERN.matcher(string);
        if (!matcher.find()) return 0;

        double amount = Double.parseDouble(matcher.group(1));
        String suffix = matcher.group(2);
        String fixedSuffix = suffix.equalsIgnoreCase("k") ? suffix.toLowerCase() : suffix.toUpperCase();

        int index = CURRENCY_FORMAT.indexOf(fixedSuffix);

        double value = amount * Math.pow(1000, index);
        return isInvalid(value) ? 0 : value;
    }

    public static boolean isInvalid(double value) {
        return value < 0 || Double.isNaN(value) || Double.isInfinite(value);
    }

}