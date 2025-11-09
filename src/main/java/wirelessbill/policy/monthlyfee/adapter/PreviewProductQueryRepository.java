package wirelessbill.policy.monthlyfee.adapter;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import wirelessbill.policy.monthlyfee.adapter.mybatis.PreviewProductQueryMapper;
import wirelessbill.policy.monthlyfee.conveter.ContractDtoToDomainConverter;
import wirelessbill.policy.monthlyfee.domain.ContractWithProductsAndSuspensions;
import wirelessbill.policy.monthlyfee.dto.ContractProductsSuspensionsDto;
import wirelessbill.policy.monthlyfee.port.ProductQueryPort;

@Repository
@RequiredArgsConstructor
@Qualifier("preview_product_query_repository")
public class PreviewProductQueryRepository implements ProductQueryPort {
    private final PreviewProductQueryMapper previewProductQueryMapper;
    private final ContractDtoToDomainConverter converter;

    @Override
    public List<ContractWithProductsAndSuspensions> findContractsAndProductInventoriesByContractIds(
            List<Long> contractIds, LocalDate billingStartDate, LocalDate billingEndDate
    ) {
        List<ContractProductsSuspensionsDto> contractProductsSuspensionsDtos =
                previewProductQueryMapper.findContractsAndProductInventoriesByContractIds(contractIds, billingEndDate);
        return converter.convertToContracts(contractProductsSuspensionsDtos);
    }
}
