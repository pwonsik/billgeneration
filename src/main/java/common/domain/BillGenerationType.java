package common.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Getter
public enum BillGenerationType {
    WIRELESS_BILL("A01", "무선정기청구서", BillIssueType.REGULAR_BILL, ChannelMode.BATCH),
    WIRELESS_REBILL("A02", "무선정기청구서재발행", BillIssueType.REISSUE_BILL, ChannelMode.BATCH),
    WIRELED_BILL("B01", "통합/유선정기청구서", BillIssueType.REGULAR_BILL, ChannelMode.BATCH),
    WIRELED_REBILL("B02", "통합/유선정기청구서재발행", BillIssueType.REISSUE_BILL, ChannelMode.BATCH),
    TERM_GNRL_BILL("C01", "일반해지미납청구서", BillIssueType.REGULAR_BILL, ChannelMode.BATCH),
    TERM_GNRL_REBILL("C02", "일반해지미납재발행청구서", BillIssueType.REISSUE_BILL, ChannelMode.BATCH),
    TERM_EQP_BILL("D01", "단말해지미납청구서", BillIssueType.REGULAR_BILL, ChannelMode.BATCH),
    TERM_EQP_REBILL("D02", "단말해지미납재발행청구서", BillIssueType.REISSUE_BILL, ChannelMode.BATCH),
    REAL_ACNT_BILL("R01", "청구서별청구정보조회", BillIssueType.REGULAR_BILL, ChannelMode.ONLINE),
    REAL_SVC_BILL("R02", "서비스별청구정보조회", BillIssueType.REGULAR_BILL, ChannelMode.ONLINE);

    private final String code;
    private final String description;
    private final BillIssueType issueType;
    private final ChannelMode channelMode;

    BillGenerationType(String code, String description, BillIssueType issueType, ChannelMode channelMode) {
        this.code = code;
        this.description = description;
        this.issueType = issueType;
        this.channelMode = channelMode;
    }

    public static BillGenerationType fromCode(String code) {
        return Arrays.stream(BillGenerationType.values())
                .filter(period -> period.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No enum constant with code " + code));
    }

    /**
     * 온라인 모드인지 확인
     */
    public boolean isOnline() {
        return this.channelMode.isOnline();
    }

    /**
     * 배치 모드인지 확인
     */
    public boolean isBatch() {
        return this.channelMode.isBatch();
    }

    /**
     * 정기 청구서인지 확인
     */
    public boolean isRegularBill() {
        return this.issueType.isRegularBill();
    }

    /**
     * 재발행 청구서인지 확인
     */
    public boolean isReissueBill() {
        return this.issueType.isReissueBill();
    }
}
