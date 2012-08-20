/**
 * 
 */
package de.mpg.escidoc.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author franke
 *
 */
public class ProxyTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		URL oracle = new URL(args[0]);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                    yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) 
            System.out.println(inputLine);
        in.close();
	}

}
