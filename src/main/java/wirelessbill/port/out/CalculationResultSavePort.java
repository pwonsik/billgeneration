package wirelessbill.port.out;

import java.util.List;

import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.CalculationResult;

public interface CalculationResultSavePort {
    void save(CalculationContext context, List<CalculationResult<?>> calculationResults);
}
