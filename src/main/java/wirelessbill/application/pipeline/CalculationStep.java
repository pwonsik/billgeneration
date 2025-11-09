package wirelessbill.application.pipeline;

@FunctionalInterface
public interface CalculationStep {
    void execute(PipelineContext context);
}
