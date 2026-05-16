public class MajorGC extends GC{
    protected boolean[] marking;
    public MajorGC(Data[] heap, int start, int end, int top) {
        super(heap, start, end, top);
        this.marking = new boolean[end - start];
    }

    @Override
    protected void search() {
        for(int i = start; i < top; i++){
            if(heap[i] != null) {
                if (heap[i].isLive()) {
                    marking[i - start] = true;
                }
            }

        }
    }

    @Override
    protected int cleaning() {
        // 실제로는 GC Root로 탐색해서 도달하지 못한 객체를 지워야 하지만 단순화하는 과정에서 실제 참조가 아닌 생존기간을 이용해서 cleaning으로 나눈 의미가 없어보임
        // 이런 식으로 단순화했을 때는 오히려 생존이 아닌 죽은 객체만 표시해서 해당 객체들에 대해 정리하는게 더 비슷한 작동방식으로 보일 수도 있었겠다 싶음
        int result = 0;
        for(int i = start; i < top; i++){
            if(heap[i] != null){
                if(!marking[i - start]){
                    heap[i] = null;
                    result++;
                }
            }
        }
        compacting();

        return result;
    }

    protected void compacting() {
        // 죽은 객체 삭제 후 빈 자리를 당겨서 연속된 빈 공간을 크게 만들어 준다.
        top = compacting(heap, start, top);
    }

    protected int compacting(Data[] data, int start, int end){
        // 죽은 객체 삭제 후 빈 자리를 당겨서 연속된 빈 공간을 크게 만들어 준다.
        int compTop = start;

        for(int i = start; i < end; i++){
            if(data[i] != null){
                int size = data[i].getSize();
                Data d = data[i];
                if(i > compTop) {
                    for (int j = i; j < i + size; j++) {
                        data[j] = null;
                    }
                    for (int j = 0; j < size; j++) {
                        data[compTop + j] = d;
                    }
                }
                else{
                    i += size-1;
                }
                compTop += size;
            }
        }
        return compTop;
    }
}
