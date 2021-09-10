package com.kickass.ifssol.util.reflect;

import org.junit.Assert;
import org.junit.Test;

public class ReflectorTest {
    @Test
    public void testReflectorProcess() {
        Reflector reflector = new Reflector();
        try {
            reflector.process("org.openapplications.oagis.x9.SyncLocationDocument");
        } catch (ClassNotFoundException e) {
            Assert.fail("Failed to process the class " + e.getMessage());
        }
    }
}
