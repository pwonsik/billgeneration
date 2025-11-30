1. 설정 (Configuration)
  📁 src/main/resources/sector.yml
    bill-structures:
      com01:
        description: "공통정보섹터"
        fields:
          custNm:      { name: "고객명", order: 1, type: "String", masking: false, used: true }
          prcplnNm:    { name: "요금제명", order: 2, type: "String", masking: false, used: true }
          svcNum:      { name: "서비스번호", order: 3, type: "String", masking: true, used: true }

      inv01:
        description: "계정별 요금정보섹터"
        fields:
          amt:         { name: "요금항목", order: 1, type: "List", masking: false, used: true }
          totalAmount: { name: "총 금액", order: 2, type: "long", masking: false, used: true }

      

  📁 src/main/java/.../config/YamlPropertySourceFactory.java
    // 커스텀 yml 파일을 읽기 위한 팩토리 클래스 (한번 만들어두면 재사용)
    public class YamlPropertySourceFactory implements PropertySourceFactory {
    
        @Override
        public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(encodedResource.getResource());
            Properties properties = factory.getObject();
            return new PropertiesPropertySource(encodedResource.getResource().getFilename(), properties);
        }
    }


  📁 src/main/java/.../config/SectorConfigProperties.java
    @Component
    @PropertySource(value = "classpath:sector.yml", factory = YamlPropertySourceFactory.class)
    @ConfigurationProperties(prefix = "bill-structures")
    @Getter
    @Setter
    public class SectorConfigProperties {

        private Map<String, SectorDefinition> sectors;

        public Optional<SectorDefinition> getDefinitionFor(String sectorCode) {
            return Optional.ofNullable(sectors.get(sectorCode));
        }

        public Optional<Map<String, SectorFieldAttribute>> getFieldAttributesFor(String sectorCode) {
            return getDefinitionFor(sectorCode).map(SectorDefinition::getFields);
        }
    }

  📁 src/main/java/.../config/SectorDefinition.java
    @Getter
    @Setter  
    public class SectorDefinition {
        private String description;
        private Map<String, SectorFieldAttribute> fields;
    }

  📁 src/main/java/.../config/SectorFieldAttribute.java`
    @Getter
    @Setter
    public class SectorFieldAttribute {`
        private String name;`
        private int order;`
        private String type;`
        private boolean masking;`
        private boolean used;`
    }

  2. 데이터 모델 (Domain Models)

  📁 src/main/java/.../domain/SectorData.java

     // 모든 섹터 데이터 모델이 구현할 마커 인터페이스
     public interface SectorData {
     }


  📁 src/main/java/.../domain/Com01Data.java
        @Value   // 모든 필드를 private final로 만들고, Getter, equals, hashCode, toString, AllArgsConstructor를 자동으로 생성
        @Builder // Builder 패턴을 자동으로 생성해 줍니다.
        public class Com01Data implements SectorData {
            String custNm;
            String prcplnNm;
            String svcNum;
            // ... 필드가 많아도 Lombok이 Getter, Builder 등 모든 코드를 자동으로 처리합니다.
        }


  📁 src/main/java/.../domain/Inv01Data.java

     public record AmountItem(
         String itm_cd,
         long amount
     ) {}
   
     @Value
     @Builder
     public class Inv01Data implements SectorData {
    
         // @Singular: 리스트에 아이템을 하나씩 추가하는 메소드(amt())를 만들어줍니다.
         // 관례적으로 컬렉션 필드명은 복수형(amts)으로 짓는 것이 좋습니다.
         @Singular
         List<AmountItem> amts;
    
         long totalAmount;
     }

    📁 src/main/java/.../domain/BillContext.java
    데이터 생성에 필요한 모든 정보를 담는 BillContext를 사용합니다.
        @Getter
        @Setter
        public class BillContext {
            private String customerName;
            private String planName;
            private String serviceNumber;
            // ... 다른 섹터 생성을 위한 추가 정보 필드들 ...
        }

  ---

  3. 데이터 생성 핸들러 (원래 버전 + `BillContext` 적용)
        public interface SectorDataHandler<T extends SectorData> {
            String getSectorCode();
            T createData(BillContext context); // BillContext를 받도록 변경
        }

   * `Com01DataHandler.java` (수정)
        @Component
        public class Com01DataHandler implements SectorDataHandler<Com01Data> {
   
            @Override
            public String getSectorCode() {
                return "com01";
            }
   
            @Override
            public Com01Data createData(BillContext context) {
                // 빌더 패턴을 사용하여 Com01Data 객체 생성
                return new Com01Data.Builder()
                    .custNm(context.getCustomerName())
                    .prcplnNm(context.getPlanName())
                    .svcNum(context.getServiceNumber())
                    .build();
            }
        }


  2. `Inv01DataHandler.java` (Lombok Builder 사용)
    @Component
    public class Inv01DataHandler implements SectorDataHandler<Inv01Data> {
   
        @Override
        public String getSectorCode() {
            return "inv01";
        }
   
        @Override
        public Inv01Data createData(BillContext context) {
            return Inv01Data.builder()
                .amts(context.getAmountItems()) // 리스트 전체를 설정하는 방법
                .totalAmount(context.getTotalAmount())
                .build();
        }
    }


  4. 핸들러 팩토리 (원래 버전)

  새로운 섹터 핸들러가 추가되어도 코드를 수정할 필요가 없는 확장성 좋은 팩토리 버전을 그대로 사용합니다.

   * `SectorDataHandlerFactory.java`
        @Component
        public class SectorDataHandlerFactory {
   
            private final List<SectorDataHandler<?>> handlers;
            private Map<String, SectorDataHandler<?>> handlerMap;
   
            public SectorDataHandlerFactory(List<SectorDataHandler<?>> handlers) {
                this.handlers = handlers;
            }
   
            @PostConstruct
            public void init() {
                handlerMap = handlers.stream()
                    .collect(Collectors.toMap(SectorDataHandler::getSectorCode, Function.identity()));
            }
   
            public SectorDataHandler<?> getHandler(String sectorCode) {
                SectorDataHandler<?> handler = handlerMap.get(sectorCode);
                if (handler == null) {
                    throw new IllegalArgumentException("Unsupported sector code: " + sectorCode);
                }
                return handler;
            }
        }

  5. 서비스 계층 (원래 버전 + `BillContext` 적용)

  BillInsertService는 팩토리를 사용하여 핸들러를 가져오고, 핸들러에 BillContext를 전달합니다.

   * `BillInsertService.java` (최종)

        @Service
        public class BillInsertService {
            private final SectorDataHandlerFactory handlerFactory;
            private final TmthBillMapper billMapper;
            private final ObjectMapper objectMapper;
            private final SectorConfigProperties sectorConfigProperties;
   
            public BillInsertService(SectorDataHandlerFactory hf, TmthBillMapper bm, ObjectMapper om, SectorConfigProperties scp) {
                this.handlerFactory = hf;
                this.billMapper = bm;
                this.objectMapper = om;
                this.sectorConfigProperties = scp;
            }
   
            public void insertBill(String sectorCode, BillContext context, TmthBill billDetails) throws Exception {
                // 1. 팩토리에서 핸들러 조회
                SectorDataHandler<?> handler = handlerFactory.getHandler(sectorCode);
   
                // 2. 핸들러에 BillContext를 전달하여 데이터 객체 생성 (내부적으로 빌더 사용)
                SectorData data = handler.createData(context);
   
                // --- 이하 로직은 동일 ---
                SectorData processedData = applyProcessing(sectorCode, data);
                String billDataJson = objectMapper.writeValueAsString(processedData);
                billDetails.setSectClCd(sectorCode);
                billDetails.setBillData(billDataJson);
                billMapper.insertBill(billDetails);
            }
   
            private SectorData applyProcessing(String sectorCode, SectorData data) {
                var attributesOpt = sectorConfigProperties.getFieldAttributesFor(sectorCode);
                if (attributesOpt.isEmpty()) return data;
                Map<String, SectorFieldAttribute> attributes = attributesOpt.get();
       
                if (data instanceof Com01Data comData) {
                    SectorFieldAttribute svcNumAttr = attributes.get("svcNum");
                    if (svcNumAttr != null && svcNumAttr.isMasking()) {
                        String maskedSvcNum = maskServiceNumber(comData.svc_num());
                        return new Com01Data(comData.cust_nm(), comData.prcpln_nm(), maskedSvcNum);
                    }
                }
                return data;
            }
       
            private String maskServiceNumber(String svcNum) {
                if (svcNum != null && svcNum.matches("\\d{3}-\\d{3,4}-\\d{4}")) {
                    String[] parts = svcNum.split("-");
                    return parts[0] + "-****-" + parts[2];
                }
                return svcNum;
            }
        }

  📁 src/main/java/.../service/SectorInfoService.java
        @Service
        public class SectorInfoService {
            private final SectorConfigProperties sectorConfigProperties;
        
            public SectorInfoService(SectorConfigProperties scp) {
                this.sectorConfigProperties = scp;
            }
        
            public List<SectorDefinitionDto> getAllSectorDefinitionsForUI() {
                return sectorConfigProperties.getSectors().entrySet().stream()
                    .map(entry -> {
                        String sectorCode = entry.getKey();
                        SectorDefinition definition = entry.getValue();
                        List<SectorFieldAttributeDto> fieldDtos = definition.getFields().entrySet().stream()
                            .filter(fieldEntry -> fieldEntry.getValue().isUsed())
                            .map(fieldEntry -> {
                                String fieldKey = fieldEntry.getKey();
                                SectorFieldAttribute attr = fieldEntry.getValue();
                                return new SectorFieldAttributeDto(fieldKey, attr.getName(), attr.getOrder(), attr.getType(), attr.isMasking());
                            })
                            .sorted(Comparator.comparingInt(SectorFieldAttributeDto::order))
                            .collect(Collectors.toList());
                        return new SectorDefinitionDto(sectorCode, definition.getDescription(), fieldDtos);
                    })
                    .collect(Collectors.toList());
            }
        }

  📁 src/main/java/.../controller/SectorInfoController.java
    @RestController
    @RequestMapping("/api")
    public class SectorInfoController {
    
        private final SectorInfoService sectorInfoService;
    
        public SectorInfoController(SectorInfoService sectorInfoService) {
            this.sectorInfoService = sectorInfoService;
        }
       
        @GetMapping("/sectors")
        public List<SectorDefinitionDto> getSectorDefinitions() {
            return sectorInfoService.getAllSectorDefinitionsForUI();
        }
    }

    -- 리턴되는 데이타 형식
   [
     {
       "sectorCode": "com01",
       "description": "공통정보섹터",
       "fields": [
         {
           "key": "custNm",
           "name": "고객명",
           "order": 1,
           "type": "String",
           "masking": false
         },
         {
           "key": "prcplnNm",
           "name": "요금제명",
           "order": 2,
           "type": "String",
           "masking": false
         },
         {
           "key": "svcNum",
           "name": "서비스번호",
           "order": 3,
           "type": "String",
           "masking": true
         }
       ]
     },
     {
       "sectorCode": "inv01",
       "description": "계정별 요금정보섹터",
       "fields": [
         {
           "key": "amt",
           "name": "요금항목",
           "order": 1,
           "type": "List",
           "masking": false
         },
         {
           "key": "totalAmount",
           "name": "총 금액",
           "order": 2,
           "type": "long",
           "masking": false
         }
       ]
     }
   ]




  📁 src/main/java/.../controller/dto/SectorDefinitionDto.java

   1 import java.util.List;
   2
   3 public record SectorDefinitionDto(
   4     String sectorCode,
   5     String description,
   6     List<SectorFieldAttributeDto> fields
   7 ) {}
  📁 src/main/java/.../controller/dto/SectorFieldAttributeDto.java

   1 public record SectorFieldAttributeDto(
   2     String key,
   3     String name,
   4     int order,
   5     String type,
   6     boolean masking
   7 ) {}
  ---

  5. 영속성 (Persistence)

  📁 src/main/java/.../persistence/TmthBill.java

    1 // TMTH_BILL 테이블에 매핑되는 DTO 클래스
    2 public class TmthBill {
    3     private long acntNum;
    4     private String sectClCd;
    5     private long serNum;
    6     private String billOperNum;
    7     private String billData;  // JSON을 문자열로 전달
    8     private String billTypes;
    9     private long cmbeAcntNum;
   10
   11     // Getters and Setters
   12 }

  📁 src/main/java/.../persistence/TmthBillMapper.java

   1 import org.apache.ibatis.annotations.Mapper;
   2
   3 @Mapper
   4 public interface TmthBillMapper {
   5     void insertBill(TmthBill bill);
   6 }

  📁 src/main/resources/mybatis/mapper/TmthBillMapper.xml

    1 <?xml version="1.0" encoding="UTF-8" ?>
    2 <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    3 <mapper namespace="your.package.path.persistence.TmthBillMapper">
    4     <insert id="insertBill" parameterType="your.package.path.persistence.TmthBill">
    5         INSERT INTO DEV.TMTH_BILL (
    6             ACNT_NUM, SECT_CL_CD, SER_NUM, BILL_OPER_NUM,
    7             BILL_DATA, BILL_TYPES, CMBE_ACNT_NUM
    8         ) VALUES (
    9             #{acntNum}, #{sectClCd}, #{serNum}, #{billOperNum},
   10             #{billData, jdbcType=JSON},
   11             #{billTypes, jdbcType=JSON},
   12             #{cmbeAcntNum}
   13         )
   14     </insert>
   15 </mapper>





     > 그런데 어떤 데서는 yml의 정보를 이용해서 VO를 안 만들기도 하던데.  map을 사용해서, 이거는 어떻게 생각해? 사실 이거 때문에 회사에서 사람들하고 다툼이 있어

✦ 네, 그 부분은 실무에서 정말 자주 발생하는 고전적인 논쟁입니다. 어느 한쪽이 무조건 틀렸다고 말하기보다는, 각 방식의 장단점과 어떤 상황에 더 적합한지를 이해하는 것이 중요합니다. 이 문제를 명확하게 정리해 드리겠습니다. 동료분들과 논의하실 때 좋은 자료가 될 것입니다.

  결론부터 말씀드리면, 안정성과 유지보수가 중요한 시스템에서는 VO/Record 방식이 `Map` 방식보다 훨씬 우수합니다.

  Map을 사용하는 동료분들의 의견도 충분히 일리가 있습니다. 그분들은 아마 Map의 '유연성'과 '초기 개발 속도'를 높게 평가하고 있을 것입니다. 하지만 이 장점들은 장기적인 관점에서 심각한 단점들을 동반합니다.

  VO/Record 방식 vs. `Map` 방식: 장단점 비교


  ┌──────────────────┬──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┬───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
  │ 항목             │ Map 사용 방식                                                                                                                                                            │ VO/Record 사용 방식 (현재 설계)
  │
  ├──────────────────┼──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ 타입 안정성      │ 없음 (치명적 단점)<br>- "cust_nm"을 "cust_name"으로 오타내도 컴파일 시 알 수 없고, 런타임에 null이 반환되어 버그 발생<br>- 데이터 타입을 잘못 캐스팅하면 `ClassCastExce... │ 강력함 (결정적 장점)<br>- data.custNm() 처럼 필드명이 잘못되면 컴파일 에러 발생<br>- 정해진 타입(String, long 등)만  사... │
  │ 코드 가독성      │ 낮음<br>- map.get("some_key") 처럼 "매직 스트링"을 사용<br>- 어떤 키와 값이 들어있는지 알려면 코드를 다 파헤쳐야 함                                                      │ 높음<br>- Com01Data 라는 클래스/레코드 이름만 봐도 데이터의 의도가 명확함<br>- data.custNm() 처럼 사용 가능한 필드가 ...
  │
  │ **유지보수/리팩... │ 어려움<br>- 필드명 변경 시, 문자열 검색으로 모든 코드를 수동으로 찾아 바꿔야 하므로 실수가 발생하기 쉬움                                                                 │ 쉬움<br>- IDE의 리팩토링 기능으로 한 번에 안전하게 모든 참조 변경 가능
  │
  │ IDE 지원         │ 거의 없음<br>- 자동 완성, 타입 체크, 사용처 찾기 등 IDE의 도움을 받기 어려움                                                                                             │ 최대 활용<br>- 자동 완성, 타입 체크, 사용처 찾기 등 IDE의 모든 기능을 활용하여 생산성 향상
  │
  │ 초기 개발 속도   │ 빠른 것처럼 보임<br>- 별도의 클래스 파일 생성 없이 Map에 put만 하면 되므로 초반엔 빠르다고 느낄 수 있음                                                                  │ 상대적으로 느림<br>- Com01Data.java 같은 파일을 섹터마다 만들어야 하는 수고가 있음 (하지만 Record 문법으로 매우 간결해짐)
  │
  │ 안정성           │ 낮음<br>- 수많은 잠재적 런타임 버그를 안고 가게 됨. 테스트로 모든 케이스를 잡기 어려움                                                                                   │ 높음<br>- 컴파일러가 1차적으로 버그를 걸러주므로 매우 안정적임
  │
  └──────────────────┴──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────  ┘

  결론 및 제언

  동료분들의 Map 방식 주장은 "빠르고 유연하다"는 점에서 출발했을 가능성이 높습니다. 이는 데이터 구조가 아직 확정되지 않았거나, 아주 작은 일회성 스크립트를 작성할 때는 유효할 수 있습니다.

  하지만 지금 설계하고 계신 청구 시스템처럼 데이터의 구조가 명확하고(com01, inv01 등), 앞으로 안정적으로 운영되고 확장되어야 하는 시스템에서는 Map 방식의 단점이 장점을 완전히 압도합니다. Map을 사용해서 얻는 약간의 초기 개발 속도는 나중에 발생할 디버깅 시간, 런타임 장애, 유지보수 비용으로 인해 모두 상쇄되고 오히려
  더 큰 비용을 치르게 됩니다. 이를 "기술 부채(Technical Debt)"라고 부릅니다.

  동료분들과의 논쟁에서 이렇게 포인트를 짚어보세요.

  > "Map을 쓰면 당장 클래스 파일을 안 만들어도 되니 빠른 것 같지만, cust_nm을 customer_name으로 오타를 내거나 타입을 잘못 넣었을 때 컴파일러가 잡아주지 못합니다. 이런 버그는 결국 테스트나 운영 중에 터지게 되어 더 큰 비용을 발생시킵니다. 반면, Com01Data 같은 Record를 쓰면 이런 실수는 개발 중에 즉시 발견할 수 있어
  시스템 전체의 안정성이 크게 올라갑니다. 장기적으로 유지보수하고 확장할 시스템이라면 어떤 방식이 더 안전할까요?"

  Java라는 언어 자체가 타입 안정성을 매우 중요하게 생각하며 발전해왔고, 최근에 추가된 Record 문법은 VO 방식의 단점(보일러플레이트 코드)을 획기적으로 줄여주었기 때문에, 현재 설계하신 VO/Record 방식이 기술적으로나 실용적으로나 훨씬 더 성숙하고 올바른 접근 방식입니다.


  > 그리고 이젠 다른 문제가 있어.
  청구서를 생성할때 각 섹터를 만드는 조건이 있어.
  com01은 항상 만들고, inv01은 금액이 존재할때만 만들고, psc01은 소액결제 내역이 존재하고 단수일때만 만들고..
  이런 로직들이 섹터를 생성할때마다 들어가 잇으니까 뭔가 큰 거를 고치면 각 섹터 생성 클래스를 다 고쳐야 되는거야.
  그래서 생터 생성 조건들을 하나의 클래스로 만들고 그 클래스를 읽는 방식으로 하면..
  뭔가 변화가 있어도 클래스 하나만 고치면 되지 않을까 싶어.
  이거 설계 가능하겠어?

✦ 네, 아주 좋은 생각입니다. 현재 각 섹터 생성 로직에 생성 '조건'이 흩어져 있어 유지보수가 어려운 문제를 겪고 계신 것 같습니다.

  말씀하신 대로, 각 섹터의 생성 조건을 별도의 클래스로 분리하고 중앙에서 관리하도록 설계를 변경하면, 향후 로직이 변경되더라도 조건 클래스 하나만 수정하면 되므로 시스템이 훨씬 유연하고 견고해집니다.

  이러한 요구사항은 스페시피케이션 패턴(Specification Pattern)을 적용하여 우아하게 해결할 수 있습니다. 각 섹터가 생성되어야 하는 '규격' 또는 '조건'을 독립적인 클래스로 만드는 것입니다.

  전체적인 설계를 제안해 드리겠습니다.

  ---

  설계 제안: 섹터 생성 조건 중앙 관리 (Specification Pattern 적용)

  1. '생성 조건'을 정의하는 스페시피케이션(Specification) 인터페이스

  모든 섹터 생성 조건 클래스가 구현해야 할 공통 인터페이스입니다. isSatisfiedBy 메소드는 주어진 BillContext가 특정 섹션을 생성할 조건을 만족하는지 여부를 반환합니다.

   * `SectorCreationSpecification.java`

    1     /**
    2      * 특정 섹터가 생성될 조건을 정의하는 스페시피케이션 인터페이스
    3      */
    4     public interface SectorCreationSpecification {
    5         /**
    6          * 주어진 컨텍스트가 이 섹션의 생성 조건을 만족하는지 확인합니다.
    7          * @param context 청구서 생성에 필요한 모든 정보가 담긴 컨텍스트
    8          * @return 조건을 만족하면 true, 그렇지 않으면 false
    9          */
   10         boolean isSatisfiedBy(BillContext context);
   11
   12         /**
   13          * 이 스페시피케이션이 적용될 섹터 코드를 반환합니다.
   14          * @return 섹터 코드
   15          */
   16         String getSectorCode();
   17     }

  
   공통 조건을 처리하는 추상 스페시피케이션 클래스

  모든 구체적인 Specification 클래스들이 상속받을 추상 클래스를 만듭니다. 이 클래스에서 모든 공통/글로벌 조건을 먼저 처리하고, 그 후에 각 섹터별 고유 조건을 호출하는 구조를 만듭니다.

   * `AbstractSectorCreationSpecification.java`

    1     // SectorCreationSpecification 인터페이스를 구현하는 추상 클래스
    2     public abstract class AbstractSectorCreationSpecification implements SectorCreationSpecification {
    3
    4         /**
    5          * 최종 생성 여부를 결정하는 템플릿 메소드 (final로 선언하여 하위 클래스가 수정하지 못하게 함)
    6          */
    7         @Override
    8         public final boolean isSatisfiedBy(BillContext context) {
    9
   10             // 1. 모든 섹터에 적용될 공통/글로벌 조건을 먼저 체크합니다.
   11             //    이곳에 공통 로직을 중앙 관리합니다.
   12             if (isAccountSuspended(context)) {
   13                 return false; // 예: 계정이 정지 상태이면 어떤 섹터도 생성하지 않음
   14             }
   15             if (isTestAccount(context)) {
   16                 return false; // 예: 테스트 계정이면 생성하지 않음
   17             }
   18
   19             // 2. 공통 조건을 모두 통과하면, 각 섹터의 개별 조건을 체크하도록 위임합니다.
   20             return checkSpecificConditions(context);
   21         }
   22
   23         // --- 공통 조건 메소드들 (private) ---
   24         private boolean isAccountSuspended(BillContext context) {
   25             // 계정 정지 여부 확인 로직
   26             return "SUSPENDED".equals(context.getAccountStatus());
   27         }
   28
   29         private boolean isTestAccount(BillContext context) {
   30             // 테스트 계정 여부 확인 로직
   31             return context.isTestAccount();
   32         }
   33
   34         // --- 각 하위 클래스에서 반드시 구현해야 할 추상 메소드 ---
   35         /**
   36          * 각 섹터의 '고유한' 생성 조건을 확인합니다.
   37          * 공통 조건은 이미 isSatisfiedBy에서 처리되었으므로, 이 메소드는 순수하게 개별 조건에만 집중합니다.
   38          * @param context 청구서 생성 컨텍스트
   39          * @return 개별 조건을 만족하면 true
   40          */
   41         protected abstract boolean checkSpecificConditions(BillContext context);
   42     }

  2. 각 섹터별 생성 조건 구현체 (수정)

  이제 각 섹터의 Specification 클래스들은 AbstractSectorCreationSpecification를 상속받고, checkSpecificConditions 메소드만 구현하면 됩니다. 공통 로직에 대해 신경 쓸 필요가 없어집니다.

   * `Inv01CreationSpecification.java` (수정)

    1     import org.springframework.stereotype.Component;
    2
    3     @Component
    4     public class Inv01CreationSpecification extends AbstractSectorCreationSpecification {
    5
    6         @Override
    7         protected boolean checkSpecificConditions(BillContext context) {
    8             // 이 클래스는 이제 'inv01' 고유의 조건에만 집중합니다.
    9             // (공통 조건은 부모 클래스에서 이미 처리됨)
   10             return context.getTotalAmount() != null && context.getTotalAmount() > 0;
   11         }
   12
   13         @Override
   14         public String getSectorCode() {
   15             return "inv01";
   16         }
   17     }

   * `Psc01CreationSpecification.java` (수정)

    1     import org.springframework.stereotype.Component;
    2
    3     @Component
    4     public class Psc01CreationSpecification extends AbstractSectorCreationSpecification {
    5
    6         @Override
    7         protected boolean checkSpecificConditions(BillContext context) {
    8             // 'psc01' 고유의 조건에만 집중합니다.
    9             return context.hasMicropayments() && context.isSingularCase();
   10         }
   11
   12         @Override
   13         public String getSectorCode() {
   14             return "psc01";
   15         }
   16     }



   4. 전체 청구서 생성을 총괄하는 서비스

  이 서비스는 청구서 생성을 시작하는 진입점 역할을 합니다. 모든 섹터에 대해 생성 조건을 확인하고, 조건을 만족하는 섹터만 생성을 위임합니다.

   * `BillCreationService.java`

    1     import org.springframework.stereotype.Service;
    2     import java.util.Set;
    3
    4     @Service
    5     public class BillCreationService {
    6
    7         private final SpecificationFactory specFactory;
    8         private final SectorDataHandlerFactory handlerFactory;
    9         private final BillInsertService billInsertService; // 이전 단계에서 만든, 단일 섹터를 저장하는 서비스
   10
   11         public BillCreationService(SpecificationFactory sf, SectorDataHandlerFactory hf, BillInsertService bis) {
   12             this.specFactory = sf;
   13             this.handlerFactory = hf;
   14             this.billInsertService = bis;
   15         }
   16
   17         /**
   18          * 하나의 청구서에 포함될 모든 섹터를 생성합니다.
   19          */
   20         public void createBill(BillContext context, TmthBill billDetailsTemplate) throws Exception {
   21
   22             // 1. 생성 가능한 모든 섹터 코드 목록을 가져옵니다. (핸들러 팩토리에서)
   23             Set<String> allSectorCodes = handlerFactory.getAvailableSectorCodes(); // 이 메소드는 팩토리에 추가 필요
   24
   25             // 2. 각 섹터에 대해 생성 조건을 확인하고, 조건 충족 시에만 생성/저장
   26             for (String sectorCode : allSectorCodes) {
   27                 SectorCreationSpecification spec = specFactory.getSpecification(sectorCode);
   28
   29                 // 3. 스페시피케이션을 통해 생성 여부 판단
   30                 if (spec != null && spec.isSatisfiedBy(context)) {
   31                     // 4. 조건 충족 시, 이전에 만든 BillInsertService를 호출하여 섹터 생성 및 저장
   32                     billInsertService.insertBill(sectorCode, context, billDetailsTemplate);
   33                 }
   34             }
   35         }
   36     }
      (`SectorDataHandlerFactory`에 `getAvailableSectorCodes()` 메소드 추가가 필요합니다.)

  5. 전체 흐름 요약

   1. 외부에서 BillCreationService.createBill()을 호출하며 BillContext를 전달합니다.
   2. BillCreationService는 생성 가능한 모든 섹터 목록을 가져옵니다.
   3. 각 섹터에 대해 SpecificationFactory에서 해당 섹터의 생성 조건(Specification) 객체를 가져옵니다.
   4. spec.isSatisfiedBy(context)를 호출하여 생성 여부를 결정합니다.
   5. 결과가 true이면, SectorDataHandlerFactory와 BillInsertService를 통해 해당 섹터의 데이터를 만들고 DB에 저장합니다. false이면 아무 작업도 하지 않고 다음 섹터로 넘어갑니다.

  이렇게 하면, 각 섹터의 생성 조건이 명확하게 분리되어 테스트하기 쉽고, 새로운 조건이 추가되거나 기존 조건이 변경되어도 해당 Specification 클래스 하나만 수정하면 되므로 유지보수성이 크게 향상됩니다.



    ------
> 그리고 또 하나 있는데
  청구서미생성 조건이라는게 있어
  근데 이게 조건이 매우 복잡하고, if문이 많아서 보기도 싫어.
  그래서 이것만 클래스로 뽑고 싶어.
  조건은 예를 들어서 2개월간 금액이 없거나, 우편청구서면서 한달 이상 부달이 되었거나, 총결제금액이 500원 미만이거나, 자동납면 100미만이건나.. 이런 것들이 쭉 있어.
  이거를 또 별도 클래스로 뽑아내서 좀더 쉽게 관리를 하고 싶어

✦ 네, 그 문제 역시 이전의 '섹터 생성 조건'과 마찬가지로, 복잡한 비즈니스 규칙을 중앙에서 관리하고 가독성을 높이는 방향으로 설계하면 좋습니다.

  여러 if문이 중첩되어 있는 코드는 이해하기 어렵고, 새로운 규칙을 추가하거나 기존 규칙을 변경할 때 실수를 유발하기 쉽습니다. 말씀하신 대로 이 복잡한 '청구서 미생성 조건' 로직을 별도의 클래스로 뽑아내면 관리가 훨씬 수월해집니다.

  이 문제 또한 스페시피케이션 패턴(Specification Pattern)을 활용하되, 여러 개의 작은 규칙들을 조합(Composition)하여 최종 조건을 만드는 방식으로 해결할 수 있습니다. 각 조건을 독립적인 부품처럼 만들고, 이 부품들을 조립하여 최종 규칙을 만드는 것입니다.

  설계 제안: 청구서 미생성 조건 리팩토링

  1. 제네릭 스페시피케이션 인터페이스

  어떤 종류의 객체(T)에 대한 조건을 만족하는지 검사할 수 있는 범용 인터페이스를 만듭니다. 여기에 and, or, not과 같은 기본 로직을 default 메소드로 추가하면, 여러 규칙을 마치 문장처럼 쉽게 조합할 수 있습니다.

   * `Specification.java`

    1     /**
    2      * 특정 객체(T)가 조건을 만족하는지 검사하는 스페시피케이션의 제네릭 인터페이스
    3      */
    4     @FunctionalInterface // 단일 추상 메소드를 가지므로 함수형 인터페이스로 선언 가능
    5     public interface Specification<T> {
    6
    7         boolean isSatisfiedBy(T item);
    8
    9         default Specification<T> and(Specification<T> other) {
   10             return (item) -> this.isSatisfiedBy(item) && other.isSatisfiedBy(item);
   11         }
   12
   13         default Specification<T> or(Specification<T> other) {
   14             return (item) -> this.isSatisfiedBy(item) || other.isSatisfiedBy(item);
   15         }
   16
   17         default Specification<T> not() {
   18             return (item) -> !this.isSatisfiedBy(item);
   19         }
   20     }

  2. 개별 미생성 규칙을 클래스로 분리

  말씀하신 복잡한 조건들을 각각의 작은 클래스로 분리합니다. 각 클래스는 단 하나의 책임만 가집니다.

   * `AmountMissingForTwoMonths.java` (2개월간 금액 미존재 조건)

   1     public class AmountMissingForTwoMonths implements Specification<BillContext> {
   2         @Override
   3         public boolean isSatisfiedBy(BillContext context) {
   4             // 2개월간 청구 금액이 없었는지 확인하는 로직
   5             return context.hasNoAmountForMonths(2);
   6         }
   7     }

   * `UndeliverablePaperBill.java` (우편청구서 반송 조건)

   1     public class UndeliverablePaperBill implements Specification<BillContext> {
   2         @Override
   3         public boolean isSatisfiedBy(BillContext context) {
   4             return context.isPaperBill() && context.isUndeliverableForMonths(1);
   5         }
   6     }

   * `TotalPaymentBelowThreshold.java` (총 결제금액 기준 미달 조건)

    1     public class TotalPaymentBelowThreshold implements Specification<BillContext> {
    2         private final long threshold;
    3
    4         public TotalPaymentBelowThreshold(long threshold) {
    5             this.threshold = threshold;
    6         }
    7
    8         @Override
    9         public boolean isSatisfiedBy(BillContext context) {
   10             return context.getTotalPayment() < threshold;
   11         }
   12     }

   * `AutoPaymentBelowThreshold.java` (자동납부 기준 미달 조건)

    1     public class AutoPaymentBelowThreshold implements Specification<BillContext> {
    2         private final long threshold;
    3
    4         public AutoPaymentBelowThreshold(long threshold) {
    5             this.threshold = threshold;
    6         }
    7
    8         @Override
    9         public boolean isSatisfiedBy(BillContext context) {
   10             return context.isAutoPayment() && context.getTotalPayment() < threshold;
   11         }
   12     }
      (`BillContext`에는 이 규칙들을 검사하는 데 필요한 정보(메소드)들이 추가되어야 합니다.)

  3. 규칙들을 조합하여 최종 정책(Policy) 생성

  이제 분리된 작은 규칙 클래스들을 조합하여 '청구서 미생성'이라는 최종 정책을 만듭니다. if문으로 가득했던 코드가 훨씬 읽기 쉬운 코드로 바뀝니다.

   * `BillNonGenerationPolicy.java`

    1     public class BillNonGenerationPolicy {
    2
    3         /**
    4          * 청구서를 생성하지 않을 최종 조건을 반환합니다.
    5          * @return 조합된 스페시피케이션
    6          */
    7         public Specification<BillContext> getPolicy() {
    8             // 각 미생성 조건을 객체로 생성
    9             Specification<BillContext> amountMissing = new AmountMissingForTwoMonths();
   10             Specification<BillContext> undeliverable = new UndeliverablePaperBill();
   11             Specification<BillContext> totalPaymentLow = new TotalPaymentBelowThreshold(500);
   12             Specification<BillContext> autoPaymentLow = new AutoPaymentBelowThreshold(100);
   13
   14             // or 메소드를 사용하여 여러 조건을 마치 문장처럼 조합
   15             // "금액이 2달간 없거나, 또는 우편물이 반송되었거나, 또는 총 결제액이 500원 미만이거나, ..."
   16             return amountMissing
   17                     .or(undeliverable)
   18                     .or(totalPaymentLow)
   19                     .or(autoPaymentLow);
   20         }
   21     }

  4. 전체 프로세스에 통합

  청구서 생성을 총괄하는 서비스에서 이 정책을 사용하여, 가장 먼저 청구서를 생성할지 말지를 결정합니다.

   * `BillCreationService.java` (수정)

    1     @Service
    2     public class BillCreationService {
    3         // ... (다른 의존성)
    4
    5         public void createBill(BillContext context, ...) throws Exception {
    6
    7             // 1. 청구서 미생성 정책을 가져와서 조건을 확인합니다.
    8             BillNonGenerationPolicy nonGenerationPolicy = new BillNonGenerationPolicy();
    9             Specification<BillContext> nonGenerationRule = nonGenerationPolicy.getPolicy();
   10
   11             if (nonGenerationRule.isSatisfiedBy(context)) {
   12                 // log.info("청구서 미생성 조건에 해당하여 생성을 건너<binary data, 8 bytes><binary data, 8 bytes><binary data, 8 bytes>니다.");
   13                 return; // 조건에 해당하면 청구서 생성 프로세스를 즉시 종료
   14             }
   15
   16             // 2. 미생성 조건에 해당하지 않으면, 이전에 설계한 '섹터별 생성' 로직을 진행합니다.
   17             // ... (for-loop를 돌며 SectorCreationSpecification을 확인하는 로직) ...
   18         }
   19     }

  개선 효과

   * 가독성: 복잡한 if문이 rule1.or(rule2).or(rule3) 처럼 읽기 쉬운 코드로 변경됩니다.
   * 유지보수성: "자동납부 금액 기준이 100원에서 200원으로 변경"되면 new AutoPaymentBelowThreshold(100) 이 부분만 200으로 바꾸면 됩니다. 새로운 미생성 조건이 추가되어도 작은 클래스 하나와 .or() 한 줄만 추가하면 됩니다.
   * 테스트 용이성: 각각의 작은 규칙(AmountMissingForTwoMonths 등)을 독립적으로 테스트할 수 있어 코드의 신뢰성이 높아집니다.