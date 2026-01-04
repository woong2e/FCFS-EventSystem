#!/bin/bash

# 1. Swap 메모리 설정 (기존 유지)
if [ ! -f /swapfile ]; then
    echo "⚠️ Swap 메모리 생성 (2GB)..."
    sudo dd if=/dev/zero of=/swapfile bs=128M count=16
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile swap swap defaults 0 0' | sudo tee -a /etc/fstab
    echo "✅ Swap 메모리 설정 완료"
fi

echo "🚀 배포 시작..."

# 2. ✅ 공용 네트워크 생성
docker network create app-network 2>/dev/null || true

# 3. ✅ Kafka 인프라 배포 (Consumer 실행을 위해 필수!)
if [ -f "docker-compose-kafka.yml" ]; then
    echo "🐦 Kafka 인프라 배포 중..."
    docker-compose -f docker-compose-kafka.yml pull
    docker-compose -f docker-compose-kafka.yml up -d

    echo "⏳ Kafka 초기화 대기 (10초)..."
    sleep 10
else
    echo "⚠️ docker-compose-kafka.yml 없음. Kafka 배포 스킵."
fi

# 4. ✅ DB & Redis 인프라 배포
if [ -f "docker-compose-database.yml" ]; then
    echo "💾 DB & Redis 인프라 배포 중..."
    docker-compose -f docker-compose-database.yml pull
    docker-compose -f docker-compose-database.yml up -d
else
    echo "⚠️ docker-compose-database.yml 없음. DB 배포 스킵."
fi

# 5. App 및 Nginx 배포
echo "☕ Application (API & Worker) 배포 시작..."

if [ -f "docker-compose.yml" ]; then
    # (1) 최신 이미지 Pull (API, Consumer, Nginx 등 모두 다운로드)
    docker-compose -f docker-compose.yml pull

    # (2) 컨테이너 실행
    docker-compose -f docker-compose.yml up -d --remove-orphans --scale api-server=2
else
    echo "❌ docker-compose.yml 파일이 없습니다!"
    exit 1
fi

# 6. 미사용 이미지 정리
docker image prune -f

echo "✅ 배포 완료!"