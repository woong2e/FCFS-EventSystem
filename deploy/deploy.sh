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

# 3. ✅DB & Redis 인프라 배포 (변경 사항이 있을 때만 재시작됨)
if [ -f "docker-compose-database.yml" ]; then
    docker-compose -f docker-compose-database.yml pull
    docker-compose -f docker-compose-database.yml up -d
else
    echo "⚠️ docker-compose-database.yml 파일이 없습니다. DB 인프라 배포 스킵."
fi

# 4. App 및 Nginx 배포
echo "Start Application..."

# (1) 최신 이미지 Pull (App만)
docker-compose -f docker-compose.yml pull app

# (2) 컨테이너 실행
docker-compose -f docker-compose.yml up -d --scale app=2

# 5. 미사용 이미지 정리
docker image prune -f

echo "✅ 배포 완료!"