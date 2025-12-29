## ESS (근태/휴가 관리) 백엔드

### API Interactive Playground

Swagger UI: http://107.21.56.220:8080/swagger-ui/index.html

        모든 API 명세 확인 및 실제 요청 테스트가 가능합니다.
        ### 테스트 계정
        - CEO: `ceo1` / `pass1234!`
        - EMPLOYEE: `emp1` / `pass1234!`

### 기술 스택

**Backend** 

![Java](https://img.shields.io/badge/Java_21-000000?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5.6-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![JPA/Hibernate](https://img.shields.io/badge/Spring_Data_JPA-59666C?style=flat-square&logo=hibernate&logoColor=white)
![JWT](https://img.shields.io/badge/JWT_0.11.5-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger_UI-85EA2D?style=flat-square&logo=swagger&logoColor=black)

**Database** 

![MySQL](https://img.shields.io/badge/MySQL_8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![H2](https://img.shields.io/badge/H2_(Test)-1E88E5?style=flat-square&logo=h2&logoColor=white)

**Build & Test** 

![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=flat-square&logo=junit5&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-EC1C24?style=flat-square)

**Infra & DevOps** 

![Docker](https://img.shields.io/badge/Docker_/_Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)
![AWS](https://img.shields.io/badge/AWS_EC2_/_RDS-FF9900?style=flat-square&logo=amazon-aws&logoColor=white)
### 기능 설명
- 회원 가입/로그인(CEO/사원, JWT 기반 인증)
- 사원 가입 승인(CEO/관리자)
- 근태 관리(출근/퇴근, 내 근태 조회)
- 휴가 관리(신청/내역 조회, 승인/거절)
- 연차 관리(부여/잔여 조회/차감/복원, 법정 연차 프리뷰)
- 대시보드(프로필/근태/연차/휴가 통합 조회)
- 내 정보 관리(정보 수정/비밀번호 변경/프로필 이미지 업로드)
- 회사/근무 규칙 관리(등록/조회/수정)



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
