package bill.port.out;

import java.time.LocalDate;
import java.util.List;

import bill.domain.discount.ContractDiscounts;

public interface ContractDiscountQueryPort {
    List<ContractDiscounts> findContractDiscounts(
        List<Long> contractIds, LocalDate billingStartDate, LocalDate billingEndDate
    );
}
