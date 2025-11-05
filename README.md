# MCP Spring Bridge

A Spring Boot library that exposes REST API documentation to Large Language Models (LLMs) via the Model Context Protocol (MCP). This enables natural language interaction with API specifications through ChatGPT or other LLM interfaces.

[한국어](#한국어) | [English](#english)

---

## 한국어

### 개요

MCP Spring Bridge는 Spring Boot 애플리케이션의 OpenAPI 문서를 MCP(Model Context Protocol) 형식으로 변환하여 LLM이 API 명세를 이해하고 응답할 수 있도록 하는 라이브러리입니다.

### 주요 기능

- OpenAPI 3.0 스펙 자동 파싱 및 변환
- MCP 프로토콜 기반 API 문서 제공
- Bearer 토큰 기반 인증
- Zero Configuration 지원
- Spring Boot Auto-Configuration

### 설치

#### Gradle

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.SALARY-MARKET:MCP-BRIDGE:1.0.8'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4'
}
```

#### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.SALARY-MARKET</groupId>
    <artifactId>MCP-BRIDGE</artifactId>
    <version>1.0.8</version>
</dependency>
```

### 설정

#### application.yml

```yaml
mcp:
  enabled: true
  base-path: /mcp
  open-api-path: /v3/api-docs
  token: "your-secret-token"

springdoc:
  api-docs:
    enabled: true
```

#### 설정 속성

| 속성 | 기본값 | 설명 |
|------|--------|------|
| `mcp.enabled` | `true` | MCP Bridge 활성화 여부 |
| `mcp.base-path` | `/mcp` | MCP 엔드포인트 기본 경로 |
| `mcp.open-api-path` | `/v3/api-docs` | OpenAPI 문서 경로 |
| `mcp.token` | - | Bearer 인증 토큰 (선택사항) |

### API 엔드포인트

#### GET /mcp/tools

사용 가능한 MCP 도구 목록을 반환합니다.

**응답 예시:**
```json
{
  "tools": [
    {
      "name": "getApiDocumentation",
      "description": "Get comprehensive API documentation",
      "inputSchema": {
        "type": "object",
        "properties": {
          "query": {
            "type": "string",
            "description": "Search query for filtering APIs"
          }
        }
      }
    }
  ]
}
```

#### POST /mcp/call

MCP 도구를 실행합니다.

**요청 예시:**
```json
{
  "name": "getApiDocumentation",
  "arguments": {
    "query": "all"
  }
}
```

**응답 예시:**
```json
{
  "status": "success",
  "result": "# API Documentation\n\n## Available Endpoints\n..."
}
```

### 사용 가능한 도구

1. **getApiDocumentation**: 전체 API 문서 조회
2. **searchEndpoints**: 키워드로 엔드포인트 검색
3. **getEndpointDetails**: 특정 엔드포인트 상세 정보 조회

### 보안

Bearer 토큰 인증을 지원합니다. `mcp.token` 속성을 설정하면 모든 MCP 엔드포인트 요청에 `Authorization: Bearer {token}` 헤더가 필요합니다.

```bash
curl -X POST http://localhost:8080/mcp/call \
  -H "Authorization: Bearer your-secret-token" \
  -H "Content-Type: application/json" \
  -d '{"name":"getApiDocumentation","arguments":{"query":"all"}}'
```

### 외부 접근 설정

#### ngrok 사용

```bash
# ngrok 설치
choco install ngrok  # Windows
brew install ngrok   # macOS

# 터널 실행
ngrok http 8080

# 출력된 URL을 application.yml에 설정
springdoc:
  server:
    url: https://your-ngrok-url.ngrok-free.dev
```

### 라이센스

MIT License

---

## English

### Overview

MCP Spring Bridge is a Spring Boot library that converts OpenAPI documentation into Model Context Protocol (MCP) format, enabling LLMs to understand and respond to API specifications.

### Features

- Automatic OpenAPI 3.0 specification parsing and conversion
- MCP protocol-based API documentation
- Bearer token authentication
- Zero configuration support
- Spring Boot Auto-Configuration

### Installation

#### Gradle

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.SALARY-MARKET:MCP-BRIDGE:1.0.8'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4'
}
```

#### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.SALARY-MARKET</groupId>
    <artifactId>MCP-BRIDGE</artifactId>
    <version>1.0.8</version>
</dependency>
```

### Configuration

#### application.yml

```yaml
mcp:
  enabled: true
  base-path: /mcp
  open-api-path: /v3/api-docs
  token: "your-secret-token"

springdoc:
  api-docs:
    enabled: true
```

#### Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `mcp.enabled` | `true` | Enable/disable MCP Bridge |
| `mcp.base-path` | `/mcp` | Base path for MCP endpoints |
| `mcp.open-api-path` | `/v3/api-docs` | OpenAPI document path |
| `mcp.token` | - | Bearer authentication token (optional) |

### API Endpoints

#### GET /mcp/tools

Returns a list of available MCP tools.

**Response Example:**
```json
{
  "tools": [
    {
      "name": "getApiDocumentation",
      "description": "Get comprehensive API documentation",
      "inputSchema": {
        "type": "object",
        "properties": {
          "query": {
            "type": "string",
            "description": "Search query for filtering APIs"
          }
        }
      }
    }
  ]
}
```

#### POST /mcp/call

Executes an MCP tool.

**Request Example:**
```json
{
  "name": "getApiDocumentation",
  "arguments": {
    "query": "all"
  }
}
```

**Response Example:**
```json
{
  "status": "success",
  "result": "# API Documentation\n\n## Available Endpoints\n..."
}
```

### Available Tools

1. **getApiDocumentation**: Retrieve complete API documentation
2. **searchEndpoints**: Search endpoints by keyword
3. **getEndpointDetails**: Get detailed information about a specific endpoint

### Security

Bearer token authentication is supported. When `mcp.token` is configured, all MCP endpoint requests require an `Authorization: Bearer {token}` header.

```bash
curl -X POST http://localhost:8080/mcp/call \
  -H "Authorization: Bearer your-secret-token" \
  -H "Content-Type: application/json" \
  -d '{"name":"getApiDocumentation","arguments":{"query":"all"}}'
```

### External Access Setup

#### Using ngrok

```bash
# Install ngrok
choco install ngrok  # Windows
brew install ngrok   # macOS

# Start tunnel
ngrok http 8080

# Configure the generated URL in application.yml
springdoc:
  server:
    url: https://your-ngrok-url.ngrok-free.dev
```

### ChatGPT Integration

#### 1. Create Custom GPT

1. Go to ChatGPT → "Explore GPTs" → "Create a GPT"
2. Configure Actions with the following schema:

```json
{
  "openapi": "3.1.0",
  "info": {
    "title": "API Assistant",
    "version": "1.0.0"
  },
  "servers": [
    {
      "url": "https://your-server-url.com"
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
            "description": "Success"
          }
        }
      }
    }
  }
}
```

#### 2. Configure Authentication

- Authentication Type: API Key
- API Key: your-token-here
- Auth Type: Bearer

### Requirements

- Java 17 or higher
- Spring Boot 3.x
- SpringDoc OpenAPI 2.x

### License

MIT License

### Links

- [Model Context Protocol](https://modelcontextprotocol.io/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Spring Boot](https://spring.io/projects/spring-boot)
