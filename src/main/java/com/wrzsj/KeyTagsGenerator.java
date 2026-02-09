package com.wrzsj;

import java.util.*;

public class KeyTagsGenerator {
    // maybe has a math method
    public static List<String> createTagsTree(int targetCount, String keys) {
        List<String> res = new ArrayList<>();
        recur(targetCount, keys, "", res);
        return res;
    }

    private static void recur(int targetCount, String keys, String prefix, List<String> res) {
        int keysLen = keys.length();
        Map<Character, Integer> keysCountMap = new HashMap<>();
        char[] chars = keys.toCharArray();
        for (char c : chars) {
            keysCountMap.put(c, 0);
        }

        /* Calculate counts each branch stored. */
        reverseArray(chars);
        int targetsLeft = targetCount;
        int level = 0;
        while (targetsLeft > 0) {
            int childLen = level == 0 ? 1 : keysLen - 1;
            for (char key : chars) {
                keysCountMap.put(key, keysCountMap.get(key) + childLen);
                targetsLeft -= childLen;
                if (targetsLeft <= 0) {
                    keysCountMap.put(key, keysCountMap.get(key) + targetsLeft);
                    break;
                }
            }
            level++;
        }

        /* Create tree ( represent by String array ). */
        reverseArray(chars);
        for (char key : chars) {
            int count = keysCountMap.get(key);
            if (count > 1) {
                recur(count, keys, prefix + key, res);
            } else if (count == 1) {
                res.add(prefix + key);
            }
        }
    }

    private static void reverseArray(char[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            char temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }
}
