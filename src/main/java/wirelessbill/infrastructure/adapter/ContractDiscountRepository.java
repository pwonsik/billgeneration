package wirelessbill.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import wirelessbill.domain.discount.ContractDiscounts;
import wirelessbill.domain.discount.Discount;
import wirelessbill.infrastructure.adapter.mybatis.ContractDiscountMapper;
import wirelessbill.infrastructure.converter.ContractDiscountDtoConverter;
import wirelessbill.infrastructure.dto.ContractDiscountDto;
import wirelessbill.port.out.ContractDiscountCommandPort;
import wirelessbill.port.out.ContractDiscountQueryPort;

import org.springframework.stereotype.Repository;

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