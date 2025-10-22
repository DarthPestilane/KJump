package com.werfad.utils;

import com.intellij.openapi.util.TextRange;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static List<Integer> findAll(String str, char c, boolean ignoreCase) {
        List<Integer> res = new ArrayList<>();
        int index = str.indexOf(c);
        while (index >= 0) {
            res.add(index);
            index = str.indexOf(c, index + 1);
        }
        return res;
    }

    public static List<Integer> findAll(String str, char c) {
        return findAll(str, c, false);
    }

    public static List<Integer> findAll(String str, String find, boolean ignoreCase) {
        List<Integer> res = new ArrayList<>();
        int index = str.indexOf(find);
        while (index >= 0) {
            res.add(index);
            index = str.indexOf(find, index + 1);
        }
        return res;
    }

    public static List<Integer> findAll(String str, String find) {
        return findAll(str, find, false);
    }

    public static TextRange createTextRange(int[] offsets) {
        if (offsets == null || offsets.length != 2) {
            throw new IllegalArgumentException("Offsets array must have exactly 2 elements [start, end]");
        }
        return new TextRange(offsets[0], offsets[1]);
    }

    public static TextRange createTextRange(int start, int end) {
        return new TextRange(start, end);
    }
}