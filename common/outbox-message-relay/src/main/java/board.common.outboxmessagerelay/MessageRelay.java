package board.common.outboxmessagerelay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageRelay {

    private final OutBoxRepository outBoxRepository;
    private final MessageRelayCoordinator messageRelayCoordinator;
    private final KafkaTemplate<String, String> messageRelayKafkaTemplate;

    /**
     * 커밋 전 outboxEvent를 받아 Repository에 저장. service단과 단일 transaction으로 처리가능
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(OutboxEvent outboxEvent) {
        log.info("[MesageRelay.createOutbox] outboxEvent={}", outboxEvent);
        outBoxRepository.save(outboxEvent.getOutbox());
    }

    /**
     * 커밋 후 outboxEvent를 받아 비동기 Kafka publish 처리
     * messageRelayPublishEventExecutor:: @Bean등록된 Publish Executor
     */
    @Async("messageRelayPublishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(OutboxEvent outboxEvent) {
        _publishEvent(outboxEvent.getOutbox());
    }

    private void _publishEvent(Outbox outbox) {
        try {
            messageRelayKafkaTemplate.send(
                    outbox.getEventType().getTopic(),
                    String.valueOf(outbox.getShardKey()),
                    outbox.getPayload()
            ).get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("[MessageRelay._publishEvent] Error while publishing event. outbox={}", outbox, e);
        }
    }

    /**
     * DB에서 생성된 지 10초이상 지난 outbox를 조회하여 비동기로 polling -> Kafka publish
     * messageRelayPublishPendingExecutor:: @Bean등록된 Pending Executor
     */
    @Scheduled(
            fixedDelay = 10,
            initialDelay = 5,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "messageRelayPublishPendingExecutor"
    )
    public void publishPendingEvent() {
        AssignedShard assignedShard = messageRelayCoordinator.assignShard();
        log.info("[MessageRelay.publishPendingEvent] assignedShard.size={}", assignedShard.getShards().size());

        for (Long shard : assignedShard.getShards()) {
            List<Outbox> outboxList = outBoxRepository.findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
                    shard,
                    LocalDateTime.now().minusSeconds(10),
                    Pageable.ofSize(100)
            );
            for (Outbox outbox : outboxList) {
                _publishEvent(outbox);
            }
        }
    }
}