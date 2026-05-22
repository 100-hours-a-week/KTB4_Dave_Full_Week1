public class MemoryTimePass {
    private final Data[] heap;
    private final Data[] meta;
    private long start;
    private long remain = 0;
    private final long msToSecond = 1000;


    public MemoryTimePass(Data[] heap, Data[] meta) {
        this.heap = heap;
        this.meta = meta;
    }

    public void timeStart(){
        start = System.currentTimeMillis();
    }

    public void timePass(){
        long now = System.currentTimeMillis();
        int time =  (int) ((now-start + remain)/msToSecond);
        remain = (now-start + remain) % msToSecond;

        start = now;
        timePass(heap, time);
        timePass(meta, time);
    }

    private void timePass(Data[] dataList, int time){
        for(int i = 0; i < dataList.length; i++){
            if(dataList[i] != null && dataList[i].isLive()){
                dataList[i].decreaseLiveTime(time);
                i += dataList[i].getSize()-1;
            }
        }
    }
}
