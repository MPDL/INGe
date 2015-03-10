/**
 * 
 */
package de.mpg.escidoc.tools.reindex;

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author franke
 *
 */
public class XSLTFunctions
{
	public static String toASCII(String input)
	{
		TokenStream result = null;
		ASCIIFoldingFilter filter = new ASCIIFoldingFilter(null);
		char[] arr = input.toCharArray();
		filter.foldToASCII(arr, arr.length);
		return new String(arr);
	}
	
	public static void main(String[] args)
	{
		System.out.println(XSLTFunctions.toASCII(args[0]));
	}
}
