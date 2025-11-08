package batch.common.factory;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.stereotype.Component;

import batch.BillParameters;
import batch.common.pipeline.DataTransformationPipeline;
import batch.common.reader.UniversalPartitionedReader;

/**
 * 범용 파티션 Reader를 생성하는 팩토리 클래스입니다.
 * 파이프라인과 파라미터를 주입받아 적절한 Reader 인스턴스를 생성합니다.
 */
@Component
public class PartitionedReaderFactory {

    /**
     * 파이프라인을 사용하는 범용 파티션 Reader를 생성합니다.
     *
     * @param pipeline 데이터 변환을 담당할 파이프라인
     * @param sqlSessionFactory MyBatis SQL 세션 팩토리
     * @param parameters 계산 관련 파라미터
     * @param partitionKey 현재 파티션의 키
     * @param partitionCount 전체 파티션 개수
     * @param <T> 읽어올 데이터의 타입
     * @return 설정이 완료된 {@link UniversalPartitionedReader} 인스턴스
     */
    public <T> ItemStreamReader<T> createReader(
            DataTransformationPipeline<T> pipeline,
            SqlSessionFactory sqlSessionFactory,
            BillParameters parameters,
            Integer partitionKey,
            Integer partitionCount) {

        return new UniversalPartitionedReader<>(
            pipeline,
            sqlSessionFactory,
            parameters,
            partitionKey,
            partitionCount
        );
    }
}
