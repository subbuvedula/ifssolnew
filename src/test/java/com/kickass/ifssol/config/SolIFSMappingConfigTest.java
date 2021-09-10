package com.kickass.ifssol.config;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SolIFSMappingConfigTest {
    @Test
    public void testLoadMappings() {
        SolIFSMappingConfig solIFSMappingConfig = new SolIFSMappingConfig();
        try {
            solIFSMappingConfig.loadMappings();
        } catch (IOException e) {
            Assert.fail("testLoadMappings failed , " + e.getMessage());
        }
        int size = solIFSMappingConfig.getTxnMappings().size();
        Assert.assertTrue(size > 0);
    }
}
