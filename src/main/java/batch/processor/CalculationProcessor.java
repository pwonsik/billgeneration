package batch.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wirelessbill.api.CalculationResultGroup;
import wirelessbill.application.CalculationCommandService;
import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.CalculationTarget;

import org.springframework.batch.item.ItemProcessor;

import batch.BillParameters;

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