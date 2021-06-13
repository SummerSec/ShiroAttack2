package com.summersec.attack.deser.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Strings {
    public static String join(Iterable<String> strings, String sep, String prefix, String suffix) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : strings) {
            if (!first) {
                sb.append(sep);
            }
            if (prefix != null) {
                sb.append(prefix);
            }
            sb.append(s);
            if (suffix != null) {
                sb.append(suffix);
            }
            first = false;
        }
        return sb.toString();
    }

    public static String repeat(String str, int num) {
        String[] strs = new String[num];
        Arrays.fill((Object[])strs, str);
        return join(Arrays.asList(strs), "", "", "");
    }

    public static List<String> formatTable(List<String[]> rows) {
        Integer[] maxLengths = new Integer[((String[])rows.get(0)).length];
        for (String[] row : rows) {
            if (maxLengths.length != row.length) throw new IllegalStateException("mismatched columns");
            for (int i = 0; i < maxLengths.length; i++) {
                if (maxLengths[i] == null || maxLengths[i].intValue() < row[i].length()) {
                    maxLengths[i] = Integer.valueOf(row[i].length());
                }
            }
        }

        List<String> lines = new LinkedList<>();
        for (String[] row : rows) {
            for (int i = 0; i < maxLengths.length; i++) {
                String pad = repeat(" ", maxLengths[i].intValue() - row[i].length());
                row[i] = row[i] + pad;
            }
            lines.add(join(Arrays.asList(row), " ", "", ""));
        }
        return lines;
    }

    public static class ToStringComparator implements Comparator<Object> { public int compare(Object o1, Object o2) {
        return o1.toString().compareTo(o2.toString());
    } }

}




