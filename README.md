# DPANG MILEAGE SERVER

## 🌐 프로젝트 개요

이 프로젝트는 마일리지 서비스를 지원하는 마이크로서비스로서, 사용자의 마일리지 생성, 조회, 삭제, 소비, 환불, 충전 요청, 충전 요청 조회 및 처리 등의 기능을 제공합니다.

이를 통해 사용자의 마일리지 관련 작업을 효율적으로 관리하고, 사용자 경험을 향상시키는데 중점을 두고 있습니다.

## 🔀 프로젝트 아키텍처

```mermaid
sequenceDiagram
    participant Client as Client
    participant Gateway as Spring Cloud Gateway
    participant MileageService as Mileage Service
    participant MySQL as MySQL
    participant OtherService as Other Service
    
    Client ->> Gateway: 요청 전송
    Gateway ->> MileageService: 요청 전달 <br> (X-DPANG-CLIENT-ID, X-DPANG-CLIENT-ROLE 헤더 추가)
    MileageService ->> MileageService: 해당 요청 권한 식별

    opt 요청에 대한 권한이 있는 경우
        MileageService ->> MySQL: 데이터 요청
        MySQL -->> MileageService: 데이터 응답

        opt 타 서비스의 데이터가 필요한 경우
            MileageService ->> OtherService: API 요청 <br> (X-DPANG-SERVICE-NAME 헤더 추가)
            OtherService ->> OtherService: 요청에 대한 처리
            OtherService -->> MileageService: 처리된 API 응답
        end

        MileageService ->> MileageService: 응답 처리
        MileageService -->> Gateway: 응답 전송
        Gateway -->> Client: 최종 응답 전달
    end

    opt 요청에 대한 권한이 없는 경우
        MileageService -->> Gateway: 사용자 권한 없음 응답
        Gateway -->> Client: 사용자 권한 없음 응답
    end

    opt 인증 실패한 경우
        Gateway -->> Client: 인증 실패 응답
    end
```

## 🗃️ 데이터베이스 구조

마일리지 서비스에서 활용하는 데이터베이스(MySQL)는 다음과 같은 구조의 테이블을 가지고 있습니다.

```mermaid
erDiagram
    USER ||--|| MILEAGE: "has"
    USER ||--|| CHARGE_REQUEST: "requests"
    
    MILEAGE {
        bigint user_id PK "사용자 식별자"
        date join_date "가입 일자"
        int mileage "마일리지"
        int personal_charged_mileage "개인 충전 마일리지"
        datetime(6) updated_at "최근 수정 일자"
    }
    
    CHARGE_REQUEST {
        bigint charge_request_id PK "충전 요청 번호"
        int requested_mileage "요청 마일리지"
        datetime(6) request_date "요청 일자"
        bigint user_id "사용자 식별자"
        varchar(255) depositor_name "입금자명"
        enum status "충전 요청 상태"
    }

```

## ✅ 프로젝트 실행

해당 프로젝트를 추가로 개발 혹은 실행시켜보고 싶으신 경우 아래의 절차에 따라 진행해주세요

#### 1. `secret.yml` 생성

```commandline
cd ./src/main/resources
touch secret.yml
```

#### 2. `secret.yml` 작성

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://{YOUR_DB_HOST}:{YOUR_DB_PORT}/{YOUR_DB_NAME}
    username: { YOUR_DB_USERNAME }
    password: { YOUR_DB_PASSWORD }

  application:
    name: mileage-server

eureka:
  instance:
    prefer-ip-address: true

  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://{YOUR_EUREKA_SERVER_IP}:{YOUR_EUREKA_SERVER_PORT}/eureka/
```

#### 3. 프로젝트 실행

```commandline
./gradlew bootrun
```

**참고) 프로젝트가 실행 중인 환경에서 아래 URL을 통해 API 명세서를 확인할 수 있습니다**

```commandline
http://localhost:8080/swagger-ui/index.html
```