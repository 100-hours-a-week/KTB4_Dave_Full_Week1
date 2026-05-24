package com.gcvisualization.gc;

import com.gcvisualization.memory.TopManager;
import org.junit.jupiter.api.BeforeEach;

public class MajorGCTest {
    TopManager topManager;


    @BeforeEach
    void setUp(){
        topManager = new TopManager();
    }


}
