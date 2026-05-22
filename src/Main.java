import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 사용자가 힙 크기 설정을 할 경우 적당한 비율이 아닐 수 있어 단순하게 고정 크기로 제공
        // Eden과 Survivor의 크기 비는 8:1:1이 평균이지만 그럴 경우 테스트에 진행이 느려질 수 있어 적당히 작은 크기 채택
        final TopManager topManager = new TopManager();
        final int eden = TopManager.getEden();
        final int young = TopManager.getSurvivor2();
        final int surv1Bound = TopManager.getSurvivor1();
        final int old = TopManager.getOld();
        final int metaSize = TopManager.getMeta();
        Data[] heap = new Data[old];
        Data[] meta = new Data[metaSize];

        final MinorGC minorGC = new MinorGC(heap, 0, young, topManager);
        final MajorGC majorGC = new MajorGC(heap,young, old, topManager);
        final FullGC fullGC = new FullGC(heap, 0, old, topManager, meta);
        final MemoryTimePass memoryTimePass = new MemoryTimePass(heap, meta);
        final MemoryManager memoryManager = new MemoryManager(heap, meta, minorGC, majorGC, fullGC, topManager);
        final JVM jvm = new JVM(memoryTimePass, memoryManager);

        final Controller controller = new Controller(jvm, sc);

        System.out.println("GC의 작업에 대해 단순화하여 표현해주는 프로그램입니다.");
        System.out.println("현재 힙영역의 크기는 " + old + "입니다.");
        System.out.println("영역 구성은 Eden: " + eden + "/ survivor1,2: " + (surv1Bound-eden) +"x2/"  + " Old Generation: "+ (old-young)+ " 입니다");
        System.out.println("메타 데이터를 저장하는 메타스페이스의 크기는 " + metaSize + " 입니다.");
        System.out.println("데이터 표현 형식은 | 이름 : 유효기간 | 으로 표시되고 빈 공간의 경우 | X : X | 로 표시됩니다.");

        jvm.showData();

        controller.run();

    }
}