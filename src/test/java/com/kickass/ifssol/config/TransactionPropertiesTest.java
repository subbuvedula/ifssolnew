package com.kickass.ifssol.config;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
@Ignore
public class TransactionPropertiesTest {
    @Test
    public void testLoadProps() {
        try {
            SolIFSMappingConfig solIFSMappingConfig = new SolIFSMappingConfig();
            solIFSMappingConfig.loadMappings();
            TransactionPropertiesConfig transactionProperties = new TransactionPropertiesConfig(solIFSMappingConfig);
            transactionProperties.prepare();
            int size = transactionProperties.getTransactionMetadata().size();
            Assert.assertTrue(size > 0);
        } catch (IOException e) {
            Assert.fail("TransactionProperties load failed");
        }
    }
}
