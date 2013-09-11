import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.izforge.izpack.installer.AutomatedInstallData;

public class JDKPathValidator extends AbstractValidator {

    /**
     * Validates the JAVA_HOME to be set to a JDK of version 1.6.x.
     * 
     * This validation will only return a warning if the JAVA_HOME is not valid, since changing the JAVA_HOME at runtime
     * will not notify the current running JVM to change its properties.
     */
    @Override
    public Status validateData(final AutomatedInstallData data) {

        final boolean isJavaHome = new Boolean(data.getVariable("JdkUseJavaHome")).booleanValue();
        String javaHome = null;

        if (isJavaHome) {
            javaHome = System.getenv("JAVA_HOME");
        }
        else {
            javaHome = data.getVariable("JdkHome");
        }

        if (javaHome == null) {
            return buildMessage("Please select a JDK home directory to continue.", null, isJavaHome);
        }

        final File path = new File(javaHome);

        if (!path.exists()) {
            return buildMessage("The selected JDK does not point to an existing directory.", path, isJavaHome);
        }
        if (!path.isDirectory()) {
            return buildMessage("The selected JDK does not point to a directory.", path, isJavaHome);
        }

        // check if JDK/lib/tools.jar exists
        final File toolsJar =
            new File(path.getAbsolutePath() + File.separatorChar + "lib" + File.separatorChar + "tools.jar");
        if (!toolsJar.isFile()) {
            return buildMessage("The selected JDK does not point to a JDK.", path, isJavaHome);
        }

        // check version by extracting and reading the MANIFEST file in tools.jar
        try {
            final JarFile jar = new JarFile(toolsJar);
            final Manifest manifest = jar.getManifest();
            final String createdBy = manifest.getMainAttributes().getValue("Created-By");

            if (createdBy == null) {
                return buildMessage("Unable to retrieve the version of the JDK. Validation cannot be performed.", null,
                    isJavaHome);
            }

            final String version = createdBy.trim().split("\\s")[0];
            final String[] versionNumbers = version.split("\\.");
            if (versionNumbers.length < 2) {
                return buildMessage(
                    "The retrieved version of the JDK seems to be invalid. Validation cannot be performed.", null,
                    isJavaHome);
            }

            if (!(versionNumbers[0].equals("1") && versionNumbers[1].equals("6"))) {
                return buildMessage("Invalid JDK version.", null, isJavaHome);
            }
        }
        catch (final IOException e) {
            return buildMessage("Unable to read the MANIFEST of the JDK. Validation cannot be performed.", null,
                isJavaHome);
        }

        return Status.OK;
    }

    private Status buildMessage(final String message, final File file, final boolean isJavaHome) {
        if (isJavaHome) {
            if (file == null) {
                buildWarningMessage(message);
            }
            else {
                buildWarningMessage(message, file);
            }
            /*
             * If the JAVA_HOME value is invalid, we cannot expect the user to stop the installer, change the system
             * settings and restart the installer just for this.
             */
            return Status.WARNING;
        }
        else {
            buildErrorMessage(message);
            return Status.ERROR;
        }
    }

    private void buildErrorMessage(final String message) {
        clearErrorMessage();
        errorMessage.append(message);
    }

    private void buildWarningMessage(final String message) {
        clearWarningMessage();
        warningMessage.append(message);
        warningMessage.append("\n\nPlease ensure to change your JAVA_HOME setting to a JDK of version 1.6.x.");
    }

    private void buildWarningMessage(final String message, final File file) {
        clearWarningMessage();
        warningMessage.append(message);
        warningMessage.append("\n(");
        warningMessage.append(file.getAbsolutePath());
        warningMessage.append(")");
        warningMessage.append("\n\nPlease ensure to change your JAVA_HOME setting to a JDK of version 1.6.x.");
    }
}