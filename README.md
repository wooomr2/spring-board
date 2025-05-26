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