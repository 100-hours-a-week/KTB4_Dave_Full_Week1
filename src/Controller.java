import java.util.InputMismatchException;
import java.util.Scanner;

public class Controller {
    private final JVM jvm;
    private final Scanner sc;

    public Controller(JVM jvm, Scanner sc) {
        this.jvm = jvm;
        this.sc = sc;
    }

    public int numberInput(int start, int end){
        int num = end;

        while(num < start || num >= end){
            try {
                num = sc.nextInt();
                if(num < start || num >= end){
                    System.out.println(start+ "에서 " + (end-1) + " 사이의 정수를 입력해주세요.");
                }
            }
            catch(InputMismatchException e){
                System.out.println(start+ "에서 " + (end-1) + " 사이의 정수를 입력해주세요.");
                sc.next();
            }
        }

        return num;
    }

    public int numberInput(int start){
        int num = 0;
        while(num < start){
            try {
                num = sc.nextInt();
                if(num < start){
                    System.out.println((start-1)+ "보다 큰 정수를 입력해주세요.");
                }
            }
            catch(InputMismatchException e){
                System.out.println((start-1)+ "보다 큰 정수를 입력해주세요.");
                sc.next();
            }
        }

        return num;
    }

    public void run(){
        int n = 0;

        while(n != 6) {
            System.out.println("어떤 작업을 할지 선택해주세요.");
            System.out.println("1. 새 데이터 저장하기");
            System.out.println("2. 시간 지나가게 하기");
            System.out.println("3. 강제로 GC 실행하기");
            System.out.println("4. 데이터 초기화하기");
            System.out.println("5. 데이터 출력하기");
            System.out.println("6. 프로그램 종료하기");

            n = numberInput(1, 7);
            switch (n){
                case 1:
                    insertData();
                    n = 0;
                    break;
                case 2:
                    timePass();
                    n = 0;
                    break;
                case 3:
                    gcForce();
                    n = 0;
                    break;
                case 4:
                    dataInit();
                    n = 0;
                    break;
                case 5:
                    jvm.nowData();
                    n = 0;
                    break;
                case 6:
                    jvm.shutDown();
                    System.out.println("프로그램을 종료합니다.");
            }
        }

    }

    public void timePass(){
        jvm.setTimerState();
        if(jvm.getTimerState()) {
            System.out.println("이제부터 시간이 흘러갑니다.");
        }
        else{
            System.out.println("이제부터 시간이 흘러가지 않습니다.");
        }
        jvm.showData();
    }

    public void insertData(){
        System.out.println("저장할 데이터 형식을 고르세요.");
        System.out.println("1. 힙영역에 저장될 지역변수 데이터");
        System.out.println("2. 메타영역에 저장될 클래스 메타 데이터");
        int n = numberInput(1, 3);

        System.out.println("저장할 데이터의 이름을 입력하세요.");
        String name = sc.next();

        System.out.println("저장할 데이터의 크기를 정해주세요.");
        int size = numberInput(1);

        System.out.println("저장할 데이터의 유효기간을 정해주세요.");
        int exp = numberInput(1);

        try {
            if (n == 1) {
                jvm.insertHeapData(new Data(name, exp, size));
            } else {
                jvm.insertMetaData(new Data(name, exp, size));
            }
        }
        catch(IllegalArgumentException e){
            System.out.println("유효하지 않은 데이터가 입력됐습니다.");
        }
        jvm.showData();
    }

    public void dataInit(){
        System.out.println("힙 영역 데이터와 메타 스페이스를 정리하겠습니다.");
        System.out.println();
        jvm.initData();
        jvm.showData();
    }

    public void gcForce(){
        System.out.println("Full GC를 실행합니다.");
        System.out.println();
        jvm.garbageCollect();
        jvm.showData();
    }
}
