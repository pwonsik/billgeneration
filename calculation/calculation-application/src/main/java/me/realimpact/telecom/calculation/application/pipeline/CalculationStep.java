package me.realimpact.telecom.calculation.application.pipeline;

@FunctionalInterface
public interface CalculationStep {
    void execute(PipelineContext context);
}
