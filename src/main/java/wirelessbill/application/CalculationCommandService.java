package wirelessbill.application;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wirelessbill.api.CalculationCommandUseCase;
import wirelessbill.api.CalculationResultGroup;
import wirelessbill.application.pipeline.CalculationStep;
import wirelessbill.application.pipeline.DefaultCalculationPipeline;
import wirelessbill.application.pipeline.PipelineContext;
import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.CalculationTarget;

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
