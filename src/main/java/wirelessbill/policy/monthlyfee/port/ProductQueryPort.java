package bill.policy.monthlyfee.port;

import java.time.LocalDate;
import java.util.List;

import bill.policy.monthlyfee.domain.ContractWithProductsAndSuspensions;

public interface ProductQueryPort {
    List<ContractWithProductsAndSuspensions> findContractsAndProductInventoriesByContractIds(
        List<Long> contractIds, LocalDate billingStartDate, LocalDate billingEndDate
    );
}
