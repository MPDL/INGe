package test;

import static org.junit.Assert.*;

import org.junit.Test;

public class RegTest
{
    static final String sub = "<sub>xx</sub>";
    static final String sup = "<sup>xx</sup>";
    

    
    @Test
    public void test1()
    {
        String s1 = sub.replaceAll("<(?!su|/su)", "&lt;");
        String s2 = sup.replaceAll("<(?!su|/su)", "&lt;");
        String s3 = "aa < bb".replaceAll("<(?!su|/su)", "&lt;");

        assertTrue(s1.equals(sub));
        assertTrue(s2.equals(sup));
        assertTrue(s3.equals("aa &lt; bb"));
       
    }
    
    @Test
    public void test2()
    {
        String s1 = sub.replaceAll("(?<!su.)>", "&gt;");
        String s2 = sup.replaceAll("(?<!su.)>", "&gt;");        
        String s3 = "aa > bb".replaceAll("(?<!su.)>", "&gt;");
        
        assertTrue(s1.equals(sub));
        assertTrue(s2.equals(sup));
        assertTrue(s3.equals("aa &gt; bb"));
    }
}
