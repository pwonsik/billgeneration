package bill.policy.monthlyfee.adapter;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import bill.policy.monthlyfee.adapter.mybatis.ProductQueryMapper;
import bill.policy.monthlyfee.conveter.ContractDtoToDomainConverter;
import bill.policy.monthlyfee.domain.ContractWithProductsAndSuspensions;
import bill.policy.monthlyfee.dto.ContractProductsSuspensionsDto;
import bill.policy.monthlyfee.port.ProductQueryPort;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Qualifier("default_product_query_repository")
public class ProductQueryRepository implements ProductQueryPort {
    private final ProductQueryMapper productQueryMapper;
    private final ContractDtoToDomainConverter converter;

    @Override
    public List<ContractWithProductsAndSuspensions> findContractsAndProductInventoriesByContractIds(
        List<Long> contractIds, LocalDate billingStartDate, LocalDate billingEndDate
    ) {
        List<ContractProductsSuspensionsDto> contractProductsSuspensionsDtos = productQueryMapper.findContractsAndProductInventoriesByContractIds(contractIds, billingStartDate, billingEndDate);
        return converter.convertToContracts(contractProductsSuspensionsDtos);
    }
}
