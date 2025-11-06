package me.realimpact.telecom.calculation.application.pipeline;

import java.util.List;

public interface CalculationPipeline {
    List<CalculationStep> getSteps();
}
