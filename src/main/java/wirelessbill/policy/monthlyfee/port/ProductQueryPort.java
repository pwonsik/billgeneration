package wirelessbill.policy.monthlyfee.port;

import java.time.LocalDate;
import java.util.List;

import wirelessbill.policy.monthlyfee.domain.ContractWithProductsAndSuspensions;

public interface ProductQueryPort {
    List<ContractWithProductsAndSuspensions> findContractsAndProductInventoriesByContractIds(
        List<Long> contractIds, LocalDate billingStartDate, LocalDate billingEndDate
    );
}
