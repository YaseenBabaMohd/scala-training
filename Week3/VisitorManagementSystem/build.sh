cd VisitorService
docker build -t visitor-management-system:latest .

cd ..
cd NotificationService
docker build -t notification-service:latest .

cd ..
docker network create kafka-network