public class JVM {

    private final MemoryManager memoryManager;

    public JVM(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
    }


    public void nextTime(int time){
        memoryManager.nextTime(time);
    }


    public void garbageCollect(){
        ExceptionHandler.OOMEHandler(memoryManager::garbageCollect, this::initData);
    }

    public void insertHeapData(Data d){
        ExceptionHandler.OOMEHandler(memoryManager::insertHeapData, d, this::initData);
    }

    public void insertMetaData(Data d){
        ExceptionHandler.OOMEHandler(memoryManager::insertMetaData, d, this::initData);
    }

    public void showData(){
        memoryManager.showData();
    }

    public void initData(){
        memoryManager.initData();
    }
}
