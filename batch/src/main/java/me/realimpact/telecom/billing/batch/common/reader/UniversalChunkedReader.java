package me.realimpact.telecom.billing.batch.common.reader;

import lombok.extern.slf4j.Slf4j;
import me.realimpact.telecom.billing.batch.calculation.CalculationParameters;
import me.realimpact.telecom.billing.batch.common.pipeline.DataTransformationPipeline;
import me.realimpact.telecom.calculation.domain.CalculationContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.realimpact.telecom.billing.batch.constant.BatchConstants.CHUNK_SIZE;

/**
 * 파이프라인 패턴을 사용하는 범용 청크 기반 Reader입니다.
 * 다양한 타입의 데이터를 처리할 수 있으며, 제네릭 대신 파이프라인을 주입받아 가독성과 확장성을 높였습니다.
 *
 * @param <T> 읽어올 데이터의 타입
 */
@Slf4j
public class UniversalChunkedReader<T> implements ItemStreamReader<T> {

    private final DataTransformationPipeline<T> transformationPipeline;
    private final SqlSessionFactory sqlSessionFactory;
    private final CalculationParameters calculationParameters;

    private static final int chunkSize = CHUNK_SIZE;

    private MyBatisCursorItemReader<Long> contractIdReader;
    private ListItemReader<T> currentChunkReader;
    private boolean initialized = false;

    public UniversalChunkedReader(
            DataTransformationPipeline<T> transformationPipeline,
            SqlSessionFactory sqlSessionFactory,
            CalculationParameters calculationParameters) {
        this.transformationPipeline = transformationPipeline;
        this.sqlSessionFactory = sqlSessionFactory;
        this.calculationParameters = calculationParameters;

        log.info("=== UniversalChunkedReader 생성 (파이프라인: {}) ===", 
                transformationPipeline.getPipelineName());
    }

    @Override
    public void open(@NonNull ExecutionContext executionContext) throws ItemStreamException {
        if (!initialized) {
            log.info("=== UniversalChunkedReader.open() 시작 ===");
            initializeContractIdReader(executionContext);
            initialized = true;
            log.info("=== UniversalChunkedReader.open() 완료 ===");
        }
    }

    @Override
    public void update(@NonNull ExecutionContext executionContext) throws ItemStreamException {
        if (contractIdReader != null) {
            contractIdReader.update(executionContext);
        }
    }

    @Override
    public void close() throws ItemStreamException {
        if (contractIdReader != null) {
            contractIdReader.close();
            log.info("=== MyBatisCursorItemReader.close() 완료 ===");
        }
    }

    @Override
    public T read() throws Exception {
        log.debug("=== UniversalChunkedReader.read() 호출 ===");

        if (contractIdReader == null) {
            log.error("contractIdReader가 null입니다. 초기화에 실패했습니다.");
            return null;
        }

        if (currentChunkReader != null) {
            T item = currentChunkReader.read();
            if (item != null) {
                log.debug("청크에서 아이템 반환");
                return item;
            }
        }

        loadNextChunk();

        if (currentChunkReader == null) {
            log.debug("더 이상 읽을 데이터 없음");
            return null;
        }

        T item = currentChunkReader.read();
        log.debug("새로운 청크에서 아이템 반환");
        return item;
    }

    private void initializeContractIdReader(ExecutionContext executionContext) {
        log.info("=== MyBatisCursorItemReader 생성 시작 ===");
        try {
            contractIdReader = new MyBatisCursorItemReader<>();
            contractIdReader.setSqlSessionFactory(sqlSessionFactory);

            if (calculationParameters.getContractIds().isEmpty()) {
                contractIdReader.setQueryId("me.realimpact.telecom.calculation.infrastructure.adapter.mybatis.ContractQueryMapper.findAllContractIds");
                Map<String, Object> parameterValues = new HashMap<>();
                parameterValues.put("billingStartDate", calculationParameters.getBillingStartDate());
                parameterValues.put("billingEndDate", calculationParameters.getBillingEndDate());
                contractIdReader.setParameterValues(parameterValues);
                log.info("전체 계약 조회");
            } else {
                contractIdReader.setQueryId("me.realimpact.telecom.calculation.infrastructure.adapter.mybatis.ContractQueryMapper.findSpecificContractIds");
                Map<String, Object> parameterValues = new HashMap<>();
                parameterValues.put("contractIds", calculationParameters.getContractIds());
                contractIdReader.setParameterValues(parameterValues);
                log.info("특정 계약 조회: {} 건", calculationParameters.getContractIds().size());
            }

            contractIdReader.open(executionContext);
            log.info("=== MyBatisCursorItemReader 생성 완료 ===");
        } catch (Exception e) {
            log.error("MyBatisCursorItemReader 초기화 실패", e);
            contractIdReader = null;
        }
    }

    private void loadNextChunk() throws Exception {
        List<Long> contractIds = new ArrayList<>();
        for (int i = 0; i < chunkSize; i++) {
            Long contractId = contractIdReader.read();
            if (contractId == null) {
                break;
            }
            contractIds.add(contractId);
        }

        if (contractIds.isEmpty()) {
            currentChunkReader = null;
            return;
        }

        List<T> transformedData = transformData(contractIds);
        currentChunkReader = new ListItemReader<>(transformedData);
    }

    private List<T> transformData(List<Long> contractIds) {
        CalculationContext ctx = calculationParameters.toCalculationContext();
        log.debug("파이프라인 '{}'을(를) 사용하여 {} 건의 계약 ID 변환 시작",
                transformationPipeline.getPipelineName(), contractIds.size());
        return transformationPipeline.transform(contractIds, ctx);
    }
}
