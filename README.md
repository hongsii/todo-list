# 할일 목록(todo-list) 웹 어플리케이션 구현

## 목차
* [개발 환경](#개발-환경)
* [실행 방법](#실행-방법)
* [문제 해결 전략](#문제-해결-전략)
* [API 정보](#API-정보)


## 개발 환경
  * 언어 : Java 8
  * 프레임워크 : Spring Boot 2.1.2 RELEASE, JPA(Hibernate)
  * DB : H2


## 실행 방법

``` shell
$ git clone https://github.com/hongsii/todo-list.git && cd todo-list && ./gradlew bootRun
```

* 서버 실행
  * `git clone https://github.com/hongsii/todo-list.git` - 원하는 경로에 clone
  * `cd todo-list` - clone된 디렉토리로 이동
  * `./gradlew bootRun` - gradle을 사용해 실행
* 웹 접속  
  * `http://localhost:8080` 으로 접속
* H2 DB 접속
  * `http://localhost:8080/h2-console` 으로 접속
  * `JDBC URL`을 `jdbc:h2:mem:todo`로 설정
* 서버 종료
  * `ctrl + c`
  

## 문제 해결 전략

1. 구현 내용 정리
* 사용자는 할일을 추가/수정 할 수 있다.
  * 추가/수정시 상위 작업을 참조 걸 수 있다. (진행 중인 작업만 가능)
* 사용자는 목록을 조회할 수 있다.
  * 목록은 페이징 처리
* 사용자는 작업을 완료처리 할 수 있다.
  * 완료하려는 작업에 참조를 건 하위 작업이 완료되지 않았다면 완료처리를 할 수 없다.

2. 도메인 (Entity) 설계
  * 사용자는 텍스트로 된 할일을 추가할 수 있다. 
    * 텍스트의 할일을 저장하는 `Task` 추출
    * 조회시 필요한 데이터는 내용 (content), 작성일 (createdData), 최종수정일 (modifiedData), 완료 여부 (isCompleted)
    * 작성일, 최종수정일은 Spring의 `Auditing` 기능으로 처리
  * 할일 추가 시 다른 할일들을 참조 걸 수 있다.  
    * 하나의 작업은 **다른 직업을 참조**할 수 있고, **다른 작업에 의해 참조**될 수 있기 때문에 **다대다 관계**로 설계 필요
    * `@JoinTable`을 사용해 상위, 하위 참조 관계 테이블 생성 및 다대다 관계 표현
    (`Task`에서 관계에 대한 역할을 분리하기 위해 별도의 Embeddable 객체 `TaskRelation` 추출)

3. 웹 애플리케이션 구현
  * 애플리케이션의 큰 구조는 `Controller - Service - Repository`로 개발
  * API에서 발생하는 예외는 `ControllerAdvice`로 처리
  * API 개발 순서
    * 도메인 테스트 코드 작성 - 최대한 도메인 객체에 비지니스 로직을 구현하도록 개발
    * Repository는 `JpaRepository` 사용 (테스트 생략)
    * Service 테스트 코드 작성 - 도메인의 비지니스 로직 호출, 엔티티 영속화 역할
    * Controller 테스트 코드 작성 - Mock 객체로 URL Mapping 요청 및 응답 테스트

## API 정보

## 작업 (Task)

* [추가](#추가)
* [수정](#수정)
* [완료 처리](#완료-처리)
* [단건 조회](#단건-조회)
* [다건 조회](#다건-조회)

### 추가

신규 작업 추가

##### Request Body

| JSON 키 | 설명 |
|---------|-----|
| content | 작업 내용 |
| superTaskIds | 상위 참조 작업 ID |

##### Request Example

```
POST /api/tasks HTTP/1.1
Content-Type: application/json;charset=UTF-8

{
  "content" : "집안일",
  "superTaskIds" : [1,2]
}
```

### 수정

아이디에 해당하는 작업 수정

##### Request Body

| JSON 키 | 설명 |
|---------|-----|
| content | 작업 내용 |
| superTaskIds | 상위 참조 작업 ID |

##### Request Example

```
PUT /api/tasks/{id} HTTP/1.1
Content-Type: application/json;charset=UTF-8

{
  "content" : "집안일",
  "superTaskIds" : [1,2]
}
```

### 완료 처리

진행 중인 작업을 완료 처리

##### Request Example

```
PATCH /api/tasks/{id}/complete HTTP/1.1
```

### 단건 조회

아이디로 작업 조회

##### Request Example

```
GET /api/tasks/{id} HTTP/1.1
```

##### Response Example

```
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8

{
  "statusCode": 200,
  "message": "OK",
  "data": {
    "task": {
      "id": 1,
      "content": "집안일",
      "createdDate": "2019-02-03 23:35:32",
      "modifiedDate": "2019-02-03 23:35:32",
      "superTaskIds": [],
      "completed": false
    }
  }
}
```

### 다건 조회

다수의 작업 조회 (페이징)

##### Request Parameter

| 파라미터명 | 값 |
|---------|----|
| size    | 페이지당 row 개수 |
| page    | 요청 페이지 번호 | 
| sort    | 정렬 (column[, [ASC/DESC]) |

##### Request Example

```
GET /api/tasks HTTP/1.1
```

##### Response Example

```
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8

{
  "statusCode": 200,
  "message": "OK",
  "data": {
    "tasks": [
      {
        "id": 1,
        "content": "집안일",
        "createdDate": "2019-02-03 23:54:22",
        "modifiedDate": "2019-02-03 23:54:22",
        "superTaskIds": [],
        "completed": false
      },
      {
        "id": 2,
        "content": "빨래",
        "createdDate": "2019-02-03 23:54:22",
        "modifiedDate": "2019-02-03 23:54:22",
        "superTaskIds": [
          1
        ],
        "completed": false
      },
    ],
    "pageData": {
      "currentPage": 0,
      "totalPages": 1
    }
  }
}
```
