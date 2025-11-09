package common.domain;

/**
 * 채널 모드를 나타내는 enum
 * - ONLINE: 온라인 실시간 처리
 * - BATCH: 배치 일괄 처리
 */
public enum ChannelMode {
    
    /**
     * 온라인 실시간 처리 모드
     */
    ONLINE(1, "온라인"),
    
    /**
     * 배치 일괄 처리 모드
     */
    BATCH(2, "배치");
    
    private final int code;
    private final String displayName;
    
    ChannelMode(int code, String displayName) {
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
     * 온라인 모드인지 확인
     */
    public boolean isOnline() {
        return this == ONLINE;
    }
    
    /**
     * 배치 모드인지 확인
     */
    public boolean isBatch() {
        return this == BATCH;
    }
}