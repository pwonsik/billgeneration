package bill.api;

import java.util.List;

import bill.domain.CalculationContext;

public interface CalculationCommandUseCase {
    List<CalculationResultGroup> calculate(List<Long> contractIds, CalculationContext ctx);
}
