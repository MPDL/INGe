package de.mpg.escidoc.services.validation.init;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAXParser handler to retrieve the identity process instruction out of the validation schema files.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 131 $ $LastChangedDate: 2007-11-21 18:53:43 +0100 (Wed, 21 Nov 2007) $ *
 */
public class IdentityHandler extends DefaultHandler
{
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(IdentityHandler.class);

    private String contentModel = null;
    private String context = null;
    private String contextName = null;
    private String metadataVersion = null;
    private String version = null;

    private boolean identified = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void processingInstruction(final String target, final String content) throws SAXException
    {
        LOGGER.debug("target: " + target);
        LOGGER.debug("content: " + content);
        if ("identification".equals(target))
        {
            // Context-Name
            Pattern pattern = Pattern.compile("context-name=\"([^\"]*)\"");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find())
            {
                contextName = matcher.group(1);
            }

            // Content-Model
            pattern = Pattern.compile("content-model=\"([^\"]*)\"");
            matcher = pattern.matcher(content);
            if (matcher.find())
            {
                contentModel = matcher.group(1);
            }

            // Context
            pattern = Pattern.compile("context=\"([^\"]*)\"");
            matcher = pattern.matcher(content);
            if (matcher.find())
            {
                context = matcher.group(1);
            }

            // Metadata-Version
            pattern = Pattern.compile("metadata-version=\"([^\"]*)\"");
            matcher = pattern.matcher(content);
            if (matcher.find())
            {
                metadataVersion = matcher.group(1);
            }

            // Version
            pattern = Pattern.compile("version=\"([^\"]*)\"");
            matcher = pattern.matcher(content);
            if (matcher.find())
            {
                version = matcher.group(1);
            }

            identified = true;
        }
    }

    public final String getContentModel()
    {
        return contentModel;
    }

    public final void setContentModel(final String contentModel)
    {
        this.contentModel = contentModel;
    }

    public final String getContext()
    {
        return context;
    }

    public final void setContext(final String context)
    {
        this.context = context;
    }

    public final String getMetadataVersion()
    {
        return metadataVersion;
    }

    public final void setMetadataVersion(final String metadataVersion)
    {
        this.metadataVersion = metadataVersion;
    }

    public final String getVersion()
    {
        return version;
    }

    public final void setVersion(final String version)
    {
        this.version = version;
    }

    public final boolean isIdentified()
    {
        return identified;
    }

    public final void setIdentified(final boolean identified)
    {
        this.identified = identified;
    }

    public final String getContextName()
    {
        return contextName;
    }

    public final void setContextName(final String contextName)
    {
        this.contextName = contextName;
    }

}
