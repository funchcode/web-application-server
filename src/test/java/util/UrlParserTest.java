package util;

import org.junit.Test;

public class UrlParserTest {

    @Test
    public void getURLTest() {
        String input = "GET /index.html HTTP/1.1";

        System.out.println("Answer: "+ UrlParser.getUrl(input));
    }

    @Test
    public void getURLTest_null() {
        String input = "GET /index.html?123 HTTP/1.1";
        System.out.println(UrlParser.getParams(input));
    }

    @Test
    public void urlParserTest() {
        String input = "/index.html";

        //System.out.println(UrlParser.urlParser(input));
    }

    @Test
    public void urlParserTest_물음표() {
        String input = "/index.html?name=jch";
        System.out.println(input.matches(".*[\\?].*"));
        //System.out.println(UrlParser.urlParser(input));
    }

    @Test
    public void getParamTest() {
        String input = "ddd /index.html?name=12 ddd";
        System.out.println(UrlParser.getParams(input));
    }

    @Test
    public void getMethodTest() {
        String input = "GET /index.html?name=12 ddd";
        System.out.println(UrlParser.getMethod(input));
    }


}
