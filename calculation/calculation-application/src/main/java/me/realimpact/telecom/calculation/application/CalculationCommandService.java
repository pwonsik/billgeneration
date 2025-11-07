package me.realimpact.telecom.calculation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.realimpact.telecom.calculation.api.CalculationCommandUseCase;
import me.realimpact.telecom.calculation.api.CalculationResultGroup;
import me.realimpact.telecom.calculation.application.pipeline.CalculationStep;
import me.realimpact.telecom.calculation.application.pipeline.DefaultCalculationPipeline;
import me.realimpact.telecom.calculation.application.pipeline.PipelineContext;
import me.realimpact.telecom.calculation.domain.CalculationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculationCommandService implements CalculationCommandUseCase {
    private final CalculationTargetQueryService queryService;
    private final DefaultCalculationPipeline pipeline;

    public CalculationResultGroup processCalculation(CalculationTarget calculationTarget, CalculationContext ctx) {
        log.debug("Processing contract calculation for contractId: {}", calculationTarget.contractId());
        try {
            PipelineContext pipelineContext = new PipelineContext(ctx, calculationTarget);

            List<CalculationStep> steps = pipeline.getSteps();
            for (CalculationStep step : steps) {
                log.debug("Executing step: {}", step.getClass().getSimpleName());
                step.execute(pipelineContext);
            }

            log.debug("Processed {} calculation results for contractId: {}",
                    pipelineContext.getCalculationResults().size(),
                    calculationTarget.contractId());
            return new CalculationResultGroup(pipelineContext.getCalculationResults());

        } catch (Exception e) {
            log.error("Failed to process contract calculation for contractId: {}", calculationTarget.contractId(), e);
            throw e;
        }
    }

    @Override
    public List<CalculationResultGroup> calculate(List<Long> contractIds, CalculationContext ctx) {
        return queryService.loadCalculationTargets(contractIds, ctx).stream()
                .map(calculationTarget -> processCalculation(calculationTarget, ctx))
                .toList();
    }
}
