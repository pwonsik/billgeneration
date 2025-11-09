package common.pipeline.example;

import common.domain.BillContext;
import common.pipeline.BillPipeline;
import common.pipeline.BillSample1Pipeline;

/**
 * Nova 스타일 파이프라인 실행 예제
 */
public class PipelineExample {
    
    public static void main(String[] args) {
        // Nova 스타일 파이프라인 생성
        BillPipeline pipeline = new BillSample1Pipeline(null);
        
        // 컨텍스트 생성
        BillContext context = new BillContext("20241109");
        
        // 파이프라인 실행
        System.out.println("=== Nova 스타일 파이프라인 실행 ===");
        System.out.println("파이프라인 지원 여부: " + pipeline.supports(context));
        System.out.println("단계 수: " + pipeline.getSteps().size());
        System.out.println();
        
        // 파이프라인 실행
        pipeline.execute(context);
        
        System.out.println();
        System.out.println("=== 파이프라인 실행 완료 ===");
    }
}