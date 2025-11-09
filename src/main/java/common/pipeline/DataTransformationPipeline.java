package common.pipeline;

import java.util.ArrayList;
import java.util.List;

import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.CalculationTarget;

/**
 * 계약 ID로부터 CalculationTarget을 생성하는 파이프라인
 * 여러 데이터 로딩 단계(Step)를 순차적으로 실행합니다.
 * 
 * 사용 예:
 * <pre>
 * pipeline
 *   .addStep(loadMonthlyChargeStep1)
 *   .addStep(loadMonthlyChargeStep2)
 *   .addStep(loadOneTimeChargeStep1)
 *   .addStep(loadOneTimeChargeStep2)
 *   .addStep(loadDiscountsStep);
 * 
 * List<CalculationTarget> targets = pipeline.transform(contractIds, context);
 * </pre>
 */
public class DataTransformationPipeline {

    private final List<DataLoadingStep<CalculationTargetBuilder>> steps = new ArrayList<>();
    private final String pipelineName;

    public DataTransformationPipeline(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    /**
     * 파이프라인에 데이터 로딩 단계를 추가합니다.
     * 
     * @param step 추가할 단계
     * @return this (메서드 체이닝)
     */
    public DataTransformationPipeline addStep(DataLoadingStep<CalculationTargetBuilder> step) {
        this.steps.add(step);
        return this;
    }

    /**
     * 주어진 계약 ID 목록을 CalculationTarget 목록으로 변환합니다.
     * 각 계약 ID마다 등록된 모든 단계를 순차적으로 실행합니다.
     *
     * @param contractIds 변환할 계약 ID 목록
     * @param context 계산 컨텍스트
     * @return 변환된 CalculationTarget 목록
     */
    public List<CalculationTarget> transform(List<Long> contractIds, CalculationContext context) {
        List<CalculationTarget> results = new ArrayList<>();
        
        for (Long contractId : contractIds) {
            CalculationTargetBuilder builder = new CalculationTargetBuilder()
                .contractId(contractId);
            
            // 등록된 모든 단계를 순차적으로 실행
            for (DataLoadingStep<CalculationTargetBuilder> step : steps) {
                step.execute(builder, contractId, context);
            }
            
            results.add(builder.build());
        }
        
        return results;
    }

    public String getPipelineName() {
        return pipelineName;
    }
}
