#!/bin/bash

# 1. Swap 메모리 설정
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

# 2. 최신 이미지 Pull
docker-compose pull app

# 3. 컨테이너 재시작 (app만 건드리는 게 효율적)
# --no-deps: app이 의존하는 다른 컨테이너(nginx 등)는 재시작 안 함
docker-compose up -d --no-deps --scale app=2 --remove-orphans app

# 4. 미사용 이미지 정리 (디스크 공간 확보)
docker image prune -f

echo "✅ 배포 완료!"