public class ExceptionHandler {
    public static <T> void OOMEHandler(ThrowableConsumer<T> v, T t , Runnable r){
        try{
            v.execute(t);
        } catch (Throwable e) {
            if(e instanceof OutOfMemoryError){
                System.out.println("OOME 발생");
                System.out.println("프로세스 종료. 메모리 초기화를 진행합니다.");
                r.run();
            }
            else{
                e.printStackTrace();
            }
        }
    }

    public static <T> void OOMEHandler(VoidThrowableFunction v, Runnable r){
        try{
            v.execute();
        } catch (Throwable e) {
            if(e instanceof OutOfMemoryError){
                System.out.println("OOME 발생");
                System.out.println("프로세스 종료. 메모리 초기화를 진행합니다.");
                r.run();
            }
            else{
                e.printStackTrace();
            }
        }
    }
}
