package de.mpg.escidoc.pubman.installer.panels;

import javax.swing.JTextArea;

import com.izforge.izpack.installer.InstallData;


public interface IConfigurationCreatorPanel
{

    public abstract void processFinishedSuccessfully(String text, String threadName);

    public abstract void processFinishedWithError(String text, Exception e, String threadName);

    public abstract JTextArea getTextArea();

    public abstract String getInstallPath();

    public abstract String getInstanceUrl();
    
    public abstract InstallData getInstallData();
}