import java.util.concurrent.atomic.AtomicInteger;

public class TopManager {
    private static final int eden = 3;
    private static final int survivor1 = 4;
    private static final int survivor2 = 5;
    private static final int old = 15;
    private static final int meta = 5;
    private int edenTop;
    private final AtomicInteger youngTop = new AtomicInteger(eden);
    private final AtomicInteger oldTop = new AtomicInteger(survivor2);
    private int metaTop;
    private boolean nextSurvivor = false;

    public TopManager(){
        topInit();
    }

    public int getEdenTop(){
        return edenTop;
    }

    public void setEdenTop(int edenTop){
        this.edenTop = edenTop;
    }

    public int getYoungTop() {
        return youngTop.get();
    }

    public void setYoungTop(int youngTop) {
        this.youngTop.set(youngTop);
    }

    public int getOldTop() {
        return oldTop.get();
    }

    public void setOldTop(int oldTop) {
        this.oldTop.set(oldTop);
    }

    public int getMetaTop() {
        return metaTop;
    }

    public void setMetaTop(int metaTop) {
        this.metaTop = metaTop;
    }

    public void setNextSurvivor(){
        nextSurvivor = !nextSurvivor;
    }

    public boolean getNextSurvivor(){
        return nextSurvivor;
    }

    public void initEdenTop(){
        edenTop = 0;
    }

    public void initYoungTop(){
        youngTop.set(eden);
        nextSurvivor = false;
    }

    public void setNextYoungTop(){
        setNextSurvivor();
        if(nextSurvivor){
            youngTop.set(survivor1);
        }
        else{
            youngTop.set(eden);
        }
    }

    public int getYoungCopyLoc(int size){
        int loc;
        int next;
        boolean isPromotion = false;
        do{
            loc = youngTop.get();
            next = loc+size;
            System.out.println("loc next " + loc + " " + next);
            if(nextSurvivor){
                if(next > survivor2){
                    System.out.println("Surv2 검사 승격");
                    isPromotion = true;
                    break;
                }
            }
            else{
                if(next > survivor1){
                    System.out.println("Surv1 검사 승격");
                    isPromotion = true;
                    break;
                }
            }
        }while(!youngTop.compareAndSet(loc, next));

        if(isPromotion){
            do{
                loc = oldTop.get();
                next = loc + size;
                System.out.println("loc next " + loc + " " + next);
                if(next > old){
                    System.out.println("Old에서도 못함");
                    next = -1;
                    break;
                }

            } while(!oldTop.compareAndSet(loc, next));
        }
        System.out.println("next " + next);
        return loc;
    }

    public int getOldCopyLoc(int size){
        int loc;
        int next;
        do{
            loc = oldTop.get();
            next = loc + size;
            if(next > old){
                next = -1;
                break;
            }
        }while(oldTop.compareAndSet(loc, next));
        return loc;
    }

    public void initOldTop(){
        oldTop.set(survivor2);;
    }

    public void initMetaTop(){
        metaTop = 0;
    }

    public void topInit(){
        initEdenTop();
        initYoungTop();
        initOldTop();
        initMetaTop();
    }

    public static int getEden(){
        return eden;
    }

    public static int getSurvivor1(){
        return survivor1;
    }

    public static int getSurvivor2(){
        return survivor2;
    }

    public static int getOld(){
        return old;
    }

    public static int getMeta(){
        return meta;
    }
}
