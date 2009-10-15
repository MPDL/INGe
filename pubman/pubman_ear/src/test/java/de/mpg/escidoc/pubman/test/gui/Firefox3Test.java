package de.mpg.escidoc.pubman.test.gui;

import java.io.IOException;

import org.junit.Before;

public class Firefox3Test extends PubmanGuiTestcase
{
    public Firefox3Test() throws IOException
    {
        super();
    }

    @Before
    public void setUp() throws Exception {
        setUp("http://localhost:8080/", "*firefox /usr/lib/firefox-3.5.3/firefox-3.5");
    }
}
