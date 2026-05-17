import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 사용자가 힙 크기 설정을 할 경우 적당한 비율이 아닐 수 있어 단순하게 고정 크기로 제공
        // Eden과 Survivor의 크기 비는 8:1:1이 평균이지만 그럴 경우 테스트에 진행이 느려질 수 있어 적당히 작은 크기 채택
        Data[] heap = new Data[15];
        Data[] meta = new Data[5];
        final int eden = 3;
        final int young = 5;
        final int surv1Bound = 4;
        final int old = 15;

        final MinorGC minorGC = new MinorGC(heap, 0, young, 0, eden, surv1Bound);
        final MajorGC majorGC = new MajorGC(heap,young, old, young);
        final FullGC fullGC = new FullGC(heap, 0, old, young, meta, young, 0);
        final JVM jvm = new JVM(heap, meta, minorGC, majorGC, fullGC, eden, young);

        final Controller controller = new Controller(jvm, sc);

        System.out.println("GC의 작업에 대해 단순화하여 표현해주는 프로그램입니다.");
        System.out.println("현재 힙영역의 크기는 " + 15 + "입니다.");
        System.out.println("영역 구성은 Eden: " + 3 + "/ survivor1,2: " + 1 +"x2/"  + " Old Generation: "+ 10+ " 입니다");
        System.out.println("메타 데이터를 저장하는 메타스페이스의 크기는 " + 5 + " 입니다.");
        System.out.println("데이터 표현 형식은 | 이름 : 유효기간 | 으로 표시되고 빈 공간의 경우 | X : X | 로 표시됩니다.");

        jvm.showData();

        controller.run();

    }
}