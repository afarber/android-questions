package de.afarber.rotatedlist;

import static org.junit.Assert.assertEquals;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    @Test
    public void applicationContext() {
        assertEquals("de.afarber.rotatedlist", InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getPackageName());
    }
}
