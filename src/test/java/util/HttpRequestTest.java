package util;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestTest {
    private String testDirectory = "./src/main/resources/";

    @Before
    public void setRequest() throws Exception {

    }

    @Test
    public void getRequestParserTest() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory+"Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);

        System.out.println(request.getHeader("d"));
    }

    @Test
    public void getParamsTest() {
        String input = "GET /user/create?userId=javajigi&password=password&name=JaeSung HTTP/1.1";
        //System.out.println(HttpRequest.getParam(input));
    }

    @Test
    public void getParameterTest() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory+"Http_POST.txt"));
        HttpRequest request = new HttpRequest(in);
        System.out.println(request.getParameter("userId"));
    }

    @Test
    public void getUrlTest() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory+"Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);
        System.out.println(request.getUrl());
    }

    @Test
    public void getHeaderTest() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory+"Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);
        System.out.println(request.getHeader("Host"));
    }
}
