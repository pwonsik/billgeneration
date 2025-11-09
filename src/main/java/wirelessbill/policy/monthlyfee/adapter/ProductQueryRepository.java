package wirelessbill.policy.monthlyfee.adapter;

import lombok.RequiredArgsConstructor;
import wirelessbill.policy.monthlyfee.adapter.mybatis.ProductQueryMapper;
import wirelessbill.policy.monthlyfee.conveter.ContractDtoToDomainConverter;
import wirelessbill.policy.monthlyfee.domain.ContractWithProductsAndSuspensions;
import wirelessbill.policy.monthlyfee.dto.ContractProductsSuspensionsDto;
import wirelessbill.policy.monthlyfee.port.ProductQueryPort;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

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
