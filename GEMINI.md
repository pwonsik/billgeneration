# GEMINI.md — Telecom Billing Project Context

## 📘 프로젝트 개요

## 🧩 기술 스택
- **Java 21*
- **Spring Boot 3.x**
- **Spring Batch**
- **MyBatis
- **Oracle 23c (JSON 컬럼 포함)**
- 헥사고날 아키텍쳐를 사용


## 🧠 Gemini 지시 사항
Gemini는 이 프로젝트에서 다음 원칙을 따르도록 합니다:

### 1. 코드 스타일
- **Google Java Style Guide** 준수  
- 체이닝 메서드는 줄바꿈  
- 인덴트 4칸  
- 로깅은 `Slf4j` 사용 (`log.info`, `log.debug`)  
- 주석은 한글 가능, 단 기술용어는 영어 병기  

## 🧠 Gemini 지시 사항
소스는 C:\Users\user\Documents\GitHub\billgeneration에 있어.
소스에 대한 개선을 물어볼때 소스를 바로 수정하지 말고 일단 소스를 보여줘.
소스를 보여줄때는 복사하기 쉽게 라인넘버를 빼고 보여줘
설명은 한글로 해줘
윈도우 환경에서 wsl로 우분투를 실행해서 Gemini를 실행함. 따라서 gradle 빌드같은 명령을 스스로 실행하면 오류가 날수 있음. 명령은 직접 실행 하지 말것
@Bean은 public 로 하지말고 default 로 할것