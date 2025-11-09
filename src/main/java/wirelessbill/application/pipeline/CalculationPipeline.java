package wirelessbill.application.pipeline;

import java.util.List;

public interface CalculationPipeline {
    List<CalculationStep> getSteps();
}
