package org.example;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        List<String> a = new ArrayList<>();
        a.add("aa");
        a.add("bb");
        a.add("cc");

        List<String> b = new ArrayList<>();
        b.add("aa");
        b.add("cc");
        b.add("bb");
        String c = "ww";
        System.out.println(a.containsAll(b)) ;
    }
}
