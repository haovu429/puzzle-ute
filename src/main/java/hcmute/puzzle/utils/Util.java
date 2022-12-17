package hcmute.puzzle.utils;

import javax.servlet.http.HttpServletRequest;
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

    public static String getBaseURL(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        StringBuffer url =  new StringBuffer();
        url.append(scheme).append("://").append(serverName);
        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath);
        if(url.toString().endsWith("/")){
            url.append("/");
        }
        return url.toString();
    }

}
