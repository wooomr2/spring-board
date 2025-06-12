```
docker pull mysql:8.0.38

docker run --name board-mysql -e MYSQL_ROOT_PASSWORD=root -d -p 3307:3306 mysql:8.0.38

docker exec -it board-mysql bash

mysql -u root -p
```

```
docker run --name board-redis -d -p 6379:6379 redis:7.4
```

```
docker run --name board-kafka -d -p 9092:9092 apache/kafka:3.8.0

docker exec --workdir /opt/kafka/bin -it board-kafka sh

[//]: # (KAFKA 토픽 생성, local message broker 1개로 셋팅)
./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic board-article --replication-factor 1 --partitions 3
./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic board-comment --replication-factor 1 --partitions 3
./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic board-like --replication-factor 1 --partitions 3
./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic board-view --replication-factor 1 --partitions 3
```

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

[//]: # (ParameterizedTypeReference)

```
✅ 1. 왜 필요한가? (제네릭 타입 소거 문제)
Java는 **타입 소거(Type Erasure)**라는 개념 때문에 런타임에는 제네릭 타입 정보가 사라집니다.

예를 들어 아래처럼 List<String>을 전달한다고 해도, 런타임에는 그냥 List로만 인식됩니다.


List<String> list = new ArrayList<>();
Type type = list.getClass().getGenericSuperclass();  // => List
이 때문에 List<String> 같은 복잡한 타입을 런타임에 유지하거나 전달하는 것이 어렵습니다.

🔍 2. ParameterizedTypeReference<T>란?
📌 정의
Spring의 ParameterizedTypeReference<T>는 제네릭 타입을 런타임에 유지할 수 있도록 해주는 추상 클래스입니다.

내부적으로는 Java의 리플렉션(Reflection)을 활용해서 제네릭 타입 정보를 보존합니다.

📦 어디에 쓰는가?
- RestTemplate이나 WebClient로 HTTP 응답을 받을 때
- 제네릭 타입을 Type 객체로 넘길 때
- 타입 정보를 명확히 전달해야 할 때

⚙️ 3. 내부 동작 원리
핵심 구조

public abstract class ParameterizedTypeReference<T> {
    private final Type type;

    protected ParameterizedTypeReference() {
        Type superclass = getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return this.type;
    }
}
익명 서브클래스를 만들면 T의 타입이 getGenericSuperclass()를 통해 잡힙니다.

이걸 통해 List<String>, Map<String, User> 같은 제네릭 타입이 Type 객체로 보존됩니다.

💡 4. 실전 사용법 예제
예제 1: RestTemplate으로 제네릭 타입 응답 받기
RestTemplate restTemplate = new RestTemplate();

ResponseEntity<List<ArticleResponse>> response =
    restTemplate.exchange(
        "https://api.example.com/articles",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<ArticleResponse>>() {}
    );

List<ArticleResponse> articles = response.getBody();
익명 클래스를 생성하면서 <List<ArticleResponse>> 타입 정보를 캡처함

예제 2: WebClient에서도 동일
WebClient client = WebClient.create();

Mono<List<UserDto>> result = client.get()
    .uri("/users")
    .retrieve()
    .bodyToMono(new ParameterizedTypeReference<List<UserDto>>() {});
    
예제 3: 직접 Type을 가져와서 사용
ParameterizedTypeReference<Map<String, List<User>>> typeRef =
    new ParameterizedTypeReference<Map<String, List<User>>>() {};

Type type = typeRef.getType();
System.out.println(type);  // java.util.Map<java.lang.String, java.util.List<User>>

⚠️ 5. 주의사항
항목	설명
익명 클래스로 생성해야 타입이 캡처됨	new ParameterizedTypeReference<>() {} ← 꼭 중괄호 필요
타입 정보를 유지하고 싶은 곳에만 사용	런타임이 중요한 곳에서만 필요 (리플렉션 기반 작업 등)
클래스 상속 구조가 복잡할수록 작동에 주의	상속 깊이가 깊어지면 제네릭 타입 추출이 실패할 수 있음

🧠 6. 요약
항목	설명
클래스명	org.springframework.core.ParameterizedTypeReference<T>
목적	런타임에 제네릭 타입 정보를 보존하고 전달하기 위함
사용 예	RestTemplate, WebClient, 리플렉션 기반 Type 추론
내부 구현	익명 서브클래스와 getGenericSuperclass() 활용
주의	반드시 익명 서브클래스로 생성해야 타입이 추론됨

📌 결론
ParameterizedTypeReference<T>는 Java의 제네릭 타입 소거 문제를 우회해
복잡한 제네릭 타입 정보를 런타임까지 유지할 수 있도록 만들어주는 유틸리티입니다.

프레임워크 내부뿐 아니라, 우리가 직접 만든 유틸 도구, HTTP 통신, Type 추론이 필요한 곳에서도 매우 유용하게 쓰입니다!
````

[//]: # (Comment 삭제)

``` 
🔄 변경 감지(Dirty Checking)에 의해 반영되는 시점
JPA는 트랜잭션 안에서 엔티티의 필드가 변경되면 자동으로 변경 사항을 감지해서, 트랜잭션 커밋 시점에 UPDATE 쿼리를 실행합니다.

즉, 다음과 같은 흐름입니다:

commentRepository.findById(...)로 엔티티를 영속성 컨텍스트에 불러옴.

comment.delete()로 deleted 값 변경.

변경된 건 감지되지만 즉시 DB 반영은 아님.

@Transactional 메서드 끝날 때 트랜잭션이 커밋되며, 그 시점에 JPA가 감지된 변경 사항에 대해 UPDATE 쿼리를 실행.
```

[//]: # (

```
ArticleLikeService.unlikePessimisticLock1
ArticleLikeService.unlikePessimisticLock2

위 두 개의 메소드에서,
articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
쿼리가 수행됩니다.

동일 파라미터에 대해 데이터가 삭제되기 전까지의 모든 동시 요청은 데이터 조회를 성공하고, ifPresent 구문으로 들어가게 됩니다.
그리고 JPA의 delete 메소드를 호출하는데, JPA의 delete 메소드는 조회된 엔티티가 타 트랜잭션에 의해 이미 삭제되었더라도 예외를 던지지 않습니다.
즉, 동시에 ifPresent로 들어간 모든 요청은 decrease를 중복으로 수행하게 됩니다.
1개만 감소하면 충분한 상황인데도, 동시 요청에 의해 카운트가 중복으로 감소되는 것입니다.
이를 해결하려면, JPA의 delete 메소드를 사용하는 대신, 직접 DELETE 쿼리를 native query로 정의할 수 있습니다.
직접 정의한 DELETE 쿼리는 영향 받은 row의 수를 반환할 수 있도록 하고,
이러한 반환 값이 0이라면, 이미 다른 요청에 의해 데이터가 삭제 되었음을 의미합니다.
이 경우 예외를 발생시켜서 decrease 중복 수행을 방지할 수 있습니다.
```

)

[//]: # ()

```
✅ 1. @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
🔍 역할
해당 쿼리를 실행할 때 JPA가 SQL의 SELECT ... FOR UPDATE 문을 생성하여,
해당 행(row)에 비관적 잠금(Pessimistic Lock)을 적용함
동시에 수정하려는 트랜잭션이 있다면 락이 풀릴 때까지 대기 또는 실패

🔐 LockModeType.PESSIMISTIC_FORCE_INCREMENT 설명
모드	설명
PESSIMISTIC_READ	읽기 잠금 (FOR SHARE) — 다른 트랜잭션이 쓰기 못함
PESSIMISTIC_WRITE	쓰기 잠금 (FOR UPDATE) — 다른 트랜잭션이 읽기/쓰기 못함
**PESSIMISTIC_FORCE_INCREMENT**	PESSIMISTIC_WRITE + 버전 증가

즉, PESSIMISTIC_FORCE_INCREMENT는: 행에 대해 쓰기 잠금(FOR UPDATE)을 걸고,
@Version 필드를 강제로 증가시킴 (Optimistic Lock과 호환성 확보)

💡 내부 동작 흐름

Spring Data JPA가 리포지토리 메서드 분석

RepositoryFactorySupport → JpaRepositoryFactory
메서드에 @Lock이 있으면 → QueryMethod.getLockModeType()으로 LockMode 추출
Query 생성 시 LockMode 설정
내부에서 JPA EntityManager.createQuery(...) 또는 createNamedQuery(...) 호출
이때 javax.persistence.Query.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT) 적용
Hibernate가 SQL 생성 시 FOR UPDATE 추가
Hibernate 내부 클래스 org.hibernate.dialect.Dialect에서 데이터베이스에 맞는 FOR UPDATE SQL 구문 생성
MySQL의 경우: SELECT ... FOR UPDATE
PostgreSQL의 경우: SELECT ... FOR UPDATE NOWAIT 등
JDBC 실행 → 락 획득
락을 얻지 못하면 트랜잭션 대기 또는 예외 (LockTimeoutException)

SELECT * FROM article_like_count WHERE article_id = ? FOR UPDATE
그리고 JPA의 버전 필드 (@Version)가 강제로 +1 됨

⚠️ 주의 사항
트랜잭션 내에서만 사용 가능 (@Transactional)
락을 걸기 때문에 성능에 영향, 필요할 때만 사용
락 획득 실패 시 LockTimeoutException 발생 가능
```

[//]: # ()

```
✅ 2. @Modifying
🔍 역할
**@Query가 DML (INSERT / UPDATE / DELETE)**일 때 명시적으로 사용
Spring Data JPA는 기본적으로 SELECT 쿼리로 추정 →
@Modifying 없으면 실행 시 InvalidDataAccessApiUsageException 발생

🔧 내부 동작 원리
@Modifying이 붙은 메서드는 Spring Data JPA 내부적으로 Query.executeUpdate()를 호출함
이로 인해 반환 타입은 void, int, boolean 중 하나
int는 영향을 받은 row 수를 반환
```

[//]: # (조회수 어뷰징 방지: 분산락 Distributed Lock)

```

```

[//]: # (TODO:: )

```
좋은 질문입니다. 현재 MessageRelay._publishEvent()에서는 Kafka 전송 성공 여부에 따라 아무런 후속 처리 없이 try-catch만 하고 있습니다.
Kafka 전송 결과에 따라 Outbox 테이블의 상태를 성공/실패로 업데이트하려면 다음과 같이 코드를 보완하시면 됩니다.

✅ 1. Outbox 엔티티에 상태 필드 추가
java
복사
편집
public enum OutboxStatus {
    PENDING,
    SENT,
    FAILED
}
java
복사
편집
@Entity
public class Outbox {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private LocalDateTime createdAt;
    private Long shardKey;
    private String payload;
    private String eventType;

    // 기타 필드 및 getter/setter
}
✅ 2. 전송 성공/실패에 따라 상태 업데이트
아래처럼 _publishEvent() 내부에서 전송 성공 시 SENT, 실패 시 FAILED로 업데이트하도록 수정합니다.

java
복사
편집
private void _publishEvent(Outbox outbox) {
    try {
        messageRelayKafkaTemplate.send(
                outbox.getEventType().getTopic(),
                String.valueOf(outbox.getShardKey()),
                outbox.getPayload()
        ).get(1, TimeUnit.SECONDS);

        // 성공 처리
        outbox.setStatus(OutboxStatus.SENT);
        outBoxRepository.save(outbox);

        log.info("[MessageRelay._publishEvent] Kafka 전송 성공. id={}, topic={}", outbox.getId(), outbox.getEventType().getTopic());

    } catch (Exception e) {
        log.error("[MessageRelay._publishEvent] Kafka 전송 실패. outbox={}", outbox, e);

        // 실패 처리
        outbox.setStatus(OutboxStatus.FAILED);
        outBoxRepository.save(outbox);
    }
}
✅ 3. PENDING 상태만 조회하도록 쿼리 제한
OutboxRepository 쿼리 메소드도 아래처럼 status = PENDING 조건을 추가해야 중복 전송을 막을 수 있습니다.

java
복사
편집
List<Outbox> findAllByShardKeyAndStatusAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
    Long shardKey,
    OutboxStatus status,
    LocalDateTime createdAt,
    Pageable pageable
);
→ 호출부에서 아래처럼 사용:

java
복사
편집
List<Outbox> outboxList = outBoxRepository.findAllByShardKeyAndStatusAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
    shard,
    OutboxStatus.PENDING,
    LocalDateTime.now().minusSeconds(10),
    Pageable.ofSize(100)
);
🔁 보너스: 실패건 재시도 처리 방식 (선택)
FAILED 상태의 이벤트를 특정 횟수만큼 재시도하거나, 별도 관리하여 보정할 수도 있습니다.

예:

상태를 FAILED로 바꿨다가 운영자가 확인 후 재시도

실패 횟수를 따로 컬럼으로 둬서 자동 재시도 로직 구성


✅ 1. Outbox 엔티티 확장
java
복사
편집
@Entity
public class Outbox {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status; // PENDING, SENT, FAILED

    private int retryCount; // 실패 횟수 기록

    private int maxRetryCount = 5; // 최대 재시도 횟수 (기본값 5)

    private LocalDateTime nextRetryAt; // 다음 재시도 가능 시간

    private LocalDateTime createdAt;

    private Long shardKey;

    private String payload;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    // ... 기타 필드 및 getter/setter
}
✅ 2. 실패 시 retryCount 증가 및 nextRetryAt 설정
java
복사
편집
private void _publishEvent(Outbox outbox) {
    try {
        messageRelayKafkaTemplate.send(
                outbox.getEventType().getTopic(),
                String.valueOf(outbox.getShardKey()),
                outbox.getPayload()
        ).get(1, TimeUnit.SECONDS);

        outbox.setStatus(OutboxStatus.SENT);
        outBoxRepository.save(outbox);

    } catch (Exception e) {
        log.error("[_publishEvent] Kafka 전송 실패: {}", outbox, e);

        outbox.setRetryCount(outbox.getRetryCount() + 1);

        if (outbox.getRetryCount() >= outbox.getMaxRetryCount()) {
            outbox.setStatus(OutboxStatus.FAILED); // 최종 실패
        } else {
            outbox.setStatus(OutboxStatus.PENDING); // 재시도 대상 유지
            outbox.setNextRetryAt(LocalDateTime.now().plusSeconds(30)); // 30초 후 재시도
        }

        outBoxRepository.save(outbox);
    }
}
✅ 3. PENDING + 재시도 조건 만족하는 이벤트만 조회
java
복사
편집
List<Outbox> findAllByShardKeyAndStatusAndNextRetryAtLessThanEqualOrderByCreatedAtAsc(
    Long shardKey,
    OutboxStatus status,
    LocalDateTime now,
    Pageable pageable
);
호출부:

java
복사
편집
List<Outbox> outboxList = outBoxRepository.findAllByShardKeyAndStatusAndNextRetryAtLessThanEqualOrderByCreatedAtAsc(
    shard,
    OutboxStatus.PENDING,
    LocalDateTime.now(),
    Pageable.ofSize(100)
);
✅ 4. 수동 재처리 (운영자 기능)
운영자가 Outbox 테이블을 조회하여 상태가 FAILED인 항목을 선택적으로 PENDING으로 변경 + retryCount 초기화해서 재시도할 수 있습니다.

예: 운영자 API

java
복사
편집
@PostMapping("/admin/outbox/{id}/retry")
public void retryFailedOutbox(@PathVariable Long id) {
    Outbox outbox = outBoxRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Outbox not found"));

    if (outbox.getStatus() == OutboxStatus.FAILED) {
        outbox.setStatus(OutboxStatus.PENDING);
        outbox.setRetryCount(0);
        outbox.setNextRetryAt(LocalDateTime.now());
        outBoxRepository.save(outbox);
    }
}
✅ 보너스: 실패 로그와 dead-letter queue
재시도 실패한 메시지는 Kafka Dead Letter Topic(DLT)으로 보내거나

별도 실패 테이블(outbox_fail_log)에 기록하여 감사성 추적을 할 수 있습니다.
```