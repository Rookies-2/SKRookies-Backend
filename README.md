# 📬 개발 2팀 루키즈 개발트랙 - 사내 메신저 "peerFlow" 프로젝트
# 팀 A-git (아지트) 

---

## 👥 팀원
- **팀장**: [백두현] - 프로젝트 총괄, 백엔드
- **팀원**: [김현근] - 백엔드 팀장
- **팀원**: [신명철] - 백엔드
- **팀원**: [이석영] - 프론트엔드 팀장
- **팀원**: [김주윤] - 프론트엔드
- **팀원**: [한송이] - 프론트엔드

---

## 📌 프로젝트 개요
**SK쉴더스 루키즈 4기 개발 2팀**에서 진행한 **사내 메신저 웹 애플리케이션**입니다.  
과제 혹은 프로젝트에서 개인 번호 주고받으며 카톡방을 파는 불편함을 최소화하기 위해 생산성 및 원활한 협업을 위해, 실시간 채팅과 보안 기능을 갖춘 내부 전용 메신저를 구현했습니다.

---

## 🎯 목표
- SK루키즈 쉴더스 게더타운 교육을 듣는 수강생들이 안전하게 협업을 위해 사용할 수 있는 메신저 서비스 구축
- 실시간 채팅 및 파일 전송 기능 도 네트워크 트래픽 이상감지, 권한 관리) 적용

---

## 🛠 기술 스택
### Backend 
- Java 17
- Spring Boot
- Spring Security 6 + JWT
- WebSocket + STOMP 
- JPA / MariaDB

### Frontend
- React + Typescript
- Bootstrap css
- Axios
### AI / ML
- Python (데이터 처리 및 모델링)
- Pandas, NumPy (데이터 전처리)
- scikit-learn RandomForestClassifier (머신러닝 모델)
- Joblib / Pickle (모델 저장 및 배포)
- REST API / Spring Boot 연동 (AI 모델 서버화)
### Infra & Tools
- Git / GitHub
- Notion

---

## 자료구조
src/main/java/com/agit/peerflow
├── ai/               # ML 관련 모듈 (비밀번호 변경 공격 탐지 및 차단 모듈)
├── config/           # 전역 설정 (WebConfig, StompAuthChannelInterceptor 등)
├── controller/       # REST API 엔드포인트, 요청/응답 처리
├── domain/           # 엔티티, ENUMS 객체 (JPA Entity, Enum)
├── dto/              # 데이터 전송 객체 (Request/Response DTO)
├── exception/        # 예외 처리 클래스 (Custom Exception, Handler)
├── repository/       # 데이터 접근 계층 (JPA Repository)
├── scheduler/        # 스케줄링 작업 (비활성 사용자 계정 정리 스케줄러)
├── security/         # 인증/인가 관련 (JWT, Security Config)
└── service/          # 비즈니스 로직 계층

---
## 📂 주요 기능 (MVP)
1. **회원 관리**
   - 회원가입 / 로그인 / 로그아웃
   - JWT 기반 인증
   - AI 기반 이상 로그인 감지: 로그인 네트워크 트래픽 분석으로 비정상 접근 시 알림
2. **채팅 기능**
   - 1:1 채팅방 생성 / 나가기
   - 그룹 채팅방 생성 / 참여 / 나가기
   - 읽음 여부 표시
   - 파일/이미지 업로드, 다운로드
3. **과제/공지사항 게시판**
   - 과제 제출 / 평가 / 수강생 별 과제 제출 상태 표시
   - 공지사항 등록 / 수정 / 삭제 기능
3. **보안 기능**
   - 권한별 접근 제어
   - AI 기반 비밀번호 변경 공격 탐지 및 차단: Backdoors, Exploits, Generic, Reconnaissance 시도, 비정상 트래픽 감지
4. **알림**
   - 새 메시지 도착 시 실시간 알림
5. **기본 UI**
   - 채팅방 목록, 메시지 창, 사용자 목록
   - 다크모드, 라이트모드

---
