package webserver;

import java.io.*;
import java.net.HttpCookie;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import com.sun.deploy.net.HttpRequest;
import com.sun.tools.internal.ws.wsdl.document.Output;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;
import util.UrlParser;

import javax.xml.crypto.Data;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    DataBase dataBase;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            log.debug("request line : {}", line);

            if(line == null) {
                return;
            }

            String[] tokens = line.split(" ");
            boolean logined = false;
            int contentLength = 0;
            while(!line.equals("")) {
                line = br.readLine();
                if(line.contains("Content-Length")) {contentLength = getContentLength(line);}
                if(line.contains("Cookie")) {logined = isLogin(line);}
                log.debug("header : {}", line);
            }

            String url = tokens[1];
            if(url.equals("/user/create")) {
                int index = url.indexOf("?");
                String queryString = url.substring(index+1);
                Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.debug("User : {}", user);
                DataBase.addUser(user);

                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos, "/index.html");
            } else if(url.endsWith(".css")) {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200CssHeader(dos, body.length);
                responseBody(dos, body);
            } else if("/user/login".equals(url)) {
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryString(body);
                User user = DataBase.findUserById(params.get("userId"));
                if(user == null) {
                    responseResource(out, "/user/login_failed.html");
                    return;
                }

                if(user.getPassword().equals(params.get("password"))) {
                    DataOutputStream dos = new DataOutputStream(out);
                    response320LoginSucceessHeader(dos);
                }else {
                    responseResource(out, "/user/login_failed.html");
                }
            }else if("/user/list".equals(url)) {
                if(!logined) {
                    responseResource(out, "/user/login.html");
                    return;
                }
                Collection<User> users = DataBase.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");
                for(User user : users) {
                    sb.append("<tr>");
                    sb.append("<td>"+user.getUserId()+"</td>");
                    sb.append("<td>"+user.getName()+"</td>");
                    sb.append("<td>"+user.getEmail()+"</td>");
                    sb.append("</tr>");
                }
                sb.append("</table>");
                byte[] body = sb.toString().getBytes();
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            }else {
                responseResource(out, url);
            }

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp"+tokens[1]).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css; \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private boolean isLogin(String line) {
        String[] headerTokens = line.split(":");
        Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String value = cookies.get("logined");
        if(value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    private void response320LoginSucceessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("Content-Length: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private int getContentLength(String line) {
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseCSS200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseLogin200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseLogin400Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=false \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: "+url+"\r\n");
            dos.writeBytes("\r\n");
        }catch (IOException e) {
            log.error(e.getMessage());
        }
    }

//    private void response302Header(DataOutputStream dos) {
//        try {
//            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
//            dos.writeBytes("Location: /index.html \r\n");
//            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
//            dos.writeBytes("\r\n");
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

/*
* try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            String target = bf.readLine();
            String line = bf.readLine();
            String url = UrlParser.getUrl(target);
            String param = null;
            String cookie = null;
            Map<String,String> params = null;
            int contentLength = 0;

            while(!"".equals(line)) {
                System.out.println(line);
                if(line == null) {return;}
                if(line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(" ")[1]);
                }else if(line.startsWith("Cookie")){
                    cookie = line.split(" ")[1];
                }
                line = bf.readLine();
            }

            Boolean isCss = url.endsWith(".css");

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = null;

            if("/user/create".equals(url) && !isCss){
                param = UrlParser.getParams(target);
                if(UrlParser.getMethod(target).equals("POST")){
                    param = IOUtils.readData(bf, contentLength);
                }
                System.out.println(param);
                params = HttpRequestUtils.parseQueryString(param);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                dataBase.addUser(user);
                System.out.println(user.toString());
                response302Header(dos);
                body = Files.readAllBytes(new File("./webapp"+url).toPath());
            }
            else if("/user/login".equals(url) && !isCss) {
                params = HttpRequestUtils.parseQueryString(IOUtils.readData(bf, contentLength));
                User current = dataBase.findUserById(params.get("userId"));
                if(current != null && current.getPassword().equals(params.get("password"))) {
                    System.out.println("로그인 성공 : @@@@@@@@@@@@@@@@@@@@@@@@");
                    body = Files.readAllBytes(new File("./webapp/index.html").toPath());
                    responseLogin200Header(dos, body.length);
                }else {
                    System.out.println("로그인 실패 : @@@@@@@@@@@@@@@@@@@@@@@@");
                    body = Files.readAllBytes(new File("./webapp/user/login_failed.html").toPath());
                    responseLogin400Header(dos, body.length);
                }
            }
            else if("/user/list".equals(url) && !isCss) {
                if(Boolean.parseBoolean(HttpRequestUtils.parseCookies(cookie).get("logined"))){
                    StringBuilder sb = new StringBuilder();
                    DataBase.findAll().stream().forEach(user -> sb.append(user.toString()));
                    body = sb.toString().getBytes();
                    response200Header(dos, body.length);
                }
            }
            else if(isCss) {
                body = Files.readAllBytes(new File("./webapp"+url).toPath());
                responseCSS200Header(dos, body.length);
            }
            else {
                body = Files.readAllBytes(new File("./webapp"+url).toPath());
                response200Header(dos, body.length);
            }

            responseBody(dos, body);

        } catch (IOException e) {
            log.error(e.getMessage());
        }*/