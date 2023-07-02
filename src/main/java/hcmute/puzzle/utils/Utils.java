package hcmute.puzzle.utils;

//import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

@Slf4j
public class Utils {



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

    public static Pageable getPageable(Integer page, Integer size) {
        Pageable pageable = Pageable.unpaged();
        if (page != null && size != null) {
            pageable = Pageable.ofSize(size).withPage(page);
        }
        return pageable;
    }

    public static String objectToJson(Object object) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(object);
            return json;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static String fileToBase64(MultipartFile file) {
        try {
            byte[] image = Base64.encodeBase64(file.getBytes(), false);
            String encodedString = new String(image);
            return encodedString;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static String fileToBase64(File file) {
        try {
            byte[] inFileBytes = Files.readAllBytes(Paths.get(file.getPath()));
            byte[] fileContent = java.util.Base64.getEncoder().encode(inFileBytes);
            //			byte[] fileContent = FileUtils.readFileToByteArray(file);
            String encodedString = new String(fileContent);
            return encodedString;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
