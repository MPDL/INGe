package de.mpg.escidoc.pubman.installer.panels;

import javax.swing.JTextArea;

import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.AutomatedInstaller;
import com.izforge.izpack.installer.GUIInstaller;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.Installer;
import com.izforge.izpack.installer.InstallerBase;

/**
 * 
 * An implementation of the IConfigurationCreatorPanel interface just for JUnit testing
 *
 * @author sieders (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class JUnitConfigurationPanel implements IConfigurationCreatorPanel
{
    @Override
    public void processFinishedSuccessfully(String text, String threadName)
    {
        System.out.println("Success Success Success Success Success Success Success Success ");
    }

    @Override
    public void processFinishedWithError(String text, Exception e, String threadName)
    {
        System.out.println("Error Error Error");
    }

    @Override
    public JTextArea getTextArea()
    {
        return new JTextArea();
    }

    @Override
    public String getInstallPath()
    {
        return "c:/escidoc.pubman";
    }

    @Override
    public String getInstanceUrl()
    {
        return "http://localhost:8080";
    }

    @Override
    public InstallData getInstallData()
    {
        GUIInstaller installer = null;
        try
        {
            installer = new GUIInstaller();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        AutomatedInstallData installData = InstallData.getInstance();

        return (InstallData)installData;
    }
}
