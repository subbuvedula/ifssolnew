package com.kickass.ifssol.config;

import com.kickass.ifssol.entity.SolNodesRoot;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SolIFSMappingConfigNewTest {
    @Test
    public void testLoadMappings() {
        SolIFSMappingConfigNew solIFSMappingConfig = new SolIFSMappingConfigNew();
        try {
            List<SolNodesRoot> rootList = new ArrayList<>();
            solIFSMappingConfig.loadMappings("C:\\IFSSOL\\mappings\\incoming",rootList );

            int size = rootList.size();
            Assert.assertTrue(size > 0);
        } catch (Exception e) {
            Assert.fail("testLoadMappings failed , " + e.getMessage());
        }

    }
}
