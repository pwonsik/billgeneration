package common.domain;

/**
 * 청구서 발행 유형을 나타내는 enum
 * - REGULAR_BILL: 정기 청구서
 * - REISSUE_BILL: 재발행 청구서
 */
public enum BillIssueType {
    
    /**
     * 정기 청구서
     */
    REGULAR_BILL(1, "정기청구서"),
    
    /**
     * 재발행 청구서
     */
    REISSUE_BILL(2, "재발행청구서");
    
    private final int code;
    private final String displayName;
    
    BillIssueType(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getCode() {
        return code;
    }
    
    /**
     * 정기 청구서인지 확인
     */
    public boolean isRegularBill() {
        return this == REGULAR_BILL;
    }
    
    /**
     * 재발행 청구서인지 확인
     */
    public boolean isReissueBill() {
        return this == REISSUE_BILL;
    }
}