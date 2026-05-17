public abstract class GC {
    protected Data[] heap;
    protected int start;
    protected int end;
    protected int top;

    public GC(Data[] heap, int start, int end, int top){
        this.heap = heap;
        this.start = start;
        this.end = end;
        this.top = top;
    }
    public GCResult execute(){
        // result[0] 에는 oldTop, [1]에는 정리한 메모리 수를 전송한다.
        GCResult gcResult = new GCResult();
        this.search();
        gcResult.setOldTop(this.cleaning());
        gcResult.setCleanDataSize(top);
        return gcResult;
    }

    public void setTop(int top){
        this.top = top;
    }
    protected abstract void search();
    protected abstract int cleaning();
}
