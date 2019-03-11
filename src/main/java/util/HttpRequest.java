package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Map<String, String> request;
    private Map<String, String> params;

    public HttpRequest(InputStream is) {
        request = new HashMap<>();
        requestParser(is);
    }


    public String getParameter(String key) {
        String paramStr = getParam(request.get("Request-Line"));
        Map<String, String> paramz = new HashMap<>();
        Arrays.stream(paramStr.split("&")).forEach(keyValue-> {
            paramz.put(keyValue.split("=")[0], keyValue.split("=")[1]);
        });
        return paramz.get(key);
    }

    // [Request-Line, Request-Header, Request-Body] 정보 가져오기
    public String getRequest(String key) {
        return request.get(key);
    }

    public String getHeader(String key) {
        Map<String, String> headers = new HashMap<>();
        Arrays.stream(request.get("Request-Header").split(";")).forEach(line -> {
            if(!line.equals("")) {
                headers.put(line.split(": ")[0], line.split(": ")[1]);
            }
        });
        return headers.get(key);
    }

    // GET, POST 구분
    private Boolean isGetMethod() {
        boolean isGet = false;

        if(getMethod().equals("GET")) {
            isGet = true;
        }

        return isGet;
    }

    public String getMethod() {
        return request.get("Request-Line").split(" ")[0];
    }

    // url 가져오기.
    public String getPath() {
        String url = request.get("Request-Line").split(" ")[1];
        if(url.contains("?")) {
            url = url.split("\\?")[0];
        }
        return url;
    }

    // POST, GET body 데이터 가져오기.
    private String getParam(String line) {
        String str = line.split(" ")[1];
        Boolean hasParam = str.contains("?");
        if(!isGetMethod()) {
            str = request.get("Request-Body");
        }
        if(hasParam) {
            str = str.split("\\?")[1];
        }
        return str;
    }

    // 요청 텍스트 분리.
    private Map<String, String> requestParser(InputStream is) {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            request.put("Request-Line", line);
            StringBuilder sb = new StringBuilder();
            while(!line.equals("")){
                line = br.readLine();

                sb.append(line+";");
            }
            request.put("Request-Header", sb.toString());

            if(!isGetMethod()){
                request.put("Request-Body", br.readLine());
            }

        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return request;
    }
}
