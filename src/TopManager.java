public class TopManager {
    private int edenTop;
    private int youngTop;
    private int oldTop;
    private int metaTop;
    private boolean nextSurvivor = false;
    private static final int eden = 3;
    private static final int survivor1 = 4;
    private static final int survivor2 = 5;
    private static final int old = 15;
    private static final int meta = 5;

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
        return youngTop;
    }

    public void setYoungTop(int youngTop) {
        this.youngTop = youngTop;
    }

    public int getOldTop() {
        return oldTop;
    }

    public void setOldTop(int oldTop) {
        this.oldTop = oldTop;
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
        youngTop = eden;
        nextSurvivor = false;
    }

    public void setNextYoungTop(){
        setNextSurvivor();
        if(nextSurvivor){
            youngTop = survivor1;
        }
        else{
            youngTop = eden;
        }
    }

    public void initOldTop(){
        oldTop = survivor2;;
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
