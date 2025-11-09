package wirelessbill.application.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.CalculationResult;
import wirelessbill.domain.CalculationTarget;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class PipelineContext {
    private final CalculationContext calculationContext;
    private final CalculationTarget calculationTarget;
    private List<CalculationResult<?>> calculationResults;
}
