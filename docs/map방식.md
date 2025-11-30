
  이 버전에서는 Com01Data, Inv01Data 같은 클래스 대신, 모든 데이터를 Map<String, Object>에 담아 처리합니다.

  ---

  `Map` 사용 버전 설계

  1. 핸들러 인터페이스 변경

  createData 메소드가 제네릭 타입 대신 Map<String, Object>를 반환하도록 수정합니다.

   * `SectorDataHandler.java` (`Map` 버전)

   1     import java.util.Map;
   2
   3     // 반환 타입이 Map<String, Object>로 고정됩니다.
   4     public interface SectorDataHandler {
   5         String getSectorCode();
   6         Map<String, Object> createData(BillContext context);
   7     }
      (참고: 이전의 `SectorData` 인터페이스와 `Com01Data`, `Inv01Data` 클래스는 이 방식에서는 더 이상 필요 없습니다.)

  2. `Map`을 반환하는 핸들러 구현

  핸들러들은 이제 HashMap을 생성하고, 여기에 데이터를 put하여 반환합니다.

   * `Com01DataHandler.java` (`Map` 버전)
        @Component
        public class Com01DataHandler implements SectorDataHandler {
            @Override
            public String getSectorCode() {
                return "com01";
            }
   
            @Override
            public Map<String, Object> createData(BillContext context) {
                Map<String, Object> data = new HashMap<>();
                data.put("custNm", context.getCustomerName());
                data.put("prcplnNm", context.getPlanName());
                data.put("svcNum", context.getServiceNumber());
                return data;
            }
        }

   * `Inv01DataHandler.java` (`Map` 버전)
        @Component
        public class Inv01DataHandler implements SectorDataHandler {
            @Override
            public String getSectorCode() {
                return "inv01";
            }
   
            @Override
            public Map<String, Object> createData(BillContext context) {
                Map<String, Object> data = new HashMap<>();
   
                // List<AmountItem>도 List<Map<String, Object>> 형태로 변환
                List<Map<String, Object>> amtList = context.getAmountItems().stream()
                    .map(item -> {
                        Map<String, Object> amtItem = new HashMap<>();
                        amtItem.put("itm_cd", item.itm_cd());
                        amtItem.put("amount", item.amount());
                        return amtItem;
                    })
                    .collect(Collectors.toList());
   
                data.put("amts", amtList);
                data.put("totalAmount", context.getTotalAmount());
                return data;
            }
        }

  3. `Map`을 사용하는 서비스 계층

  BillInsertService는 핸들러로부터 Map을 받아 처리합니다. 특히 applyProcessing 메소드에서 `Map`을 직접 조작할 때의 단점이 명확하게 드러납니다.

   * `BillInsertService.java` (`Map` 버전)

        @Service
        public class BillInsertService {
            private final SectorDataHandlerFactory handlerFactory;
            private final TmthBillMapper billMapper;
            private final ObjectMapper objectMapper;
            private final SectorConfigProperties sectorConfigProperties;
   
            // ... 생성자 ...
   
            public void insertBill(String sectorCode, BillContext context, TmthBill billDetails) throws Exception {
                SectorDataHandler handler = handlerFactory.getHandler(sectorCode);
                // createData는 이제 Map<String, Object>를 반환
                Map<String, Object> data = handler.createData(context);
   
                // Map을 직접 수정하는 추가 처리
                Map<String, Object> processedData = applyProcessing(sectorCode, data);
   
                String billDataJson = objectMapper.writeValueAsString(processedData);
   
                // ... 이하 DB 저장 로직은 동일 ...
            }
   
            private Map<String, Object> applyProcessing(String sectorCode, Map<String, Object> data) {
                if ("com01".equals(sectorCode)) {
                    // yml에서 마스킹 속성을 가져옴
                    SectorFieldAttribute svcNumAttr = sectorConfigProperties.getFieldAttributesFor(sectorCode)
                                                        .flatMap(attrs -> Optional.ofNullable(attrs.get("svcNum")))
                                                        .orElse(null);
   
                    if (svcNumAttr != null && svcNumAttr.isMasking()) {
                        // 1. "매직 스트링" 키를 사용해 값 조회. 오타나면 null 반환.
                        Object svcNumObj = data.get("svcNum");
   
                        // 2. 타입이 맞는지 확인하고 수동으로 캐스팅.
                        if (svcNumObj instanceof String) {
                            String maskedSvcNum = maskServiceNumber((String) svcNumObj);
                            // 3. Map의 값을 새로운 값으로 교체. (원본 Map이 변경되는 Side Effect 발생)
                            data.put("svcNum", maskedSvcNum);
                        }
                    }
                }
                return data;
            }
   
            // ... maskServiceNumber 메소드 ...
        }

  ---

  두 방식 비교

  이제 두 가지 버전을 명확하게 비교하실 수 있습니다.

   * VO/빌더 버전:
       * Com01Data, Inv01Data 같은 명시적인 클래스가 있어 데이터 구조가 명확합니다.
       * 컴파일 시점에 타입 체크가 가능하여 안전합니다. (data.getSvcNum() vs data.get("svcNum"))
       * applyProcessing에서 instanceof와 getter를 사용해 안전하고 명확하게 로직을 작성할 수 있습니다.

   * `Map` 버전:
       * 별도의 VO 클래스를 작성하지 않아도 되어 초기에는 간단해 보입니다.
       * applyProcessing에서 보듯이, 문자열 키("매직 스트링")를 사용해야 하고, instanceof 타입 체크와 수동 캐스팅이 필요하며, 원본 데이터가 직접 수정되는 등 코드가 복잡해지고 오류 발생 가능성이 높아집니다.