cd EquipmentAllocation
docker build -t scala-play-corporate-equipment-allocation:latest .

cd ..
cd NotificationService
docker build -t notification-service:latest .

cd ..
docker network create kafka-network