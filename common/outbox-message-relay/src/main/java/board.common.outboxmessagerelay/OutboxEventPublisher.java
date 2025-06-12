package board.common.outboxmessagerelay;

import board.common.event.Event;
import board.common.event.EventPayload;
import board.common.event.EventType;
import board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final Snowflake outboxSnowflake = new Snowflake();
    private final Snowflake eventSnowflake = new Snowflake();
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Kafka event 발행
     */
    public void publish(EventType type, EventPayload payload, Long shardKey) {
        // articleId= 10, shardKey == articleId
        // 10 % 4 = 2 물리적 샤드
        Outbox outbox = Outbox.create(
                outboxSnowflake.nextId(),
                type,
                Event.of(eventSnowflake.nextId(), type, payload).toJson(),
                shardKey % MessageRelayConstants.SHARD_COUNT
        );

        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
    }
}
