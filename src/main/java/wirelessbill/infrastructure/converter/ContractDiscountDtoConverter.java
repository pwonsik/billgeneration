package bill.infrastructure.converter;

import org.springframework.stereotype.Component;

import bill.domain.discount.ContractDiscounts;
import bill.domain.discount.Discount;
import bill.infrastructure.dto.ContractDiscountDto;
import bill.infrastructure.dto.DiscountDto;

import java.util.List;

/**
 * 계약 할인 도메인 객체와 DTO 간의 변환을 담당하는 컨버터
 */
@Component
public class ContractDiscountDtoConverter {

    /**
     * DiscountDto를 Discount 도메인 객체로 변환
     */
    public Discount convertToDiscount(DiscountDto dto) {
        return new Discount(
            dto.getContractId(),
            dto.getDiscountId(),
            dto.getDiscountStartDate(),
            dto.getDiscountEndDate(),
            dto.getProductOfferingId(),
            dto.getDiscountApplyUnit(),
            dto.getDiscountAmount(),
            dto.getDiscountRate(),
            dto.getDiscountAppliedAmount()
        );
    }

    /**
     * ContractDiscountDto를 ContractDiscount 도메인 객체로 변환
     */
    public ContractDiscounts convertToContractDiscount(ContractDiscountDto dto) {
        List<Discount> discounts = dto.getDiscounts().stream()
            .map(this::convertToDiscount)
            .toList();
            
        return new ContractDiscounts(
            dto.getContractId(),
            discounts
        );
    }

    /**
     * ContractDiscountDto 리스트를 ContractDiscount 도메인 객체 리스트로 변환
     */
    public List<ContractDiscounts> convertToContractDiscounts(List<ContractDiscountDto> dtos) {
        return dtos.stream()
            .map(this::convertToContractDiscount)
            .toList();
    }

}