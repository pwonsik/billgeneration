package bill.port.out;

import java.util.List;

import bill.domain.CalculationContext;
import bill.domain.CalculationResult;

public interface CalculationResultSavePort {
    void save(CalculationContext context, List<CalculationResult<?>> calculationResults);
}
