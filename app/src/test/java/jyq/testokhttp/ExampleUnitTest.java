package jyq.testokhttp;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    
    @Test
    public void testOkHttpUtilGet(){
        try {
            final String asString = OkhttpUtil.getAsString("http://oacisqzry.bkt.clouddn.com/readme.txt");
            System.out.println(asString);
            assertEquals(asString, "echo, you are the best!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}