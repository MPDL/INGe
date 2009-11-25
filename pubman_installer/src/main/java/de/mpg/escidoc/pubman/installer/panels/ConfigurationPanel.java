package de.mpg.escidoc.pubman.installer.panels;

import java.awt.LayoutManager2;

import javax.swing.JTextArea;

import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;

public class ConfigurationPanel extends IzPanel
{

	public ConfigurationPanel(InstallerFrame arg0, InstallData arg1,
			LayoutManager2 arg2) {
		super(arg0, arg1, arg2);
	}

	public JTextArea textArea;
	boolean isValid = false;
	
	

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	
	
}
