# 📄 3-Tier Architecture 기반 금융 도메인 고가용성 시스템

---

## 1. 프로젝트 소개

본 프로젝트는 **금융 도메인**에서 필수적인 **고가용성(High Availability)** 을 확보하기 위해

**3-Tier Architecture**를 설계·구축하고, 장애 상황에서도 서비스가 중단되지 않도록 하는 것을 목표로 합니다.

---

## 2. 팀 소개 및 역할

| 이름 | 역할 |  |
| --- | --- | --- |
| **김문석** | **Project Leader** / 인프라 설계·구축 총괄, 장애 전환 구조 설계 | github url |
| **이정이** | BE & FE 개발, 서버 환경 구성 | github url |
| **김현수** | 무중단 장애 전환 구조 설계 | github url |
| **박여명** | 네트워크 구성 및 테스트 | github url |

---

## 3. Why 3-Tier Architecture?

### 📌 장점

1. **확장성**
    - 계층별 독립 확장 가능 → 수평·수직 스케일 아웃 용이
2. **보안**
    - DB 접근을 WAS를 통해서만 허용 (최소 권한, 네트워크 분리)
3. **유지보수**
    - 로직·UI 서버를 집중 배포 → 클라이언트 무설치, 버전 관리 간편
4. **역할분리 & 재사용성**
    - 프레젠테이션·비즈니스·데이터 계층 분리 → 코드 품질 향상, 모듈 재사용 용이

---

## 4. 3-Tier Architecture 구조

**구성 계층**

- **Presentation Tier** : 사용자의 요청을 처리하는 진입 지점
- **Application Tier** : 비즈니스 로직 실행
- **Database Tier** : 데이터 저장·조회 및 무결성 관리

---

### 4.1 Presentation Tier

- **구성요소**
    - **VIP** (192.168.0.100): 클라이언트 진입 가상 IP
    - **Keepalived**: VIP 관리, 장애 시 자동 전환
    - **HAProxy**: 로드밸런서
    - **Nginx**: 정적 리소스 처리, 동적 요청은 WAS로 전달
- **역할**
    - SSL 종료, 트래픽 부하 분산, 내부 계층 보호
- **특징**
    - 캐싱·압축으로 트래픽 절감
    - 로드밸런싱으로 성능 향상

---

### 4.2 Application Tier

- **구성요소**
    - **Tomcat WAS**
    - **Servlet Container**
    - **Business Logic Module**
- **특징**
    - API 처리, 세션 관리, 트랜잭션 관리
    - 데이터와 분리된 로직 변경·배포 가능
    - DB 물리 IP 대신 VIP(192.168.0.200) 사용 → 장애 시 무중단 전환 가능

---

### 4.3 Database Tier

- **구성요소**
    - MySQL Master-Slave (GTID 기반 복제)
    - Keepalived (VIP 자동 전환)
    - Orchestrator (자동 승격 및 복제 재구성)
- **특징**
    - PK, FK, Lock을 통한 데이터 무결성
    - ACID 트랜잭션 보장
    - Slave를 통한 읽기 부하 분산 가능

---

## 5. 금융 도메인에서 HA의 중요성

- **중단 = 금전적 손실**
- **밀리초 단위의 실시간성** 요구
- 장애 사례:
    - 2020 키움증권, 2021 미래에셋증권, 2024 업비트
    - 원인: 서버 증설 부족, BCP 부재, 시스템 과부하 대비 부족
    - 결과: 수십억 원 배상, 고객 신뢰 하락

---

## 6. HA 구현 요소

| 구성 요소 | 설명 |
| --- | --- |
| **Keepalived** | VIP 기반 고가용성 네트워크 장애 대응 |
| **Orchestrator** | DB 장애 시 자동 승격, Topology 시각화 |
| **DB Replication** | GTID 기반 실시간 복제 |
| **VRRP** | Master 장애 시 VIP를 Backup 노드로 이동 |

---

## 7. 시스템 아키텍처

![image.png](attachment:ae22899f-a05d-4e4d-aaf4-6e4a261d8551:image.png)

### 7.1 Presentation Tier

```
Client → VIP(192.168.0.100) → HAProxy → Nginx

```

- VIP는 Keepalived가 관리
- LB 장애 시 VIP가 Backup LB로 이동

### 7.2 Application Tier

```
Nginx → WAS(Tomcat) → VIP(192.168.0.200) → DB

```

- VIP를 사용해 DB 접근 → Master 장애 시 IP 변경 불필요

### 7.3 Database Tier

```
Master DB ↔ Slave DB (GTID Replication)

```

- Keepalived로 VIP 전환
- Orchestrator로 자동 승격 및 복제 재구성

---

## 8. PoC (Proof of Concept) 테스트

### PoC 1: Load Balancer 장애

**방법**: Master LB 컨테이너 중지

![정상 상황을 나타낸 다이어그램](attachment:1d50226e-a744-4024-851b-f8ea42dff45d:image.png)

정상 상황을 나타낸 다이어그램

![장애 발생과 그에 따른 변화를 나타낸 다이어그램](attachment:e344ef64-cfb7-4dec-9522-aa0b3f7dfb81:image.png)

장애 발생과 그에 따른 변화를 나타낸 다이어그램

- **결과**: VIP가 Backup LB로 이동, 서비스 무중단 유지

![image.png](attachment:94eff561-e809-40ff-985e-73c68e7eed59:image.png)

- 

![image.png](attachment:187596c6-bf10-451d-b825-2922124b2b5f:image.png)

- 

![image.png](attachment:4c6a4cab-fe00-4537-a464-4a7b45a0cfad:image.png)

- 

![image.png](attachment:dc912713-e90e-40bd-a3e4-0157a35429df:image.png)

- 

---

### PoC 2: Master DB 장애

![정상 상황을 나타낸 다이어그램 ](attachment:9db8816f-d672-4938-b458-0a85746ec218:image.png)

정상 상황을 나타낸 다이어그램 

![장애 발생과 그에 따른 변화를 나타낸 다이어그램](attachment:cf3d8e1d-2a48-41a8-80bd-01bb56099878:image.png)

장애 발생과 그에 따른 변화를 나타낸 다이어그램

**방법**: Master MySQL 서비스 중지

- **결과**:
    1. VIP가 Slave로 이전
    2. Slave가 Master로 승격
    3. WAS가 IP 변경 없이 새로운 Master에 접근
- **문제점**:
    - Master 재가동 시 VIP 자동 복귀 → Split-Brain 위험
    - 복제 자동 복구 미동작
- **해결방안**:
    - Orchestrator로 복제 구조 재구성 및 완전 자동화

### DB 정상 상황

@ MASTER DB

![image.png](attachment:28f85077-7ebf-4efc-8209-ce4f21aa327a:image.png)

- Master Running

![image.png](attachment:b646dc5e-0a9a-41db-98d8-60b6309a0c51:image.png)

- VIP도 정상 할당

@ SLAVE DB

![image.png](attachment:447287b9-6836-41ed-8383-82dc0a6dcf89:image.png)

- Slave Running

![image.png](attachment:5aa7a593-0cf0-4a3b-a415-7fe7822e902f:image.png)

- VIP 할당 안 되어있음

### ### 장애 상황 ###

@ MASTER DB

![image.png](attachment:8e32723d-efdb-46c1-a965-13ea46e42254:image.png)

- Master 종료

![image.png](attachment:d6e71b4b-7540-49aa-ac1e-31994f7280ab:image.png)

- 더이상 Master에서 VIP 소유 X

@ SLAVE DB

![image.png](attachment:6f303733-7ec7-41a5-88ef-d4d44e7695b0:image.png)

- 복제 역할을 멈추고 Master 역할 수행 중인 Slave

![image.png](attachment:f6bbb5da-f87b-458f-b234-957953e4e937:image.png)

- VIP를 인계받은 Slave

@ MASTER 다시 살린 상황

![image.png](attachment:093f9ec2-cc7b-4881-9114-48bca9a00afb:image.png)

- Mater db가 다시 살아나 VIP를 인계받은 상황

![image.png](attachment:e0884bcf-e726-4db2-9afe-beb31ec4811f:image.png)

- VIP를 잃은 Slave

---

## 9. 트러블슈팅

| 문제 | 원인 | 해결 |
| --- | --- | --- |
| LB Split-Brain | auth_pass 불일치, 방화벽 차단 | auth_pass 통일, UFW 해제 |
| 정적 파일 소실 | 컨테이너 재시작 시 파일 손실 | 볼륨 마운트 적용 |
| Slave DB 읽기 실패 | repl 계정 권한 불일치 | 계정 재생성 및 REPLICATION 권한 부여 |
| Master 재가동 시 Split-Brain | VIP 복귀 로직 문제 | Orchestrator + 스크립트 활용 예정 |

---

## 10. 회고

- **김문석**:예상치 못한 변수로 완전 자동 전환에 한계가 있었음 → 장애 상황을 고려한 설계 필요성 인식
    
    HA는 단순히 서버를 여러 대 두는 것이 아니라, 장애 감지·자동 전환·데이터 일관성이 모두 갖춰져야 함을 배움
    
    MySQL의 마스터-슬레이브 복제와 Orchestrator를 통해 이중화 및 장애 전환 구조를 구성했지만, 예상치 못한 변수들로 인해 완전한 자동 전환에는 한계가 있었습니다.
    
    이 경험을 통해 고가용성 아키텍처는 단순한 기술 적용이 아닌, 다양한 장애 상황을 고려한 정교한 설계가 필요하다는 점을 깨달았고, 향후 Orchestrator의 구조와 HA 시스템 전반에 대해 더 깊이 있게 학습할 계획입니다
    
- **이정이**:
    
    3티어 아키텍처를 직접 구현하며 계층 간 역할 분리와 구조적 장점을 체감할 수 있었습니다. 이를 통해 2티어와 비교되는 3티어의 구성과 이점에 대해 개념적으로 이해할 수 있었습니다.
    
    고가용성은 단순히 서버를 여러 대 두는 것으로 보장되지 않으며, 장애 감지와 자동 전환, 상태 동기화 같은 요소들이 함께 갖춰져야 한다는 점을 실습을 통해 배웠습니다.
    
- **박여명**:
    
    3Tier Architecture를 기반으로이중화에 대해서 학습하고 구성하며, .0실무 환경에서는 고가용성 (2중화,3중화)에 대한 중요성을 느끼게 되었습니다.
    
    고가용성에 대한 이론 뿐 아니라 실 구현을 통해 고가용성 보장을 위한 실무적인 방법을 고민했다는 점에서 의미있었습니다.
    
- **김현수** :
    
    고가용성에 대한 이론 뿐 아니라 실 구현을 통해 고가용성 보장을 위한 실무적인 방법을 고민했다는 점에서 의미있었습니다.
    
    2티어 → 3티어 → MSA 로 이어지는 아키텍처의 흐름을 이해할 수 있었습니다.
    

---

## 11. 향후 계획

- Orchestrator와 Keepalived의 완전 연동
- Split-Brain 방지 스크립트 배포
- 복제 구조 자동 복구 기능 고도화
- MSA 환경으로 확장 테스트
