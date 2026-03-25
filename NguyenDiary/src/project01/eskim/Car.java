package project01.eskim;

/**
 * 이 클래스는 자동차 클래스입니다.
 *
 *  @author project01.eskim
 *  @since 2025-10-04
 *  @version 1.1
 *
 */
public class Car //04-10-25
{
    //기본적인 차 객체 생성을 위한 변수들
    public String num, type, contact;
    public int row,col;

    /**
     * 이 메소드는 자동차의 주차 위치와 정보들을 입력받아서 객체를 생성합니다.
     * @param num 차의 번호
     * @param type 차의 종류
     * @param contact 차주의 전화번호
     * @param row 몇번째 행인지를 의미. y좌표와 같음
     * @param col 몇번째 열에 있는지 나타냄. x좌표와 동일
     */
    public Car(String num, String type, String contact, int row, int col) //10-10-25
    {
        this.num = num;
        this.type = type;
        this.contact = contact;
        this.row = row;
        this.col = col;
    }
}
