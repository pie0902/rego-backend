## ESS (근태/휴가 관리) 백엔드

### 기술 스택
- Java 21 (JDK 21)
- Spring Boot 3.5.6 (Spring Web/MVC, Spring Security, Validation)
- Spring Data JPA (Hibernate)
- MySQL 8.0 (Docker Compose)
- JWT (JJWT 0.11.5)
- Swagger UI (SpringDoc OpenAPI 2.8.5)
- Gradle
- Lombok / MapStruct
- 테스트: JUnit 5, H2
- 배포/자동화: Docker, Docker Compose, Docker Hub, GitHub Actions, Aws EC2, Aws RDS

### 기능 설명
- 회원 가입/로그인(CEO/사원, JWT 기반 인증)
- 사원 가입 승인(CEO/관리자)
- 근태 관리(출근/퇴근, 내 근태 조회)
- 휴가 관리(신청/내역 조회, 승인/거절)
- 연차 관리(부여/잔여 조회/차감/복원, 법정 연차 프리뷰)
- 대시보드(프로필/근태/연차/휴가 통합 조회)
- 내 정보 관리(정보 수정/비밀번호 변경/프로필 이미지 업로드)
- 회사/근무 규칙 관리(등록/조회/수정)

### API Interactive Playground

  Swagger UI: http://107.21.56.220:8080/swagger-ui/index.html

        모든 API 명세 확인 및 실제 요청 테스트가 가능합니다.
        ### 테스트 계정
        - CEO: `ceo1` / `pass1234!`
        - EMPLOYEE: `emp1` / `pass1234!`


### Architecture Diagram
<p align="center">
  <a href="https://github.com/user-attachments/assets/d538efe3-1325-4202-9c90-f7ec5fda82f7">
    <img width="900" alt="Architecture Diagram" src="https://github.com/user-attachments/assets/d538efe3-1325-4202-9c90-f7ec5fda82f7" />
  </a>
</p>

### ERD Diagram
<p align="center">
  <a href="https://github.com/user-attachments/assets/f170c703-b344-4e38-818c-fd98a318c583">
    <img width="900" alt="ERD Diagram" src="https://github.com/user-attachments/assets/f170c703-b344-4e38-818c-fd98a318c583" />
  </a>
</p>

### 배포된 서버
Swagger UI
- `http://107.21.56.220:8080/swagger-ui/index.html`


### 5분 체험 시나리오(Swagger 기준)

1) 로그인 토큰 발급: `POST /api/auth/login`
2) 사원 근태 확인 `GET /api/attendance/me`
3) 오늘 출근: `POST /api/attendance/check-in`
4) 오늘 퇴근: `POST /api/attendance/check-out`
5) 휴가 신청/조회: `POST /api/leaveRequest/create`, `GET /api/leaveRequest/my`
6) 대표(CEO)로 대기 휴가 승인: `GET /api/leaveRequest/pending`, `PATCH /api/leaveRequest/update-status`
