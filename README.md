# Calculator_Network_Project
Network Calculator using Java Socket (HW1)

# Calculator Network Project (HW1)

네트워크 프로그래밍 과제 - Java 소켓을 이용한 계산기

## 프로젝트 소개

TCP/IP 소켓 통신을 사용하여 클라이언트-서버 구조의 계산기를 구현했습니다.

### 주요 기능
- 4가지 산술 연산 (ADD, SUB, MUL, DIV)
- ThreadPool 기반 멀티클라이언트 처리 (최대 10개)
- HTTP 스타일 프로토콜
- 체계적인 예외 처리

## 아키텍처

\`\`\`
Client ←─TCP/IP─→ Server (ThreadPool)
                    ├─ ClientHandler 1
                    ├─ ClientHandler 2
                    └─ ClientHandler 3
\`\`\`

## 실행 방법

### 서버 실행
\`\`\`bash
cd src
javac calculator/CalculatorServer.java
java calculator.CalculatorServer
\`\`\`

### 클라이언트 실행
\`\`\`bash
cd src
javac calculator/CalculatorClient.java
java calculator.CalculatorClient
\`\`\`

## 상세 문서

자세한 내용은 [Wiki](https://github.com/YourUsername/Calculator-Network-Project/wiki)를 참고하세요.

## 기술 스택

- **Language**: Java
- **Network**: TCP/IP Socket
- **Concurrency**: ExecutorService, ThreadPool
- **Protocol**: Custom (HTTP-style)

## Author

Hwang Minwoo (황민우)

## 📅 프로젝트 정보

- **과목**: 컴퓨터 네트워크 및 실습
- **과제**: HW#1 Calculator in the Cloud
- **제출일**: 2025.11.10
