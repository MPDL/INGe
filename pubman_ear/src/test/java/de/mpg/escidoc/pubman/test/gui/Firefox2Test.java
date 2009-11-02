package de.mpg.escidoc.pubman.test.gui;

import java.io.IOException;

import org.junit.Before;

public class Firefox2Test extends PubmanGuiTestcase
{
    public Firefox2Test() throws IOException
    {
        super();
    }

    @Before
    public void setUp() throws Exception {
        setUp("http://localhost:8080/", "*firefox /usr/lib/firefox/firefox-2-bin");
    }
}
