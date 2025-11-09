package wirelessbill.port.out;

import java.time.LocalDate;
import java.util.List;

import wirelessbill.domain.discount.ContractDiscounts;

public interface ContractDiscountQueryPort {
    List<ContractDiscounts> findContractDiscounts(
        List<Long> contractIds, LocalDate billingStartDate, LocalDate billingEndDate
    );
}
