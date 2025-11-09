package wirelessbill.port.out;

import wirelessbill.domain.discount.Discount;

public interface ContractDiscountCommandPort {
    void applyDiscount(Discount discount);
}
