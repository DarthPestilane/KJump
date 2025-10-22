package com.werfad.utils;

import com.intellij.openapi.util.TextRange;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static List<Integer> findAll(String str, char c, boolean ignoreCase) {
        List<Integer> res = new ArrayList<>();
        if (ignoreCase) {
            char lowerC = Character.toLowerCase(c);
            char upperC = Character.toUpperCase(c);
            for (int i = 0; i < str.length(); i++) {
                char currentChar = str.charAt(i);
                if (currentChar == lowerC || currentChar == upperC) {
                    res.add(i);
                }
            }
        } else {
            int index = str.indexOf(c);
            while (index >= 0) {
                res.add(index);
                index = str.indexOf(c, index + 1);
            }
        }
        return res;
    }

    public static List<Integer> findAll(String str, char c) {
        return findAll(str, c, false);
    }

    public static List<Integer> findAll(String str, String find, boolean ignoreCase) {
        List<Integer> res = new ArrayList<>();
        if (ignoreCase) {
            String lowerStr = str.toLowerCase();
            String lowerFind = find.toLowerCase();
            int index = lowerStr.indexOf(lowerFind);
            while (index >= 0) {
                res.add(index);
                index = lowerStr.indexOf(lowerFind, index + 1);
            }
        } else {
            int index = str.indexOf(find);
            while (index >= 0) {
                res.add(index);
                index = str.indexOf(find, index + 1);
            }
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