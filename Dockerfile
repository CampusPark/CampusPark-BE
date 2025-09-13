# 1단계: 빌드를 위한 베이스 이미지
FROM openjdk:21-jdk AS builder
WORKDIR /workspace/app

# 의존성 파일들만 먼저 복사
COPY gradlew ./
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 다운로드 (이 부분은 거의 바뀌지 않으므로 캐싱됨)
RUN ./gradlew dependencies --info

# 소스 코드 복사 (자주 바뀌는 부분)
COPY src ./src

# 애플리케이션 빌드
RUN ./gradlew build -x test


# 2단계: 실제 실행을 위한 경량 이미지
FROM openjdk:21-jdk
WORKDIR /app

# non-root 사용자 설정 (보안 강화)
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
USER appuser

# 빌드 단계에서 생성된 jar 파일만 복사
COPY --from=builder /workspace/app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]