package wirelessbill.api;

import java.util.List;

import wirelessbill.domain.CalculationResult;

public record CalculationResultGroup(
    List<CalculationResult<?>> calculationResults
) {
}
