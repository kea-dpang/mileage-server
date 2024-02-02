# DPANG MILEAGE SERVER

## 🌐 프로젝트 개요

이 프로젝트는 마일리지 서비스를 지원하는 마이크로서비스로서, 사용자의 마일리지 생성, 조회, 삭제, 소비, 환불, 충전 요청, 충전 요청 조회 및 처리 등의 기능을 제공합니다.

이를 통해 사용자의 마일리지 관련 작업을 효율적으로 관리하고, 사용자 경험을 향상시키는데 중점을 두고 있습니다.

## 🔀 프로젝트 아키텍처

아래의 Sequence Diagram은 본 프로젝트의 주요 컴포넌트인 Spring Cloud Gateway, 타 서비스, 그리고 마일리지 서비스 간의 상호작용을 보여줍니다.

```mermaid
sequenceDiagram
    participant Client as Client
    participant Gateway as Spring Cloud Gateway
    participant OtherService as Other Service
    participant MileageService as Mileage Service
    participant MySQL as MySQL
    Client ->> Gateway: 요청 전송 (JWT 토큰 포함)
    Gateway ->> Gateway: 요청 인증 및 인가

    alt 인증 성공
        alt 요청이 Other Service에서 Mileage Service에 API 요청하는 경우
            Gateway ->> OtherService: 요청 전달 (X-DPANG-CLIENT-ID 헤더 추가)
            OtherService ->> MileageService: API 요청
        else 요청이 직접 Mileage Service를 호출하는 경우
            Gateway ->> MileageService: 요청 전달 (X-DPANG-CLIENT-ID 헤더 추가)
        end
        MileageService ->> MileageService: 해당 요청 권한 식별

        alt 요청이 역할에 적합
            MileageService ->> MySQL: 데이터 요청
            MySQL -->> MileageService: 데이터 응답
            MileageService ->> MileageService: 응답 처리

            alt 요청이 Other Service에서 Mileage Service에 API 요청하는 경우
                MileageService -->> OtherService: 응답 전송
                OtherService -->> Gateway: 응답 전송
            else 요청이 직접 Mileage Service를 호출하는 경우
                MileageService -->> Gateway: 응답 전송
            end

            Gateway -->> Client: 최종 응답 전달

        else 요청이 역할에 부적합
            alt 요청이 Other Service에서 Mileage Service에 API 요청하는 경우
                MileageService -->> OtherService: 사용자 권한 없음 응답
                OtherService -->> Gateway: 사용자 권한 없음 응답
            else 요청이 직접 Mileage Service를 호출하는 경우
                MileageService -->> Gateway: 사용자 권한 없음 응답
            end
            Gateway -->> Client: 사용자 권한 없음 응답
        end

    else 인증 실패
        Gateway -->> Client: 인증 실패 응답
    end

```

시퀀스 다이어그램을 통해 확인할 수 있듯이, 클라이언트로부터의 요청은 초기 단계에서 Spring Cloud Gateway를 통과하게 됩니다. 이 과정에서 사용자 인증이 이루어지며, 이 인증이 성공적으로 완료되어야만
서비스 요청이 이어집니다.

인증 과정이 정상적으로 마무리되면, 'X-DPANG-CLIENT-ID'라는 사용자 정의 헤더에 사용자의 ID 정보가 포함되어 전달됩니다. 이 헤더는 Mileage 서비스로의 요청에 함께 첨부되어, Mileage
서비스가 요청을 한 사용자를 정확하게 파악할 수 있게 도와줍니다. 이렇게 사용자 식별에 성공한 요청은 적절한 처리 과정을 거친 후, 최종 결과가 반환됩니다.

## 🗃️ 데이터베이스 구조

마일리지 서비스에서 활용하는 데이터베이스(MySQL)는 다음과 같은 구조의 테이블을 가지고 있습니다.

```mermaid
erDiagram
    USER ||--|| MILEAGE: "has"
    USER ||--|| CHARGE_REQUEST: "requests"
    MILEAGE {
        bigint user_id PK
        date join_date
        int mileage
        int personal_charged_mileage
        datetime(6) updated_at
    }
    CHARGE_REQUEST {
        bigint charge_request_id PK
        int requested_mileage
        datetime(6) request_date
        bigint user_id
        varchar(255) depositor_name
        enum status
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