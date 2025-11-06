package me.realimpact.telecom.bill.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Getter
public enum BillingBillType {
    REGULAR("01", "정기청구"),
    REBILL("02", "재발행");

    private final String code;
    private final String description;

    BillingBillType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static BillingBillType fromCode(String code) {
        return Arrays.stream(BillingBillType.values())
                .filter(type -> type.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No enum constant with code " + code));
    }
}
