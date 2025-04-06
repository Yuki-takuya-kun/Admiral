package io.github.admiral.utils;

/** Trace formatter.*/
public class TraceFormatter {
    public static String formatHeader(String... elems){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elems.length; i++) {
            if (i > 0) sb.append("- [%s]".formatted(elems[i]));
            else sb.append("[%s]".formatted(elems[i]));
        }
        return sb.toString();
    }
}
