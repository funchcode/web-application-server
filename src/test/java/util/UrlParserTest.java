package util;

import org.junit.Test;

public class UrlParserTest {

    @Test
    public void getURLTest() {
        String input = "GET /index.html HTTP/1.1";

        System.out.println("Answer: "+ UrlParser.getURL(input));
    }

    @Test
    public void getURLTest_null() {

    }
}
