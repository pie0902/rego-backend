## ESS (근태/휴가 관리) 백엔드

포트폴리오용으로 **Swagger UI에서 바로 테스트 가능한 더미데이터(회사/유저/연차/휴가/근태)** 를 자동 생성합니다.

### 로컬 데모 실행(추천)

1) MySQL 실행

```bash
docker compose -f compose.yaml up -d
```

2) 백엔드 실행 (기본 프로필: `demo`)

```bash
./gradlew bootRun
```

3) Swagger UI

- `http://localhost:8080/swagger-ui/index.html`

### 테스트 계정(더미)

더미 계정/비밀번호는 `dummy.md`에 정의되어 있고, 앱 시작 시 자동으로 생성됩니다.

- CEO: `ceo1` / `password123!`
- EMPLOYEE: `employee1` / `password123!`

### 5분 체험 시나리오(Swagger 기준)

1) 로그인 토큰 발급: `POST /api/auth/login`
2) 사원 근태 확인(과거 10영업일 더미): `GET /api/attendance/me`
3) 오늘 출근: `POST /api/attendance/check-in`
4) 오늘 퇴근: `POST /api/attendance/check-out`
5) 휴가 신청/조회: `POST /api/leaveRequest/create`, `GET /api/leaveRequest/my`
6) 대표(CEO)로 대기 휴가 승인: `GET /api/leaveRequest/pending`, `PATCH /api/leaveRequest/update-status`

### 배포(prod)에서 더미데이터

- `prod` 프로필에서는 더미데이터가 비활성화됩니다.
- 설정 파일: `src/main/resources/application-prod.properties`
- 배포 컴포즈: `docker-compose.prod.yml`
