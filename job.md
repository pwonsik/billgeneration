## 작업실행
.\gradlew :batch:bootRun --args="--spring.batch.job.name=billOperNumAssignJob --batch.thread-count=4 invOperCyclCd=01"

##  STS 파라미터
--spring.batch.job.name=billOperNumAssignJob 
--batch.thread-count=10
invOperCyclCd=01