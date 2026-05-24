package com.gcvisualization.gc;

import com.gcvisualization.memory.Data;
import com.gcvisualization.memory.TopManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MinorGCTest {
    private TopManager topManager;
    private MinorGC minorGC;
    private Data[] data;


    @BeforeEach
    void setUp(){
        topManager = new TopManager();
        data = new Data[TopManager.OLD_END];
        minorGC = new MinorGC(data, 0, TopManager.SURVIVOR_2_END, topManager);
    }

    private boolean existsInRange(Data target, int start, int end){
        for(int i = start; i < end; i++){
            if(data[i] == target){
                return true;
            }
        }
        return false;
    }

    @Test
    void minorGC_shouldCopyLiveObjectToSurvivor(){
        Data d = new Data("test", 1, 1);
        int now = topManager.allocateEden(1);
        data[now] = d;

        int survivor = TopManager.EDEN_END;
        int survivorEnd = TopManager.SURVIVOR_1_END;
        if(topManager.getNextSurvivor()){
            survivor = TopManager.SURVIVOR_1_END;
            survivorEnd = TopManager.SURVIVOR_2_END;
        }
        minorGC.execute();

       assertTrue(existsInRange(d, survivor, survivorEnd));
    }

    @Test
    void minorGC_shouldCleanYoungGeneration(){
        Data edenTest = new Data("eden", 1, 1);
        Data survivorTest = new Data("survivor", 1, 1);
        while(edenTest.isLive()) {
            edenTest.decreaseLiveTime(1);
        }
        while(survivorTest.isLive()) {
            survivorTest.decreaseLiveTime(1);
        }
        int now = topManager.allocateEden(1);
        data[now] = edenTest;
        now = topManager.allocateSurvivor(1);
        data[now] = survivorTest;
        // 기존의 allocateSurvivor이 복사할 위치를 할당하는 것을 고려해 nextSurvivor 전환 필요
        topManager.setNextSurvivorTop();


        minorGC.execute();
        int youngEnd = TopManager.SURVIVOR_2_END;

        assertFalse(existsInRange(edenTest, 0, youngEnd));
        assertFalse(existsInRange(survivorTest, 0, youngEnd));
    }

    @Test
    void minorGC_shouldNotCopyDeadObject(){
        Data edenTest = new Data("eden", 1, 1);
        Data survivorTest = new Data("survivor", 1, 1);
        while(edenTest.isLive()) {
            edenTest.decreaseLiveTime(1);
        }
        while(survivorTest.isLive()) {
            survivorTest.decreaseLiveTime(1);
        }

        int now = topManager.allocateEden(1);
        data[now] = edenTest;
        now = topManager.allocateSurvivor(1);
        data[now] = survivorTest;
        // 기존의 allocateSurvivor이 복사할 위치를 할당하는 것을 고려해 nextSurvivor 전환 필요
        topManager.setNextSurvivorTop();

        minorGC.execute();
        int oldEnd = TopManager.OLD_END;

        assertFalse(existsInRange(edenTest, 0, oldEnd));
        assertFalse(existsInRange(survivorTest, 0, oldEnd));
    }

    @Test
    void minorGC_shouldPromotionLiveObjectToOld(){
        Data d = new Data("test", 1, 1);
        while(!d.isPromotion()){
            d.surviveGC();
        }
        int now = topManager.allocateEden(1);
        data[now] = d;

        minorGC.execute();
        assertTrue(existsInRange(d, TopManager.SURVIVOR_2_END, TopManager.OLD_END));
    }

    @Test
    void minorGC_shouldIncreaseObjectAge(){
        Data d = new Data("test", 1, 1);
        int now = topManager.allocateEden(1);
        data[now] = d;
        int before = d.getGcTime();
        minorGC.execute();

        assertEquals(before+1, d.getGcTime());
    }

    @Test
    void minorGC_whenPromotionFails_thenThrowOOME(){
        Data d = new Data("can't promote", 1, 1);
        while(topManager.allocateOld(1) != TopManager.RETRYABLE_FAILURE){
            // 실제로 old 영역에 데이터 저장은 하지 않지만 topManager의 oldTop을 변화시켜 가득 찬 상태로 인식시킴
        }
        while(!d.isPromotion()){
            d.surviveGC();
        }
        int now = topManager.allocateEden(1);
        data[now] = d;

        assertThrows(OutOfMemoryError.class, minorGC::execute);

    }
}
