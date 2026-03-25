package project01.eskim;

/**
 * 이 클래스는 외부 클래스로 호출을 하는 용도입니다.
 *
 * 요구사항 1. 주차장 클래스 만들기 (Y/N)
 * 요구사항 2. 파일에서 주차장 초기화 (Y/N)
 * 요구사항 3. 차량 클래스 (Y/N)
 * 요구사항 4. 주차 상태 출력 (Y/N)
 * 요구사항 5. 차량 주차 기능 (Y/N)
 * 요구사항 6. 차량 출차 기능 (Y/N)
 * 요구사항 7. 차량 검색 기능 (Y/N)
 * 요구사항 8. 주차장 정보 출력 (Y/N)
 * 요구사항 9. 메뉴 운영 (Y/N)
 * 요구사항 10. 추가기능 (Y/N)
 *
 * @author project01.eskim
 * @since 2025-10-04
 * @version 1.0.1
 */

public class Main
{
    /**
     * 메인 함수입니다. 제 이름, 학번을 보여주고 주차장 관리 시스템을 구동합니다.
     * @param args 메인 함수의 인자
     */
    public static void main(String[] args) //04-10-25
    {
        System.out.println("202510954 김은성\n");
        Parking konkuk = new Parking();
        konkuk.menu();
    }
}