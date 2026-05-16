public class Data {
    private String name;
    private int liveTime;
    private int size;
    private int gcTime;

    public Data(String name, int liveTime, int size){
        this.name = name;
        this.liveTime = liveTime;
        this.size = size;
        this.gcTime = 0;
    }

    public void decreaseLiveTime(int time){
        liveTime -= time;
    }

    public void surviveGC(){
        gcTime++;
    }

    public String getName(){
        return name;
    }

    public int getLiveTime(){
        return liveTime;
    }

    public boolean isLive(){
        return liveTime > 0;
    }

    public int getSize(){
        return size;
    }

    public boolean isPromotion(){
        return gcTime > 2;
    }
}
