package com.gcvisualization.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TopManagerTest {
    private TopManager topManager;

    @BeforeEach
    void setUp(){
        topManager = new TopManager();
    }

    @Test
    void allocateEden_whenEnoughSpace_thenSuccess(){
        int address = topManager.allocateEden(3);

        assertTrue(address >= 0);

    }

    @Test
    void allocateEden_whenEdenFull_thenFail(){
        for(int i = 0; i < TopManager.EDEN_END; i++){
            topManager.allocateEden(1);
        }

        int result = topManager.allocateEden(1);

        assertEquals(
                TopManager.RETRYABLE_FAILURE,
                result
        );
    }

    @Test
    void allocateEden_whenObjectLarge_thenFail(){
        int result = topManager.allocateEden(TopManager.EDEN_END+1);

        assertEquals(TopManager.DATA_TOO_LARGE, result);
    }

    @Test
    void allocateSurvivor_whenEnoughSpace_thenSuccess(){
        int address = topManager.allocateSurvivor(1);

        assertTrue(address >= 0);
    }

    @Test
    void allocateSurvivor_whenSurvivorFull_thenFail(){
        for(int i = 0; i < TopManager.SURVIVOR_1_END - TopManager.EDEN_END; i++){
            topManager.allocateSurvivor(1);
        }

        int result = topManager.allocateSurvivor(1);

        assertEquals(
                TopManager.RETRYABLE_FAILURE,
                result
        );
    }

    @Test
    void allocateOld_whenEnoughSpace_thenSuccess(){
        int address = topManager.allocateOld(1);

        assertTrue(address >= 0);
    }

    @Test
    void allocateOld_whenObjectLarge_thenFail(){
        int result = topManager.allocateOld(TopManager.OLD_END - TopManager.SURVIVOR_2_END + 1);

        assertEquals(TopManager.DATA_TOO_LARGE, result);
    }

    @Test
    void allocateOld_whenOldFull_thenFail(){
        for(int i = 0; i < TopManager.OLD_END-TopManager.SURVIVOR_2_END; i++){
            topManager.allocateOld(1);
        }

        int result = topManager.allocateOld(1);

        assertEquals(
                TopManager.RETRYABLE_FAILURE,
                result
        );
    }

    @Test
    void allocateMeta_whenEnoughSpace_thenSuccess(){
        int address = topManager.allocateMeta(1);

        assertTrue(address >= 0);
    }

    @Test
    void allocateMeta_whenObjectLarge_thenFail(){
        int result = topManager.allocateMeta(TopManager.META_END+1);

        assertEquals(TopManager.DATA_TOO_LARGE, result);
    }

    @Test
    void allocateMeta_whenMetaFull_thenFail(){
        for(int i = 0; i < TopManager.META_END; i++){
            topManager.allocateMeta(1);
        }

        int result = topManager.allocateMeta(1);

        assertEquals(
                TopManager.RETRYABLE_FAILURE,
                result
        );
    }


}
