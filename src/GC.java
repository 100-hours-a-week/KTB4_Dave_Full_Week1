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
    public int[] execute(){
        // result[0] 에는 oldTop, [1]에는 정리한 메모리 수를 전송한다.
        int[] result = new int[2];
        this.search();
        result[1] = this.cleaning();
        result[0] = top;
        return result;
    }

    public void setTop(int top){
        this.top = top;
    }
    protected abstract void search();
    protected abstract int cleaning();
}
