package com.ibm.mcp.zdtp.shared.util;

import java.util.function.Function;
import java.util.function.Supplier;

public final class Record {

    private Record() {}

    public static <T> T nullOr(Supplier<T> getter) {
        try {
            return getter.get();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static <T, R> R nullOrMap(T value, Function<T, R> mapper) {
        if (value == null) return null;
        try {
            return mapper.apply(value);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static String nullOrGet(String value) {
        return value != null ? value : "";
    }

    public static String nullOrEmpty(String value) {
        return value == null ? "" : value;
    }

    public static String nullOrNA(String value) {
        return value != null ? value : "N/A";
    }

    public static Integer nullOrZero(Integer value) {
        return value != null && value > 0 ? value : null;
    }
}