/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.citationmanager.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;

/**
 * An instance of this class represents 
 * a Collection of {@link FontStyle}s 
 * 
 * @author vmakarenko (initial creation)  
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class FontStylesCollection implements Cloneable {

    private String name;						// Name of collection
    private List<FontStyle> fontStyles;			// List of font styles 
    private FontStyle defaultFontStyle;			// default font stlye
    
//  Default FONTSTYLE_NAME (hardcoded). Should be taken from from defaultFontStyle.getName() 
    public static final String DEFAULT_FONTSTYLE_NAME = "NORMAL";  

	public FontStylesCollection(){
    	setDefault();
    } 
    
    public void setDefault() {
    	setName("Default");
    	setFontStyles(new ArrayList<FontStyle>());
    	setDefaultFontStyle(null);
    }

    public void setName( String newName ) { name = newName; }
    public String getName() { return name; }

    public List<FontStyle> getFontStyles() {
      return fontStyles;
    }

    public void setFontStyles(List<FontStyle> fontStyles) {
      this.fontStyles = fontStyles;
    }

    public void addFontStyle(FontStyle fs) {
        for ( FontStyle fstemp: fontStyles ) {
        // do not add if there is font with same name
            if ( fstemp.getName().equals(fs.getName()) ) {
                return;
            }
        }
        fontStyles.add(fs);
    }

    public void setDefaultFontStyle(FontStyle defaultFontStyle) {
		this.defaultFontStyle = defaultFontStyle;
	}
    
    public FontStyle getDefaultFontStyle() {
        return defaultFontStyle;
    }

    public void findDefaultFontStyle() {
        for ( FontStyle fs: fontStyles ) {
            if (fs.getDef()) {
                setDefaultFontStyle(fs);
                break;
            }
        }
    }

    public FontStyle getFontStyleByName( String name ) {
        for ( FontStyle fs: fontStyles ) {
            if (fs.getName().equals(name)) {
                return fs;
            }
        }
//      throws Exception if not found (should be organized )
        return null;
    }

    
    public void removeCssClass()
    {
        for ( FontStyle fs: fontStyles ) 
        {
        	fs.setCssClass(null);
        }
               	
    }


    public String toString() {

        String str = "";
        int i = 0;
        for ( FontStyle fs: fontStyles ) {
            i++;
            str += i+")" + fs + "\n";
        }
        return str + "\nDefault font:" + defaultFontStyle;
    }

    /**
     * Loads {@link FontStylesCollection} from xmlfile
     * @param xmlFileName
     * @return {@link FontStylesCollection}
     * @throws IOException 
     * @throws SAXException
     */
    public static FontStylesCollection loadFromXml( String xmlFileName )  throws IOException, SAXException {

        Digester digester = new Digester();
        digester.setValidating(false);
        
        String path = "font-styles-collection";
        digester.addObjectCreate(path, FontStylesCollection.class.getName());
        
        path += "/font-style";
        digester.addObjectCreate(path, FontStyle.class.getName());
        
        digester.addSetProperties(path);
        digester.addSetProperties(path, "font-size", "fontSize");
        digester.addSetProperties(path, "font-name", "fontName");
        digester.addSetProperties(path, "is-bold", "isBold");
        digester.addSetProperties(path, "is-italic", "isItalic");
        digester.addSetProperties(path, "is-underline", "isUnderline");
        digester.addSetProperties(path, "is-strike-through", "isStrikeThrough");
        digester.addSetProperties(path, "pdf-font-name", "pdfFontName");
        digester.addSetProperties(path, "is-underline", "isUnderline");
        digester.addSetProperties(path, "fore-color", "foreColor");
        digester.addSetProperties(path, "back-color", "backColor");
        digester.addSetProperties(path, "pdf-encoding", "pdfEncoding");
        digester.addSetProperties(path, "is-pdf-embedded", "isPdfEmbedded");
        digester.addSetProperties(path, "css-class", "cssClass");

        
        digester.addSetNext(path, "addFontStyle");

        FileInputStream input = new FileInputStream( xmlFileName );
        FontStylesCollection fsc = (FontStylesCollection)digester.parse( input );
        fsc.findDefaultFontStyle();

        return fsc;

    }

    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch(CloneNotSupportedException e) {
        // should never happen
        }

        ((FontStylesCollection)clone).setName(getName());

        ((FontStylesCollection)clone).setFontStyles(new ArrayList<FontStyle>());

        for ( FontStyle fs: fontStyles ) {
            ((FontStylesCollection)clone).addFontStyle((FontStyle)fs.clone());
        }
        return clone;
    }


    /**
     * Writes {@link FontStylesCollection} to xmlfile
     * @param xmlFileName
     * @throws IOException
     * @throws SAXException
     * @throws CitationStyleManagerException 
     */
    public void writeToXml( String xmlFileName )  throws IOException, SAXException, CitationStyleManagerException {

    	Document doc = XmlHelper.createDocument();
    	
        Element root = doc.createElement("font-styles-collection");
        
        doc.appendChild(root);

        //here is the loop:

        for ( FontStyle fs: fontStyles ) {

            Element element = doc.createElement("font-style");

                element.setAttribute("def", "" + fs.getDef());
                element.setAttribute("name", fs.getName());
                element.setAttribute("font-size", "" + fs.getFontSize());
                element.setAttribute("font-name", fs.getFontName());
                element.setAttribute("is-bold", "" + fs.getIsBold());
                element.setAttribute("is-italic", "" + fs.getIsItalic());
                element.setAttribute("is-underline", "" + fs.getIsUnderline());
                element.setAttribute("is-strike-through", "" + fs.getIsStrikeThrough());
                element.setAttribute("pdf-font-name",fs.getPdfFontName());
                element.setAttribute("fore-color", fs.getForeColor());
                element.setAttribute("back-color", fs.getBackColor());
                element.setAttribute("pdf-encoding", fs.getPdfEncoding());
                element.setAttribute("is-pdf-embedded", "" + fs.getIsPdfEmbedded());
                element.setAttribute("css-class", "" + fs.getCssClass());

            root.appendChild(element);
        }
        
        XmlHelper.output(doc, xmlFileName);

    }

    public static void main(String[] args)  throws IOException, SAXException, CitationStyleManagerException{

        FontStylesCollection fsc = FontStylesCollection.loadFromXml("src/main/resources/CitationStyles/Default/FontStyles.xml");
//        fsc.writeToXml("resource/CitationStyles/Default/FontStylesTestOutput.xml");

        // toString methods made...
//        System.out.println(fsc);

//        fsc.writeToXml("FontStylesOut2.xml");

        FontStylesCollection fscclone = (FontStylesCollection)fsc.clone();

//        fscclone.writeToXml("FontStylesOut3.xml");

        System.out.println("Source: -->" + fsc);

        fscclone.fontStyles.get(0).setFontName("Name in clone!!!");

        System.out.println("Source after cloning:" + fsc);

        System.out.println("Clone: -->" + fscclone);



    }

}
