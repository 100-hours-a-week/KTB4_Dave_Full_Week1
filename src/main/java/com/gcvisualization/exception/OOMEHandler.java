package com.gcvisualization.exception;

import com.gcvisualization.functional.ThrowingConsumer;
import com.gcvisualization.functional.ThrowingRunnable;

public class OOMEHandler {
    private static final String OOME_MSG = """
            OOME 발생
            프로세스 종료. 메모리 초기화를 진행합니다.
            """;
    public static <T> void execute(ThrowingConsumer<T> v, T t , Runnable r){
        try{
            v.execute(t);
        } catch (OutOfMemoryError e) {
            System.out.println(OOME_MSG);
            r.run();
        }
    }

    public static void execute(ThrowingRunnable v, Runnable r){
        try{
            v.execute();
        } catch (OutOfMemoryError e) {
            System.out.println(OOME_MSG);
            r.run();
        }
    }
}
