-- inv_obj_acnt 테이블에 10,000건의 샘플 데이터를 삽입하는 PL/SQL 스크립트
--SET SERVEROUTPUT ON;

DECLARE
  v_pay_mthd_cd        inv_obj_acnt.pay_mthd_cd%TYPE;
  v_bill_types         inv_obj_acnt.bill_types%TYPE;
  v_temp_bill_types    VARCHAR2(20);
  v_svc_cnt            inv_obj_acnt.svc_cnt%TYPE;
  v_rep_svc_mgmt_num   inv_obj_acnt.rep_svc_mgmt_num%TYPE;
  v_inv_oper_cycl_cd   inv_obj_acnt.inv_oper_cycl_cd%TYPE;
  v_random_dist        NUMBER;
BEGIN
  -- 10,000건의 데이터를 생성하기 위한 루프
  FOR i IN 1..10000 LOOP
    -- 대표서비스관리번호 생성 (임의의 10자리 숫자)
    v_rep_svc_mgmt_num := TRUNC(DBMS_RANDOM.VALUE(1000000000, 10000000000));

    -- 납부방법코드 생성 ('01', '02', '03', '04' 중 임의 선택)
    v_pay_mthd_cd := LPAD(TRUNC(DBMS_RANDOM.VALUE(1, 5)), 2, '0');

    -- 청구유형 생성 ('1', '2', 'B', 'H' 값들의 조합)
    v_temp_bill_types := '';
    IF DBMS_RANDOM.VALUE > 0.5 THEN v_temp_bill_types := v_temp_bill_types || '1,'; END IF;
    IF DBMS_RANDOM.VALUE > 0.5 THEN v_temp_bill_types := v_temp_bill_types || '2,'; END IF;
    IF DBMS_RANDOM.VALUE > 0.5 THEN v_temp_bill_types := v_temp_bill_types || 'B,'; END IF;
    IF DBMS_RANDOM.VALUE > 0.5 THEN v_temp_bill_types := v_temp_bill_types || 'H,'; END IF;

    IF v_temp_bill_types IS NULL THEN
      -- 만약 아무것도 선택되지 않았다면, 4개 중 하나를 임의로 선택
      v_bill_types :=
        CASE TRUNC(DBMS_RANDOM.VALUE(1, 5))
          WHEN 1 THEN '1'
          WHEN 2 THEN '2'
          WHEN 3 THEN 'B'
          WHEN 4 THEN 'H'
        END;
    ELSE
      -- 맨 마지막의 콤마(,) 제거
      v_bill_types := RTRIM(v_temp_bill_types, ',');
    END IF;

    -- inv_oper_cycl_cd 생성 (90%는 '01', 10%는 '02')
    IF DBMS_RANDOM.VALUE < 0.9 THEN
        v_inv_oper_cycl_cd := '01';
    ELSE
        v_inv_oper_cycl_cd := '02';
    END IF;

    -- 서비스건수 생성 (요청된 분포에 따른 값)
    v_random_dist := DBMS_RANDOM.VALUE; -- 0과 1 사이의 난수 생성
    IF v_random_dist < 0.7 THEN
      -- 70% 확률: 1
      v_svc_cnt := 1;
    ELSIF v_random_dist < 0.9 THEN
      -- 20% 확률: 2 ~ 100
      v_svc_cnt := TRUNC(DBMS_RANDOM.VALUE(2, 101));
    ELSIF v_random_dist < 0.98 THEN
      -- 8% 확률: 101 ~ 1000
      v_svc_cnt := TRUNC(DBMS_RANDOM.VALUE(101, 1001));
    ELSIF v_random_dist < 0.99 THEN
      -- 1% 확률: 1001 ~ 5000
      v_svc_cnt := TRUNC(DBMS_RANDOM.VALUE(1001, 5001));
    ELSE
      -- 나머지 1% 확률: 5001 이상 (5001 ~ 20000 범위로 지정)
      v_svc_cnt := TRUNC(DBMS_RANDOM.VALUE(5001, 20001));
    END IF;

    -- 데이터 삽입
    INSERT INTO inv_obj_acnt (
      acnt_num,
      rep_svc_mgmt_num,
      bill_oper_num,
      bill_oper_cycl_cd,
      inv_oper_cycl_cd,
      pay_mthd_cd,
      bill_types,
      svc_cnt
    ) VALUES (
      1000000000 + i,     -- acnt_num은 10억번부터 순차적으로 증가
      v_rep_svc_mgmt_num, -- 생성된 대표서비스관리번호
      NULL,               -- bill_oper_num은 null
      NULL,               -- bill_oper_cycl_cd는 null
      v_inv_oper_cycl_cd, -- 생성된 inv_oper_cycl_cd
      v_pay_mthd_cd,      -- 생성된 납부방법코드
      v_bill_types,       -- 생성된 청구유형
      v_svc_cnt           -- 생성된 서비스건수
    );
  END LOOP;

  -- 변경사항 커밋
  COMMIT;
  
  -- 완료 메시지 출력
  DBMS_OUTPUT.PUT_LINE('총 ' || TO_CHAR(10000) || '건의 데이터가 inv_obj_acnt 테이블에 성공적으로 삽입되었습니다.');

EXCEPTION
  WHEN OTHERS THEN
    -- 오류 발생 시 롤백
    ROLLBACK;
    -- 오류 메시지 출력
    DBMS_OUTPUT.PUT_LINE('데이터 삽입 중 오류 발생: ' || SQLERRM);
    RAISE;
END;


