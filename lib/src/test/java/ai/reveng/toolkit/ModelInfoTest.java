package ai.reveng.toolkit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelInfoTest {
    @Test void testStringConstructor() {
        ModelInfo mi = new ModelInfo("binnet-13.4");
        assertEquals(mi.getName(), "binnet");
        System.out.println(mi.getMajVersion());
        assertEquals(mi.getMajVersion(), 13);
        assertEquals(mi.getMinVersion(), 4);
    }
}