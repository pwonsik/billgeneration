package batch.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import batch.BillParameters;
import bill.api.CalculationResultGroup;
import bill.domain.CalculationResult;
import bill.port.out.CalculationResultSavePort;

import java.util.List;

/**
 * MonthlyFeeCalculationResult를 데이터베이스에 저장하는 커스텀 Writer
 */
@RequiredArgsConstructor
@Slf4j
public class CalculationWriter implements ItemWriter<CalculationResultGroup> {

    private final CalculationResultSavePort calculationResultSavePort;
    private final BillParameters calculationParameters;

    @Override
    public void write(Chunk<? extends CalculationResultGroup> chunk) throws Exception {
        if (chunk.isEmpty()) {
            return;
        }

        List<CalculationResult<?>> calculationResults = chunk.getItems().stream()
            .flatMap(calculationResultGroup -> calculationResultGroup.calculationResults().stream())
            .toList();

        // 1. 계산 결과 저장
        calculationResultSavePort.save(calculationParameters.toCalculationContext(), calculationResults);

        // 2. 각 결과의 후처리 실행
        calculationResults.forEach(result -> result.executePost(calculationParameters.toCalculationContext()));
    }
}