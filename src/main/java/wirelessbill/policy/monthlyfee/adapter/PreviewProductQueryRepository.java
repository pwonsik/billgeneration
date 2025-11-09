package bill.policy.monthlyfee.adapter;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import bill.policy.monthlyfee.adapter.mybatis.PreviewProductQueryMapper;
import bill.policy.monthlyfee.conveter.ContractDtoToDomainConverter;
import bill.policy.monthlyfee.domain.ContractWithProductsAndSuspensions;
import bill.policy.monthlyfee.dto.ContractProductsSuspensionsDto;
import bill.policy.monthlyfee.port.ProductQueryPort;
import lombok.RequiredArgsConstructor;

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
