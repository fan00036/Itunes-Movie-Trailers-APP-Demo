package com.algonquincollege.fan00036.itunesmovietrailers.util;

/**
 * Useful String Utilities.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */
public final class StringUtils {

    /**
     * Return the nth occurrence of needle in text.
     *
     * @param text the String
     * @param needle the character to search
     * @param n the nth occurence
     * @return the index position if found; otherwise, -1
     */
    public static int nthIndexOf( String text, char needle, int n ) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == needle) {
                n--;
                if (n == 0) return i;
            }
        }
        return -1;
    }
}
