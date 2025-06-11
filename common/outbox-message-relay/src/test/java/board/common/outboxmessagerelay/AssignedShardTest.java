package board.common.outboxmessagerelay;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AssignedShardTest {


    @Test
    void ofTest() {
        // given

        long shardCount = 64L;
        List<String> appList = List.of("appId1", "appId2", "appId3");

        // when
        AssignedShard assignedShard1 = AssignedShard.of(appList.getFirst(), appList, shardCount);
        AssignedShard assignedShard2 = AssignedShard.of(appList.get(1), appList, shardCount);
        AssignedShard assignedShard3 = AssignedShard.of(appList.getLast(), appList, shardCount);
        AssignedShard assignedShard4 = AssignedShard.of("invalid", appList, shardCount);

        // then
        List<Long> resultList = Stream.of(
                        assignedShard1.getShards(),
                        assignedShard2.getShards(),
                        assignedShard3.getShards(),
                        assignedShard4.getShards()
                )
                .flatMap(List::stream)
                .toList();

        assertThat(resultList).hasSize((int) shardCount);

        for (int i = 0; i < shardCount; i++) {
            assertThat(resultList.get(i)).isEqualTo(i);
        }

        assertThat(assignedShard4.getShards()).isEmpty();
    }
}