# 실습을 위한 개발 환경 세팅은 맨 아래를 참고하면 된다.
* 이 프로젝트는 '**자바 웹 프로그래밍 Next Step**'을 학습하기 위한 Repository이다.
* 이 책의 저자인 **박재성**님에게 감사의 말씀을 드리고싶다.
---

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* InputStream - InputStreamReader - BufferedReader.
* readLine() 시 해당 행의 데이터를 보여주는 것을 확인했다.

### 요구사항 2 - get 방식으로 회원가입
* 정규표현식 "?" 물음표 구분은 .*[\\?].*로 하면 된다.

### 요구사항 3 - post 방식으로 회원가입
* 문자열 비교 시에는 .equals를 사용한다.

### 요구사항 4 - redirect 방식으로 이동
* HTTP 302 응답을 통해 Redirect를 사용할 수 있다.

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 

# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 **구현 과정을 통해 학습한 내용을 인식하는 것**이 배움에 중요하다. 
