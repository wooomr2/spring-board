docker pull mysql:8.0.38

docker run --name board-mysql -e MYSQL_ROOT_PASSWORD=root -d -p 3307:3306 mysql:8.0.38

docker exec -it board-mysql bash

mysql -u root -p


[//]: # (PK 선택 전략)
````
Snowflake
- 64비트 ID 생성 알고리즘
- timestamp+nodeId+sequence
````