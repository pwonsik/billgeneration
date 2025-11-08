package batch.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.ItemProcessor;

import batch.BillParameters;
import bill.api.CalculationResultGroup;
import bill.application.CalculationCommandService;
import bill.domain.CalculationContext;
import bill.domain.CalculationTarget;

/**
 * Spring Batch ItemProcessor 구현체
 * ContractDto를 받아서 월정액 계산을 수행하고 결과를 반환
 */

@RequiredArgsConstructor
@Slf4j
public class CalculationProcessor implements ItemProcessor<CalculationTarget, CalculationResultGroup> {

    private final CalculationCommandService calculationCommandService;
    private final BillParameters calculationParameters;

    @Override
    public CalculationResultGroup process(@NonNull CalculationTarget calculationTarget) throws Exception {
        CalculationContext ctx = calculationParameters.toCalculationContext();

        return calculationCommandService.processCalculation(calculationTarget, ctx);
    }
}