package wirelessbill.api;

import java.util.List;

import wirelessbill.domain.CalculationContext;

public interface CalculationCommandUseCase {
    List<CalculationResultGroup> calculate(List<Long> contractIds, CalculationContext ctx);
}
