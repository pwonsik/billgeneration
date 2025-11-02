package me.realimpact.telecom.billing.batch.bill.partitioner.prework;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class BillOperNumAssignPartitioner implements Partitioner {

	private final String invOperCyclCd;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        log.info("=== BillOperNumAssign 파티션 생성 시작 ===");
        log.info("요청된 파티션 수 (gridSize): {}", gridSize);

        // 루프에서 생성자의 threadCount 대신, 파라미터로 받은 gridSize를 사용
        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            context.putInt("partitionKey", i);
            context.putInt("partitionCount", gridSize); // partitionCount도 gridSize로 변경
            context.putString("invOperCyclCd", invOperCyclCd);
            
            String partitionName = "partition" + i;
            partitions.put(partitionName, context);
            
            log.info("파티션 생성: {} (partitionKey={}, partitionCount={}, invOperCyclCd={})",
                    partitionName, i, gridSize, invOperCyclCd);
        }
        log.info("총 {} 개 파티션 생성 완료", partitions.size());
        return partitions;
    }
}