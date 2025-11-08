package bill.infrastructure.adapter;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import bill.domain.masterdata.RevenueMasterData;
import bill.infrastructure.adapter.mybatis.RevenueMasterDataMapper;
import bill.infrastructure.converter.RevenueMasterDataConverter;
import bill.port.out.RevenueMasterDataQueryPort;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 수익 항목 마스터 데이터 Repository 구현체
 */
@Repository
@RequiredArgsConstructor
public class RevenueMasterDataRepository implements RevenueMasterDataQueryPort {
    private final RevenueMasterDataMapper revenueMasterDataMapper;
    private final RevenueMasterDataConverter revenueMasterDataConverter;

    @Override
    public Map<String, RevenueMasterData> findRevenueMasterDataByBaseDate(LocalDate baseDate) {
        return revenueMasterDataMapper.findByBaseDate(baseDate)
            .stream()
            .collect(Collectors.toMap(
                dto -> dto.revenueItemId(),
                dto -> revenueMasterDataConverter.convertToDomain(dto)
            ));
    }
}