import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MemoryManager {
    private final Data[] heap;
    private final Data[] meta;
    private final MinorGC[] minorGCS;
    private final GC majorGC;
    private final FullGC fullGC; // metaTop 데이터 주고받기 위해 GC 대시 FullGC 사용
    private final TopManager topManager;
    private static final int threadPoolSize = 2;
    private final ExecutorService executorService;

    public MemoryManager(Data[] heap, Data[] meta, MinorGC[] minorGCS, GC majorGC, FullGC fullGC, TopManager topManager){
        this.heap = heap;
        this.meta = meta;
        this.minorGCS = minorGCS;
        this.majorGC = majorGC;
        this.fullGC = fullGC;
        this.topManager = topManager;
        executorService = Executors.newFixedThreadPool(threadPoolSize);
    }


    private void garbageCollect(GC gc){
        // 원래 GC의 경우 major GC 같은 게 실패하면 Full GC를 시행해야 하나 여기서는 soft reference 삭제 등의 작동을 구현하지 않았기 때문에
        // major GC만 실패해도 OOME 발생으로 대체
        gc.execute();
    }

    public void garbageCollect(){
        // FullGC 실행
        int youngBound = TopManager.getSurvivor2();
        int oldTop = topManager.getOldTop();
        garbageCollect(this.fullGC);
        if(oldTop < youngBound){ //? 왜 이런 코드가?
            topManager.initOldTop();
        }
    }

    public void insertHeapData(Data d){
        // 에덴에 값을 넣을 때 자리가 부족하면 minor GC 실행
        // minor GC 검사 전에 조건 만족 시 major GC를 먼저 수행.
        int eden = TopManager.getEden();
        int youngBound = TopManager.getSurvivor2();
        int oldTop = topManager.getOldTop();
        int edenTop = topManager.getEdenTop();

        if(!checkDataSize(d, eden)){
            if(!checkDataSize(d, heap.length - youngBound)){
                throw new OutOfMemoryError();
            }
            if(!checkDataSize(oldTop, d, heap.length)){
                garbageCollect(majorGC);
                oldTop = topManager.getOldTop();
            }
            if(!checkDataSize(oldTop, d, heap.length)){
                throw new OutOfMemoryError();
            }
            else{
                for(int i = oldTop; i < oldTop+d.getSize(); i++){
                    heap[i] = d;
                }
                topManager.setOldTop(oldTop+d.getSize());
                return;
            }
        }

        if(!checkDataSize(edenTop, d, eden)){
            if(heap.length - oldTop < eden){
                // Old Generation의 남은 공간이 eden보다 작은 경우 majorGC 실행
                garbageCollect(majorGC);
            }
            try {
                int surv = 1;
                if(!topManager.getNextSurvivor()){
                    surv++;
                }
                CompletableFuture<Void> edenFuture =
                        CompletableFuture.runAsync(minorGCS[0], executorService);
                CompletableFuture<Void> survivorFuture =
                        CompletableFuture.runAsync(minorGCS[surv], executorService);

                edenFuture.join();
                survivorFuture.join();
                topManager.initEdenTop();
                topManager.setNextYoungTop();
                edenTop = topManager.getEdenTop();
            }
            catch(OutOfMemoryError t){
                throw new OutOfMemoryError();
            }
        }
        for(int i = edenTop; i < edenTop+d.getSize(); i++){
            heap[i] = d;
        }
        topManager.setEdenTop(edenTop+d.getSize());
    }

    public void insertMetaData(Data d){
        // meta 데이터 넣을 자리 부족하면 full GC 실행
        // 여기선 콘솔 프로그램이니 하나씩만 넣으니까 Full GC 시 1 이상의 값만 확보되면 상관없고 0일 경우 에러 던지기
        int metaTop = topManager.getMetaTop();

        if(!checkDataSize(d, meta.length)){
            throw new OutOfMemoryError();
        }

        if(!checkDataSize(metaTop, d, meta.length)){
            garbageCollect();
            metaTop = topManager.getMetaTop();
        }
        if(!checkDataSize(metaTop, d, meta.length)){
            throw new OutOfMemoryError();
        }
        for(int i = metaTop; i < metaTop + d.getSize(); i++){
            meta[i] = d;
        }
        topManager.setMetaTop(metaTop + d.getSize());
    }



    public void initData(){
        // 모든 데이터 비우기
        for(int i = 0; i < heap.length; i++){
            heap[i] = null;
        }
        for(int i = 0; i < meta.length; i++){
            meta[i] = null;
        }
        topManager.topInit();
    }

    private boolean checkDataSize(Data d, int size){
        return d.getSize() <= size;
    }

    private boolean checkDataSize(int start, Data d, int end){
        return start + d.getSize() <= end;
    }

    public void showData(){
        DataPrinter.printData(heap, "heap");
        DataPrinter.printData(meta, "meta");
    }

    public void executorServiceShutdown(){
        executorService.shutdown();
    }
}
