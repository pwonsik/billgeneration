package wirelessbill.infrastructure.converter;

import org.springframework.stereotype.Component;

import wirelessbill.domain.masterdata.RevenueMasterData;
import wirelessbill.infrastructure.dto.RevenueMasterDataDto;

/**
 * RevenueMasterData DTO와 Domain 객체 간 변환 로직
 */
@Component
public class RevenueMasterDataConverter {
    
    /**
     * DTO를 도메인 객체로 변환한다
     * 
     * @param dto RevenueMasterDataDto
     * @return RevenueMasterData 도메인 객체
     */
    public RevenueMasterData convertToDomain(RevenueMasterDataDto dto) {
        return new RevenueMasterData(
            dto.revenueItemId(),
            dto.effectiveStartDate(),
            dto.effectiveEndDate(),
            dto.revenueItemName(),
            dto.overdueChargeRevenueItemId(),
            dto.vatRevenueItemId()
        );
    }
}