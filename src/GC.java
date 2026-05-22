public abstract class GC {
    protected Data[] heap;
    protected int start;
    protected int end;
    protected TopManager topManager;

    public GC(Data[] heap, int start, int end, TopManager topManager){
        this.heap = heap;
        this.start = start;
        this.end = end;
        this.topManager = topManager;
    }


    public void execute(){
        System.out.println(this.getClass().getName()+" 발생");
        this.search();
        this.cleaning();
    }

    protected abstract void search();
    protected abstract int cleaning();
}
