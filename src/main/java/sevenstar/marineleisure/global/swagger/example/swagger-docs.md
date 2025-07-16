# ✅ Swagger 이용 가이드

---

## 🔧 1. Swagger(OpenAPI) 설정 방법

### 📦 Gradle 의존성 추가

```groovy
dependencies {
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0'
}
```

버전은 최신 안정 버전을 확인(https://search.maven.org/search?q=springdoc-openapi)

## 🌐 2. Swagger UI 접속 방법

Spring Boot 서버 실행 후 아래 주소로 접속:

```bash
http://localhost:8080/swagger-ui/index.html
```

## 🧩 3. API 문서 자동 생성 원리

| 구성 요소                                            | 설명                               |
| ------------------------------------------------ | -------------------------------- |
| `@RestController`, `@RequestMapping`             | API 엔드포인트 자동 인식                  |
| `@RequestBody`, `@PathVariable`, `@RequestParam` | 파라미터 자동 문서화                      |
| `@Schema`, `@Operation`, `@Parameter`            | Swagger 문서 커스터마이징용 어노테이션         |
| DTO 클래스                                          | 요청(Request)/응답(Response) 스키마 정의용 |


## 🧪 4. 실전 예제

SwaggerController.java, Swagger 패키지안의 예제 참조 바람


## 📁 5. 자주 사용하는 Swagger 어노테이션
| 어노테이션          | 설명                        |
| -------------- | ------------------------- |
| `@Operation`   | API 메서드에 대한 설명 추가         |
| `@Schema`      | DTO 필드에 대한 설명, 예제 지정      |
| `@Parameter`   | `@RequestParam` 등 파라미터 설명 |
| `@RequestBody` | 요청 본문에 대한 설명 (대부분 생략 가능)  |

## 🧼 6. 주의할 점
- MultipartFile은 @RequestPart 또는 @ModelAttribute로 작성해야 Swagger에서 제대로 보임
- record나 @Getter 기반 DTO에 @Schema 어노테이션은 잘 붙어야 UI에서 인식됨
- 너무 과도한 Swagger 어노테이션은 피하고, 필요한 곳만 문서화

## 📌 기타
Swagger 문서는 OpenAPI 3.0 스펙을 따릅니다.

API 명세가 자동으로 관리되므로 Postman 문서 작성이 불필요합니다.

필요시 Swagger JSON 명세를 export하여 API 문서 자동 생성 도구와 연동 가능합니다.
