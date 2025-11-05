# MCP Spring Bridge 배포 가이드

## 🚀 로컬 Maven 배포 완료

### 배포된 아티팩트
- **Group ID**: `com.github.KidKim826`
- **Artifact ID**: `mcp-spring-bridge`
- **Version**: `1.0.6`
- **위치**: `~/.m2/repository/com/github/KidKim826/mcp-spring-bridge/1.0.6/`

### 배포된 파일들
- `mcp-spring-bridge-1.0.6.jar` (26,879 bytes)
- `mcp-spring-bridge-1.0.6.pom` (1,848 bytes)
- `mcp-spring-bridge-1.0.6.module` (2,200 bytes)

## 📦 사용법

### Gradle 프로젝트에서 사용
```gradle
dependencies {
    implementation 'com.github.KidKim826:mcp-spring-bridge:1.0.6'
}
```

### Maven 프로젝트에서 사용
```xml
<dependency>
    <groupId>com.github.KidKim826</groupId>
    <artifactId>mcp-spring-bridge</artifactId>
    <version>1.0.6</version>
</dependency>
```

## 🔄 다음 배포 단계

### JitPack 배포 (공개)
```bash
git tag v1.0.6
git push origin v1.0.6
```

### 사용자 프로젝트에서 JitPack 사용
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.KidKim826:mcp-spring-bridge:1.0.6'
}
```

## ✅ 배포 완료!
로컬 Maven 저장소에 성공적으로 배포되었습니다.