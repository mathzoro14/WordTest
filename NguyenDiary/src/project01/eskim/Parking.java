package project01.eskim;

import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;
import java.io.File;

/**
 * 이 클래스는 주차 시스템과 주차장을 관리합니다.
 * @author project01.eskim
 * @since 2025-10-04
 * @version 1.3.4
 */

public class Parking //04-10-25
{

    //주차장 객체 생성
    public int Row, Col,x=0;
    public Car[] car = null;
    public int[][] ParkingSlot;

    //스캐너들과 파일 생성 파트
    Scanner scanner = new Scanner(System.in);
    Scanner scan;
    {
        //파일 열기 시도
        try
        {
            File file = new File("res/konkuk.txt");
            scan = new Scanner(file);
            if(scan.hasNext()) //exception
                this.Row = scan.nextInt();
            if(scan.hasNext())
                this.Col = scan.nextInt();
            this.car = new Car[this.Row*this.Col];
            this.ParkingSlot = new int[this.Row][this.Col];
            for(int i=0;i<this.Col;i++)
                for(int j=0;j<this.Row;j++)
                    this.ParkingSlot[j][i] = 0;

            while(scan.hasNext())
            {
                int CarRow = scan.nextInt();
                int CarCol = scan.nextInt();
                String CarNum = scan.next();
                String CarType = scan.next();
                String CarContact = scan.next();
                if(car.length > this.x)
                {
                    if((CarRow>=0 && CarRow <this.Row) && (CarCol>=0 && CarCol<this.Col))
                    {
                        this.car[this.x++] = new Car(CarNum, CarType, CarContact, CarRow, CarCol);
                        this.ParkingSlot[CarRow][CarCol] = 1;
                    }
                    else System.out.println("해당 개체 생성 불가");
                }
                else break;
            }
        }
        //파일 경로 미존재 시의 예외처리
        catch (FileNotFoundException e)
        {
            System.out.println("파일을 찾을 수 없음.");
        }

        //파일 형식의 이상이 있을 시의 예외처리
        catch(InputMismatchException e)
        {
            System.out.println("파일 내 형식의 오류 존재");
        }
    }

    /**
     * 이 메소드는 받는 인자가 없으면 0,0으로 넘겨줍니다.
     */
    public Parking() //10-10-25
    {
        this(0,0);
    }

    /**
     * 이 메소드는 인자 2개를 그대로 가져와 변수에 넣는 용도.
     * 사실상 위의 인자 없는 메소드 처리용
     * @param row 몇번째 행. y좌표
     * @param col 몇번째 열. x좌표
     */
    public Parking(int row, int col) //10-10-25
    {
        this.Row = row;
        this.Col = col;
    }

    /**
     * 이 메소드는 주차 기능을 수행시켜준다.
     * 주차 위치를 입력받아 주차가 가능한지 판별하고
     * 자동차의 정보를 받아 번호 중복여부 확인 후 처리한다.
     */
    public void ParkProcess() //10-10-25
    {
        System.out.print("[시스템] 주차 위치 추천: ");
        this.PosRecommend();
        System.out.print("\n주차시킬 위치(x,y)의 x,y를 차례로 입력하세요: ");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        Car testCar = this.carPosRec(x,y);
        if((x>=0 && x<this.ParkingSlot[0].length) && (y>=0 && y<this.ParkingSlot.length)
                && testCar == null)
        {
            System.out.print("자동차의 번호를 입력해주세요: ");
            String carNum1 = scanner.next();
            Car carRec = this.carSearch(carNum1);
            if(carRec==null)
            {
                System.out.print("자동차의 종류를 입력해주세요: ");
                String carType1 = scanner.next();
                System.out.print("전화번호를 입력해주세요: ");
                String carContact1 = scanner.next();
                this.car[this.x++] = new Car(carNum1, carType1, carContact1,y,x);
                this.ParkingSlot[y][x] = 1;
                System.out.println("해당 차량의 주차가 완료되었습니다.");
                System.out.println("\n-고객님의 차량 정보-");
                System.out.println("차량 번호: "+ carNum1+", 차량 종류: "+carType1+ ", 차주 전화번호: "+
                        carContact1+", 주차 위치: (" +x+","+y+")");
            }
            else System.out.println("해당 차량은 이미 주차장에 존재하는 차량입니다.");
            this.menu();
        }
        else if(testCar != null)
        {
            System.out.println("이미 해당 자리에 차량이 주차되어 있습니다. 다시 입력해주세요.\n");
            this.ParkProcess();
        }
        else
        {
            System.out.println("주차 좌표를 올바르게 입력해주세요.\n");
            this.ParkProcess();
        }
    }

    /**
     * 이 메소드는 출차를 도와준다.
     * 차량번호를 받아 있으면 출차를 시키고 없으면 메시지로 표시한다.
     */
    public void ExitProcess() //10-10-25
    {
        System.out.print("출차시키고 싶은 차량번호를 입력해주세요: ");
        String carnum = scanner.next();
        Car setcar = carSearch(carnum);
        if(setcar!=null)
        {
            this.ParkingSlot[setcar.row][setcar.col]=0;
            for(int i=0;i<this.x;i++)
            {
                if(Objects.equals(this.car[i].num, carnum))
                    this.car[i]=null;
            }
            System.out.println(carnum+"번 차량의 출차가 완료되었습니다.");
        }
        else System.out.println("주차장에 존재하지 않는 차량입니다.");
        this.menu();
    }

    /**
     * 이 메소드는 주차장 내 차량검색을 하게 해준다.
     * 중복되는 차량번호나 전화번호가 있으면 그 차량의 정보와 위치를 표시해준다.
     */
    public void ParkSearch() //10-10-25
    {
        System.out.print("차량검색을 위해 전화번호나 차량번호를 입력해주세요: ");
        String carnum = scanner.next();
        Car setcar = this.carSearch(carnum);
        if(setcar!=null)
        {
            System.out.println("\n검색한 차량의 정보");
            System.out.println("차량 번호: "+ setcar.num+", 차량 종류: "+setcar.type+", 차주 전화번호: "+setcar.contact+", 주차 위치: ("
                    +setcar.col+","+setcar.row+")");
            System.out.println("\n-차량 위치-");
            for(int i=0;i<this.ParkingSlot.length;i++)
            {
                for(int j=0;j<this.ParkingSlot[i].length;j++)
                {
                    if (i==setcar.row && j==setcar.col) System.out.print("★ ");
                    else System.out.print("□ ");
                }
                System.out.println();
            }
        }
        else System.out.println("해당 정보와 일치하는 차량을 찾을 수 없습니다.");
        this.menu();
    }

    /**
     * 생성한 객체들 가운데서 같은 차량번호를 가진 차량이 있는지 판별시켜준다.
     * @param carnum 비교를 위한 차량 번호
     * @return carnum과 동일한 번호의 차량 존재 시 그 차량 반환, 없으면 null 반환
     */
    public Car carSearch(String carnum) //10-10-25
    {
        for(Car cars: this.car)
        {
            if(cars!=null)
            {
                //차량 번호 비교용
                if(Objects.equals(cars.num, carnum) || Objects.equals(cars.contact, carnum))
                    return cars;
            }
        }
        return null;
    }

    /**
     * 이 메소드는 주차 기능에서 미리 주차된 차량이 있는지 판별해준다.
     * @param x 주차를 하고 싶은 위치의 x좌표
     * @param y 주차를 하고 싶은 위치의 y좌표
     * @return 중복 차량이 있으면 그 차를 반환, 없으면 null 반환
     */
    public Car carPosRec(int x,int y) //11-10-25
    {
        for(Car cars: this.car)
        {
            if(cars!=null)
            {
                if(cars.col == x && cars.row == y)
                    return cars;
            }
        }
        return null;
    }

    /**
     * 이 메소드는 위치 추천을 위해서 사용되고,
     * 출구인 (0,0)에 가까운 자리를 추천해준다.
     */
    public void PosRecommend() //11-10-25
    {
        int rec=0,k=0;
        while(true)
        {
            for(int i=0;i<=k;i++)
            {
                if(this.ParkingSlot[k-i][i]==0 && (k-i) < this.ParkingSlot.length &&
                i < this.ParkingSlot[0].length)
                {
                    if(rec==1) System.out.print(",");
                    System.out.print("("+i+","+(k-i)+")");
                    rec=1;
                }
            }
            k++;
            if(rec==1) break;
        }
    }

    /**
     * 주차장 내 차들의 정보들을 다 보여주는 메소드이다.
     */
    public void ShowParking() //10-10-25
    {
        System.out.println("현재 주차장에 주차된 차들의 정보");
        for(Car cars: this.car)
        {
            if(cars!=null)
                System.out.println("차량 번호: "+ cars.num+", 차량 종류: "+cars.type+", 차주 전화번호: "+cars.contact+", 주차 위치: ("
                +cars.col+","+cars.row+")");
        }
        this.menu();
    }

    /**
     * 메뉴를 표시해준다.
     */
    public void menu() //10-10-25
    {
        System.out.println("--------------------------------------------------------------");
        for(int i=0;i<this.ParkingSlot.length;i++)
        {
            for(int j=0;j<this.ParkingSlot[i].length;j++)
            {
                if (this.ParkingSlot[i][j] == 1) System.out.print("■ ");
                else System.out.print("□ ");
            }
            System.out.println();
        }
        System.out.println("\n1. 차량 주차 2. 차량 출차 3. 차량 검색 4. 주차장 정보 출력 5. 종료");
        System.out.print("원하시는 기능을 번호를 입력해 선택해 주세요 : ");
        int order = scanner.nextInt();
        System.out.println();
        switch(order)
        {
            case 1 -> this.ParkProcess();
            case 2 -> this.ExitProcess();
            case 3 -> this.ParkSearch();
            case 4 -> this.ShowParking();
            case 5 -> System.out.println("이용해주셔서 감사합니다. 다음에 또 이용하세요!");
            default ->
            {
                System.out.println("번호를 다시 입력해주세요.");
                this.menu();
            }
        }
    }
}

