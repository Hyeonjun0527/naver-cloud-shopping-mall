# naver-cloud-shopping-mall

네이버 클라우드 데브옵스 부트캠프(2024.02 ~ 2024.04) 기간에 **혼자 진행한 쇼핑몰 애플리케이션
점진적 리팩토링** 아카이브입니다. 하나의 쇼핑몰을 **Servlet/JSP 레거시에서 시작해 단계별로
구조를 개선**하며 현대적 Spring 스택으로 전환했습니다.

> 각 단계는 당시 별도 저장소로 관리했고, 이 저장소에 **원본 커밋 히스토리를 그대로 보존**해
> (`git subtree`) 통합했습니다. 폴더별 커밋·날짜는 모두 당시(2024년) 실제 작업 기록입니다.

## 리팩토링 여정 (Servlet/JSP → Spring Boot)

| 폴더 | 시기 | 핵심 변화 |
|---|---|---|
| [`01-model2mvc-mybatis`](01-model2mvc-mybatis) | 2024.02 ~ 03 | **Model2 MVC** 구조 + **MyBatis · Spring** 적용 (비즈니스 로직 분리) |
| [`02-presentation-layer`](02-presentation-layer) | 2024.03 | **Presentation 계층** 정리 (Presentation + Business Logic 분리) |
| [`03-feature-expansion`](03-feature-expansion) | 2024.03 | 기능 확장 및 `dev` 브랜치 기반 형상 관리 (Myshop08) |
| [`04-spring-boot`](04-spring-boot) | 2024.04 | **Spring Boot 전환** |
| [`05-react-frontend`](05-react-frontend) | 2024.04 | **React 프론트엔드** 연동 |

## 대표 문제 해결

- **계층 분리 · 단계적 전환**: JSP에 결합돼 있던 로직을 **Model2 MVC + MyBatis**로 비즈니스
  계층으로 분리하고, Spring의 **트랜잭션 · DI**로 관심사를 정리한 뒤 **REST API · Ajax**를 거쳐
  최종적으로 **Spring Boot**로 재구성하며 레이어 경계를 명확히 했습니다.
  (`01-model2mvc-mybatis` ~ `04-spring-boot`)

## 기술 스택

`Java` · `Servlet/JSP` · `JSP EL/JSTL` · `MyBatis` · `Spring MVC` · `Spring Boot` · `React` · `MySQL`
