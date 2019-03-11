package util;

import com.sun.tools.internal.ws.wsdl.document.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        dos = new DataOutputStream(out);
    }

    void addHeader(String key, String value) {
        try {
            dos.writeBytes(key+": "+value+" \r\n");
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    void forward(String a) {

    }

    void forwardBody(String a) {

    }

    public void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8 \r\n");
            dos.writeBytes("Content-Length: "+ lengthOfBodyContent +" \r\n");
        }catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
            dos.flush();
        }catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    public void sendRedirect(String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: "+ url +"\r\n");
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    void processHeaders() {

    }
}
