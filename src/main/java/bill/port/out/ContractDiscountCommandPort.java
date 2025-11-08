package bill.port.out;

import bill.domain.discount.Discount;

public interface ContractDiscountCommandPort {
    void applyDiscount(Discount discount);
}
