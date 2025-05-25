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
[//]: # (인덱스)
``````
testData = BULK_INSERT_SIZE * EXECUTE_COUNT = 100,000,000

Clustered Index: Primary Index: 테이블의 Primary Key로 자동 생성. 테이블당 1개
Secondary Index(보조 인덱스): 인덱스 컬럼 데이터 + 데이터에 접근하기 위한 포인터(Primary Key)
Covering Index: 인덱스만으로 쿼리의 모든 데이터를 처리할 수 있는 인덱스, Clustered Index의 데이터를 읽지 않고, Secondary Index에 포함된 정보만으로 처리가능한 인덱스
``````

[//]: # (무한 스크롤)
``````
limit = (((n – 1) / k) + 1) * m * k + 1
• 현재 페이지(n)
• n > 0
• 페이지당 게시글 개수(m)
• 이동 가능한 페이지 개수(k)
• ((n - 1) / k)의 나머지는 버림
• n=7, m=30, k=10
 • (((7 - 1) / 10) + 1) * 30 * 10 + 1 = 301
• n=12, m=30, k=10
 • (((12 - 1) / 10) + 1) * 30 * 10 + 1 = 601
``````