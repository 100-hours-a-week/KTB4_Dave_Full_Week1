public class FullGC extends MajorGC{
    private Data[] meta;
    private boolean[] metaMarking;
    private int youngBound;
    private int metaTop;

    public FullGC(Data[] heap, int start, int end, int top, Data[] meta, int youngBound, int metaTop) {
        super(heap, start, end, top);
        this.meta = meta;
        metaMarking = new boolean[meta.length];
        this.youngBound = youngBound;
        this.metaTop = metaTop;
    }

    public void setMetaTop(int top){
        this.metaTop = top;
    }

    public int getMetaTop(){
        return metaTop;
    }

    @Override
    protected void search(){
        super.search();
        for(int i = 0; i < metaTop; i++){
            if(meta[i] != null){
                if(meta[i].isLive()){
                    metaMarking[i] = true;
                }
            }
        }
    }

    @Override
    protected int cleaning(){
        // Full GC의 경우 반환하는 값이 작으면 OOME와 같은 예외를 던지게 하는 것도 고려해봐야 함.
        // 아니면 호출하는 쪽에서 0이면 에러 던지게 하는 게 나을 듯
        int result = 0;

        result += super.cleaning();

        for(int i = 0; i < metaTop; i++){
            if(meta[i] != null){
                if(!metaMarking[i]){
                    meta[i] = null;
                    result++;
                }
            }
            else{
                break;
            }
        }


        return result;
    }

    @Override
    protected void compacting(){
        // 아니면 Young과 Old 나눠서 할지 정해야 함

        compacting(heap, start, youngBound);

        metaTop = compacting(meta, 0, metaTop);
    }
}
