package util;

import java.util.HashMap;
import java.util.Map;

public class UrlParser {

    // URL 추출 메서드
    private static String getParser(String request) {
        return request.split(" ")[1];
    }

    // GET/POST 추출
    public static String getMethod(String request) {
        return request.split(" ")[0];
    }

    // URL 추출
    public static String getUrl(String request) {
        String url = getParser(request);
        if(url.matches(".*[\\?].*")) {
            url = url.split("\\?")[0];
        }
        return url;
    }

    // Param 추출
    public static String getParams(String request) {
        String param = getParser(request);
        if(param.matches(".*[\\?].+")) {
            param = param.split("\\?")[1];
        }
        return param;
    }
}
