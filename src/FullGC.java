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
        // 정리한 데이터 크기를 반환한다.
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

        compacting();
        return result;
    }

    @Override
    protected void compacting(){
        compacting(heap, start, youngBound);
        top = compacting(heap, youngBound, end);
        metaTop = compacting(meta, 0, metaTop);
    }
}
