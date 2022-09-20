# 블로그 검색 서비스

### API 명세
1. 블로그 검색 - 카카오의 블로그 서비스 API 를 이용하여 키워드를 통해 블로그를 검색합니다.
   * URI : (GET) /api/v1/search/keyword
   * Request Param (query param)
     * query : 검색 키워드
     * sort : 정렬 방식 accuracy (default) / recency
     * page : 페이지 번호 1~50 (default 1)
     * size : 한 페이지에 보여질 문서 수 1~50 (default 10)
   * Response Param
      * totalElements : 총 검색 결과 수
      * pageSize : 현재 페이지 크기
      * pageNumber : 현재 페이지 번호
      * lastItem : 마지막 페이지일 경우 true
      * fromService : 사용된 검색 서비스 표시 Kakao (default) / Naver (Kakao 장애시)
      * documents[] :
        * title : 글 제목
        * contents : 글 요약
        * url : 글 url
        * blogname : 블로그이름
        * thumbnail : 미리보기 이미지 URL
        * datetime : 작성시간 [YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].000+[tz] (Naver 의 경우 [YYYY]-[MM]-[DD]T00:00.000+09:00)
   * Sample Request :
     * http://localhost:8080/api/v1/search/keyword?query=노정욱&page=1&size=2
   * Sample Response : 
     * {"totalElements":342,"pageSize":2,"pageNumber":1,"lastItem":false,"fromService":"Kakao","documents":[{"title":"&#39;커피 한잔 할까요?&#39; <b>노정욱</b> 감독 &#34;옹성우, &#39;강고비&#39; 그 차제…준비된 연기자&#34;","contents":"&#39;커피 한잔 할까요?&#39; 온라인 제작발표회 / 사진: 카카오TV 제공 <b>노정욱</b> 감독과 박호산이 옹성우와 &#39;고비&#39;의 싱크로율에 감탄했다. ​ 22일 오후 카카오TV 오리지널 &#39;커피 한잔 할까요?&#39;(극본/연출 <b>노정욱</b>)의 온라인 제작발표회가 열려 <b>노정욱</b> 감독을 비롯해 옹성우, 박호산, 서영희가 참석했다. ​ 허영만 화백의 동명 만화...","url":"https://blog.naver.com/pickcon_kr/222545058752","blogname":"PICKCON(픽콘)","thumbnail":"","datetime":"2021-10-22T17:54:00.000+09:00"},{"title":"[두부만드는사람들] 론칭 (주)공존컴퍼니 <b>노정욱</b> 대표","contents":"기계부터 사후관리까지 원스톱시스템 구축 2021년 3월 론칭 후 가맹점 50여 곳 오픈 ​ 두부 프랜차이즈 ‘두부만드는사람들’를 론칭한 (주)공존컴퍼니(대표 <b>노정욱</b>)는 우수한 기술력을 바탕으로 두부제조 기계 자체 생산 및 A/S 서비스를 제공하는 기업이다. 두만사는 식약처로부터 허가받은 남해 지하 염수, 백년초...","url":"https://blog.naver.com/l1012016/222301506294","blogname":"월간 PEOPLE & COMPANY","thumbnail":"https://search1.kakaocdn.net/argon/130x130_85_c/1v7FCwEltc1","datetime":"2021-04-07T10:28:00.000+09:00"}]}
   * 에러 정보 : 
     * 에러 발생시 응답형태 :  
       * errorCode : 에러코드
       * errorString : 에러내용
       * serviceErrorInfo : Kakao / Naver 검색 API 에서 오류 발생시 해당 정보 출력
       * Sample : {"errorCode":"5000","errorString":"Connector Service Error","serviceErrorInfo":"400 Bad Request from GET ..."}
     * 에러 정의
       * HTTP 400 | errorCode : 1001 | errorString : InvalidArgument Error | 호출 파라미터 오류
       * HTTP 400 | errorCode : 5000 | errorString : Connector Service Error | Kakao / Naver 검색 API 오류
       * HTTP 500 | errorCode : 9000 | errorString : 기타 서버 에러 | 기타 서버 내부 오류

2. 인기 검색어 목록 - 사용자들이 많이 검색한 순서대로 최대 10개의 검색 키워드와 검색횟수를 함께 제공합니다.
   * **(주의) 완전히 실시간 동기화는 아니며 3초에 한번 검색순위가 반영됩니다.**
   * URI : (GET) /api/v1/search/keyword/rank
   * Request Param
   * Response Param
      * [] :
         * keyword : 검색어
         * views : 검색횟수
   * Sample Request :
     * http://localhost:8080/api/v1/search/keyword/rank
   * Sample Response :
     * [{"keyword":"빅맥","views":24},{"keyword":"노정욱","views":4},{"keyword":"노정욱1","views":3},{"keyword":"노정욱15","views":1},{"keyword":"노정욱100","views":1},{"keyword":"노정욱99","views":1},{"keyword":"노정욱16","views":1},{"keyword":"노정욱2","views":1},{"keyword":"노정욱13","views":1},{"keyword":"햄버거","views":1}]
   * 에러 정보 :
      * 에러 발생시 응답형태 :
         * errorCode : 에러코드
         * errorString : 에러내용
         * Sample : {"errorCode":"9000","errorString":"기타 서버 에러","serviceErrorInfo": null}
      * 에러 정의
         * HTTP 500 | errorCode : 9000 | errorString : 기타 서버 에러 | 기타 서버 내부 오류


***

### 추가 구현 기능

1. 성능 향상과 동일 키워드 조회수의 동시성 이슈를 해결하기 위해 주기적으로 Redis DB -> 메인 DB 로의 업데이트 전략을 사용하였습니다.  
   * Embedded Redis 를 사용하여 검색요청시 메인 DB 대신 Redis 에 키워드를 sorted set 형태로 저장합니다.
   * 3초마다 스케쥴러가 동작하여 Redis DB 에 저장된 정보를 메인 DB 로 업데이트 하고 Redis DB 를 비웁니다.
   * 검색요청시마다 메인 DB 를 update 하지 않으므로 불필요한 낭비를 줄이고, Redis 는 싱글스레드 특성상 Atomic 을 보장해 주므로 동시성 이슈를 해결할 수 있습니다.

2. 카카오 블로그 검색 API 장애 발생 상황을 대비하여 서킷브레이커 를 적용하였습니다.
   * 카카오 블로그 검색 API 호출 시 일정 횟수 이상 Exception 이 발생할 경우, 장애로 판단하여 서킷브레이커를 Open 하고 네이버 블로그 검색 API 를 통해 데이터를 제공합니다.
   * 10초 경과 후 서킷브레이커를 half-open 상태로 변경 합니다.
   * half-open 상태에서 요청은 다시 카카오 블로그 검색 API 를 호출하며, Exception 이 발생하는 경우 다시 서킷브레이커를 Open 합니다.
   * half-open 카카오 블로그 검색 API 호출이 성공하면 서킷브레이커를 Close 합니다.
   * (서킷브레이커 전략 관련 설정은 application.yaml 파일의 resilience4j 부분 참고)

***

### 사용 오픈소스

org.springframework.boot:spring-boot-starter-data-redis : Redis DB 연동을 위해 사용

org.springframework.boot:spring-boot-starter-data-jpa : JPA 기능을 위해 사용

org.springframework.boot:spring-boot-starter-web : Springboot Web 서비스 제공을 위해 사용

org.springframework.boot:spring-boot-starter-webflux : 외부 서버 호출시 WebClient 사용을 위해 사용 

io.github.resilience4j:resilience4j-spring-boot2:1.7.1 : 서킷브레이커 사용을 위해 사용

org.mapstruct:mapstruct:1.5.2.Final : Domain(Entity) -> DTO 데이터 매핑을 위해 사용

it.ozimov:embedded-redis:0.7.3 : Redis 를 설치 없이 Embedded 환경에서 구동하기 위해 사용

org.projectlombok:lombok : lombok 어노테이션 사용을 위해 사용

org.projectlombok:lombok-mapstruct-binding:0.2.0 : mapstruct 와 lombok 연동을 위해 사용

org.springframework.boot:spring-boot-starter-test : 테스트코드 작성및 수행을 위해 사용

org.junit.jupiter:junit-jupiter-api : 테스트코드 작성및 수행을 위해 사용

org.junit.jupiter:junit-jupiter-engine : 테스트코드 작성및 수행을 위해 사용

com.h2database:h2 : h2 DB 사용을 위해 사용

***
### 다운로드 링크
https://github.com/bigmac1955/blogsearch/blob/48a29cef6df90c9ae7cbcce4f1c3abfb4cb9e837/BlogSearch-Server-0.0.1-SNAPSHOT.jar

실행방법 : java -jar BlogSearch-Server-0.0.1-SNAPSHOT.jar

