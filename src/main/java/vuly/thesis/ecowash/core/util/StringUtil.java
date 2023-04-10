package vuly.thesis.ecowash.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class StringUtil {

    private static ObjectMapper objectMapper;

    public static void setObjectMapper(ObjectMapper ob){
        objectMapper = ob;
    }

    public static ObjectMapper getObjectMapper(){
        return objectMapper;
    }


    public static String buildRedisKey(String username, String ip){
        return username + "_" + ip;
    }

    public static String getBodyFromRequest(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty();
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String randomAlphanumeric() {
        return RandomStringUtils.randomAlphanumeric(128);
    }

    public static String randomAlphanumeric(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String join(List<BigInteger> values, String delimiter) {
        if (values != null && !values.isEmpty()) {
            List<String> stringList = new ArrayList<>();
            for(BigInteger item : values) {
                stringList.add(String.valueOf(item));
            }

            return String.join(delimiter, stringList);
        }
        return "";
    }

    public static String toUpperCase(String text) {
        return text != null ? text.toUpperCase() : null;
    }
}
