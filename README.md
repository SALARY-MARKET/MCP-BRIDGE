# MCP Spring Bridge

🚀 **Replace Swagger UI with ChatGPT via Model Context Protocol (MCP)**

Swagger 문서 대신 ChatGPT에게 질문하면 Spring Boot 프로젝트의 API 명세와 사용법을 자동으로 알려주는 오픈소스 라이브러리입니다.

## ✨ 주요 기능

- 📚 **Swagger 대체**: 문서 대신 ChatGPT가 API 명세를 실시간으로 설명
- 🤖 **자연어 질의**: "사용자 생성 API 어떻게 써?" 같은 자연어로 질문 가능
- 🔄 **실시간 문서화**: OpenAPI 스펙을 실시간으로 파싱해서 최신 정보 제공
- 🔧 **Zero Configuration**: 의존성 추가만으로 즉시 사용 가능
- 🌐 **팀 공유**: 공유 GPT 계정으로 팀 전체가 API 정보 접근 가능

## 🚀 빠른 시작

### 1. 의존성 추가

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.KidKim826:mcp-spring-bridge:1.0.7'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4'
}
```

### 2. 설정

```yaml
# application.yml
server:
  forward-headers-strategy: framework

springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
  server:
    url: https://your-ngrok-url.ngrok-free.dev  # ngrok 사용 시

mcp:
  enabled: true
  base-path: /mcp
  open-api-path: /v3/api-docs
  token: "your-secret-token"  # 보안을 위해 설정 (필수)
```

### 3. 앱 실행 & 터널 설정

```bash
# 1. Spring Boot 앱 실행
./gradlew bootRun

# 2. 새 터미널에서 ngrok 터널 실행 (권장)
ngrok http 8080

# 출력된 URL 복사: https://abc123.ngrok-free.dev
```

**ngrok 설치:**
```bash
# Windows (Chocolatey)
choco install ngrok

# 또는 https://ngrok.com/download 에서 다운로드

# 계정 생성 후 authtoken 설정
ngrok config add-authtoken YOUR_AUTHTOKEN
```

### 3. GPTs 연결

#### 방법 1: Actions를 통한 연결 (권장)

1. **Custom GPT 생성** (GPT Plus 필요)
   - ChatGPT → "Explore GPTs" → "Create a GPT"
   - 이름: "My API Assistant"
   - 설명: "Spring Boot API 문서를 실시간으로 검색하고 설명해주는 어시스턴트"

2. **Actions 설정**
   ```
   Configure → Actions → Create new action
   ```
   
   **Schema 입력:**
   ```json
   {
     "openapi": "3.1.0",
     "info": {
       "title": "API Assistant",
       "version": "1.0.0"
     },
     "servers": [
       {
         "url": "https://your-ngrok-url.ngrok-free.dev"
       }
     ],
     "paths": {
       "/mcp/call": {
         "post": {
           "operationId": "callTool",
           "requestBody": {
             "required": true,
             "content": {
               "application/json": {
                 "schema": {
                   "type": "object",
                   "properties": {
                     "name": {
                       "type": "string",
                       "enum": ["getApiDocumentation", "searchEndpoints", "getEndpointDetails"]
                     },
                     "arguments": {
                       "type": "object",
                       "properties": {},
                       "additionalProperties": true
                     }
                   },
                   "required": ["name", "arguments"]
                 }
               }
             }
           },
           "responses": {
             "200": {
               "description": "Success",
               "content": {
                 "application/json": {
                   "schema": {
                     "type": "object",
                     "properties": {
                       "status": {"type": "string"},
                       "result": {"type": "string"}
                     }
                   }
                 }
               }
             }
           }
         }
       }
     }
   }
   ```

3. **Authentication 설정** (토큰 사용 시)
   ```
   Authentication Type: API Key
   API Key: your-token-here
   Auth Type: Bearer
   ```

4. **Instructions 추가**
   ```
   CRITICAL INSTRUCTIONS - YOU MUST FOLLOW EXACTLY:

   You can ONLY use these THREE tool names. NO OTHER NAMES ARE ALLOWED:

   1. "getApiDocumentation" - Get comprehensive API documentation
   2. "searchEndpoints" - Search for specific endpoints
   3. "getEndpointDetails" - Get detailed endpoint information

   When user asks about APIs:

   For "show all APIs" or "list APIs":
   {"name": "getApiDocumentation", "arguments": {"query": "all"}}

   For "search user API" or "find chat API":
   {"name": "searchEndpoints", "arguments": {"keyword": "user"}}

   For "details of /api/users":
   {"name": "getEndpointDetails", "arguments": {"path": "/api/users"}}

   Always provide practical examples with curl commands.
   ```

#### 방법 2: 로컬 MCP 클라이언트 (고급 사용자)

1. **Claude Desktop 설치**
2. **MCP 설정 파일 생성** (`~/.claude_desktop_config.json`):
   ```json
   {
     "mcpServers": {
       "spring-api": {
         "command": "curl",
         "args": ["-X", "GET", "http://localhost:8080/mcp/tools"]
       }
     }
   }
   ```

#### 방법 3: 터널을 통한 외부 접근

**ngrok 사용 (권장)**
```bash
# 1. ngrok 설치
choco install ngrok  # Windows
# 또는 https://ngrok.com/download

# 2. 계정 생성 및 authtoken 설정
ngrok config add-authtoken YOUR_AUTHTOKEN

# 3. 터널 실행
ngrok http 8080

# 출력 예시:
# Forwarding  https://abc123.ngrok-free.dev -> http://localhost:8080
```

**GPTs Actions에서 ngrok URL 사용:**
```json
"servers": [
  {
    "url": "https://abc123.ngrok-free.dev"
  }
]
```

**주의사항:**
- localtunnel은 GPTs에서 보안 문제로 차단될 수 있습니다
- ngrok이 더 안정적이고 GPTs와 호환성이 좋습니다

```
사용자: "사용자 생성 API 어떻게 써?"
ChatGPT: "POST /api/users 엔드포인트를 사용하세요..."

사용자: "결제 관련 API 있어?"
ChatGPT: "결제 관련 엔드포인트를 찾았습니다..."
```

## 🎯 사용 예시

### 기존 Spring Boot Controller (변경 없음)

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public User getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }
    
    @PostMapping
    @Operation(summary = "Create new user")
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.create(request);
    }
}
```

### ChatGPT 질문 & 답변 예시

**질문 1:** "사용자 조회 API 어떻게 써?"

**ChatGPT 답변:**
```
# GET /api/users/{id}

**Summary:** Get user by ID

**Example:**
curl -X GET http://localhost:8080/api/users/123

**Response:**
{
  "id": 123,
  "name": "John Doe",
  "email": "john@example.com"
}
```

**질문 2:** "사용자 생성할 때 어떤 데이터 보내야 해?"

**ChatGPT 답변:**
```
# POST /api/users

**Request Body:**
{
  "name": "string (required)",
  "email": "string (required)",
  "age": "number (optional)"
}

**Example:**
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name": "John", "email": "john@example.com"}'
```

## 🔧 고급 설정

```yaml
server:
  forward-headers-strategy: framework  # ngrok 사용 시 필수
  tomcat:
    connection-timeout: 60000          # 타임아웃 설정

springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
  server:
    url: https://your-ngrok-url.ngrok-free.dev  # 외부 URL 설정

mcp:
  enabled: true                    # MCP Bridge 활성화 (기본: true)
  base-path: /mcp                  # MCP 엔드포인트 경로 (기본: /mcp)
  open-api-path: /v3/api-docs      # OpenAPI 문서 경로 (기본: /v3/api-docs)
  token: "your-secret-token"       # Bearer 토큰 (필수)

logging:
  level:
    com.mcpbridge: DEBUG             # MCP 디버그 로그
```

## 📋 API 엔드포인트

| 엔드포인트 | 메서드 | 설명 |
|---------|-------|------|
| `/mcp/tools` | GET | 사용 가능한 MCP Tools 목록 반환 |
| `/mcp/call` | POST | MCP 도구 실행 |

### 사용 가능한 도구

1. **getApiDocumentation** - 전체 API 문서 조회
   ```json
   {"name": "getApiDocumentation", "arguments": {"query": "all"}}
   ```

2. **searchEndpoints** - 키워드로 엔드포인트 검색
   ```json
   {"name": "searchEndpoints", "arguments": {"keyword": "user"}}
   ```

3. **getEndpointDetails** - 특정 엔드포인트 상세 정보
   ```json
   {"name": "getEndpointDetails", "arguments": {"path": "/api/users/{id}"}}
   ```

## 🧪 테스트

```bash
./gradlew test
```

## 📦 빌드 & 배포

```bash
# 라이브러리 빌드
./gradlew clean build

# 로컬 Maven 저장소에 배포 (테스트용)
./gradlew publishToMavenLocal

# GitHub에 배포 (JitPack 자동 빌드)
git tag v1.0.7
git push origin v1.0.7
```

## 🤝 기여하기

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 라이센스

이 프로젝트는 [MIT License](LICENSE) 하에 배포됩니다.

## 🔗 관련 링크

- [Model Context Protocol (MCP)](https://modelcontextprotocol.io/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Spring Boot](https://spring.io/projects/spring-boot)

---

**Made with ❤️ for the AI-powered development community**