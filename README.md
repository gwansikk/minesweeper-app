# minesweeper-app

![minesweeper-app](https://github.com/gwansikk/minesweeper-app/assets/39869096/35262004-6381-4701-9542-e07bd705fe87)

## Description

본 프로젝트는 경기대학교 `모바일프로그래밍 (DD731)`의 기말과제로 클래식한 지뢰찾기 게임의 기능을 구현한 안드로이드 애플리케이션입니다.

- **확장성 고려 설계**: 이 프로젝트는 기능 추가나 업데이트를 용이하게 하기 위해 적절한 함수화로 설계되었습니다.

- **반응형 설계**: 사용자 인터페이스는 다양한 화면 크기에 맞춰 자동으로 조절됩니다.

- **동적 렌더링**: 프로젝트는 TableLayout을 활용하여 지뢰찾기 게임판과 같은 격자 모양의 사용자 인터페이스 구성 요소를 런타임에서 동적으로 렌더링합니다.

## Roadmap

- [x] **기본 게임판 구현**

  - [x] TableLayout을 이용하여 격자 모양의 게임판 생성 (9x9)
  - [x] Button을 TableRow에 추가하여 코드로 런타임 환경에서 렌더링

- [x] **사용자 인터페이스**

  - [x] 타이머, 남은 지뢰 수 표시
  - [x] 재시작 및 결과 보기 기능
  - [x] 각 칸, 지뢰, 깃발 등의 디자인

- [x] **게임 상호작용**

  - [x] 사용자의 클릭에 반응하는 기능 (BREAK, FLAG)
  - [x] 지뢰가 아닌 칸에 주변 지뢰 수 표시
  - [x] 사용자가 칸을 클릭했을 때 지뢰가 아니면 주변의 안전한 칸들을 연쇄적으로 열기
  - [x] 지뢰를 클릭하면 게임 오버
  - [x] 모든 안전한 칸을 열었을 때 게임 승리
