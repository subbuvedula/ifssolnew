package com.kickass.ifssol.config;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SolIFSMappingConfigNewTest {
    @Test
    public void testLoadMappings() {
        SolIFSMappingConfigNew solIFSMappingConfig = new SolIFSMappingConfigNew();
        try {
            //solIFSMappingConfig.loadMappings("incoming");
            //int size = solIFSMappingConfig.getSolNodesRootList().size();
            //Assert.assertTrue(size > 0);
        } catch (Exception e) {
            Assert.fail("testLoadMappings failed , " + e.getMessage());
        }

    }
}
