package bill.infrastructure.adapter;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import bill.domain.discount.ContractDiscounts;
import bill.domain.discount.Discount;
import bill.infrastructure.adapter.mybatis.ContractDiscountMapper;
import bill.infrastructure.converter.ContractDiscountDtoConverter;
import bill.infrastructure.dto.ContractDiscountDto;
import bill.port.out.ContractDiscountCommandPort;
import bill.port.out.ContractDiscountQueryPort;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContractDiscountRepository implements ContractDiscountQueryPort, ContractDiscountCommandPort {
    private final ContractDiscountMapper contractDiscountMapper;
    private final ContractDiscountDtoConverter contractDiscountDtoConverter;

    @Override
    public List<ContractDiscounts> findContractDiscounts(
        List<Long> contractIds, LocalDate billingStartDate, LocalDate billingEndDate
    ) {
        List<ContractDiscountDto> contractDiscountDtos =
            contractDiscountMapper.findDiscountsByContractIds(contractIds, billingStartDate, billingEndDate);
        return contractDiscountDtoConverter.convertToContractDiscounts(contractDiscountDtos);
    }

    @Override
    public void applyDiscount(Discount discount) {
        contractDiscountMapper.applyDiscount(discount);
    }
}