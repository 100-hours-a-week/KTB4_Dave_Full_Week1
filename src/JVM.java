public class JVM {
    private final MemoryTimePass memoryTimePass;
    private final MemoryManager memoryManager;
    private boolean isTimerOn = false;

    public JVM(MemoryTimePass memoryTimePass, MemoryManager memoryManager) {
        this.memoryTimePass = memoryTimePass;
        this.memoryManager = memoryManager;
    }

    public void setTimerState(){
        isTimerOn = !isTimerOn;
        memoryTimePass.timeStart();
    }
    public boolean getTimerState(){
        return isTimerOn;
    }

    public void garbageCollect(){
        if(isTimerOn) {
            memoryTimePass.timePass();
        }
        ExceptionHandler.OOMEHandler(memoryManager::garbageCollect, this::initData);
    }

    public void insertHeapData(Data d){
        if(isTimerOn) {
            memoryTimePass.timePass();
        }
        ExceptionHandler.OOMEHandler(memoryManager::insertHeapData, d, this::initData);
    }

    public void insertMetaData(Data d){
        if(isTimerOn) {
            memoryTimePass.timePass();
        }
        ExceptionHandler.OOMEHandler(memoryManager::insertMetaData, d, this::initData);
    }

    public void showData(){
        memoryManager.showData();
    }

    public void nowData(){
        if(isTimerOn){
            memoryTimePass.timePass();
        }
        memoryManager.showData();
    }

    public void shutDown(){
        memoryManager.executorServiceShutdown();
    }

    public void initData(){
        memoryManager.initData();
    }
}
