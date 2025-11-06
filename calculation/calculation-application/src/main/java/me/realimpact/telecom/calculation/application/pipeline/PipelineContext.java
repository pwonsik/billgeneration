package me.realimpact.telecom.calculation.application.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.realimpact.telecom.calculation.application.CalculationTarget;
import me.realimpact.telecom.calculation.domain.CalculationContext;
import me.realimpact.telecom.calculation.domain.CalculationResult;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class PipelineContext {
    private final CalculationContext calculationContext;
    private final CalculationTarget calculationTarget;
    private List<CalculationResult<?>> calculationResults;
}
