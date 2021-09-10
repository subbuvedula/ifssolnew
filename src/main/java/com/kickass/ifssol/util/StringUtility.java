package com.kickass.ifssol.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtility {

    public static String makeFirstLetterCap(String val) {
        if (StringUtils.isEmpty(val)) {
            return  val;
        }

        String fistLetter = (val.charAt(0) + "").toUpperCase();
        String remaining = val.substring(1,val.length());
        return fistLetter + remaining;
    }
}
