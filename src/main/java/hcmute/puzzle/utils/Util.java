package hcmute.puzzle.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class Util {



    public static Set<String> stringToSet(String str) {
        Set<String> strSet = new HashSet<String>();

        StringTokenizer wordFinder = new StringTokenizer(str, "-");
        // the second argument is a string of the 1 delimiters '-'
        while (wordFinder.hasMoreTokens()) {
            if (strSet.add(wordFinder.nextToken().trim())) {
                // if Author didn't exists
                System.out.println("Add successfully");
            } else {
                // if Author existed
                System.out.println("Author existed");
            }
        }
        return strSet;
    }

    public static String setToString(Set<String> strSet) {
        String str = "";
        for (String setElement : strSet) {
            str = str + "-" + setElement;
        }
        return str;
    }



}