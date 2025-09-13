# JRE 이미지를 기반으로 사용 (경량화)
FROM eclipse-temurin:21-jre-jammy

# non-root 사용자 설정 (보안 강화)
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
USER appuser

# 컨테이너의 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 실행
# JAR 파일은 Docker 빌드 과정에서 복사되지 않으며,
# docker-compose.yml 파일에서 볼륨 마운트를 통해 제공됩니다.
ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080