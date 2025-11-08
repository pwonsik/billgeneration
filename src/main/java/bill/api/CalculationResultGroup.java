package bill.api;

import java.util.List;

import bill.domain.CalculationResult;

public record CalculationResultGroup(
    List<CalculationResult<?>> calculationResults
) {
}
