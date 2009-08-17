package de.mpg.mpdl.migration.foxml;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;

/**
 * 
 * TODO Description.
 *
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ESCIDOCPROPERTIES extends Object
{
    // URIs for vocabulary elements
    protected static final String PROPERTIES_URI = "http://escidoc.de/core/01/properties/";
    protected static final String STRUCTURAL_RELATIONS_URI = "http://escidoc.de/core/01/structural-relations/";
    protected static final String VERSION_URI = "http://escidoc.de/core/01/properties/version/";
    protected static final String RELEASE_URI = "http://escidoc.de/core/01/properties/release/";

    // Return properties URI for vocabulary elements
    public static String getPropertiesURI()
    {
        return PROPERTIES_URI;
    }
    
    // Return structural-relations URI for vocabulary elements
    public static String getStructuralRelationsURI()
    {
        return STRUCTURAL_RELATIONS_URI;
    }
    
    // Return version URI for vocabulary elements
    public static String getVersionURI()
    {
        return VERSION_URI;
    }
    
    // Return release URI for vocabulary elements
    public static String getReleaseURI()
    {
        return RELEASE_URI;
    }

    // Define the property labels and objects
    static final String S_LOCATORURL = "locator-url";
    public static Property locatorurl = null;
    static final String S_NAME = "name";
    public static Property name = null;
    static final String S_DESCRIPTION = "description";
    public static Property description = null;
    static final String S_LOGINNAME = "login-name";
    public static Property loginname = null;
    static final String S_EMAIL = "email";
    public static Property email = null;
    static final String S_CREATIONDATE = "creation-date";
    public static Property creationdate = null;
    static final String S_PUBLICSTATUS = "public-status";
    public static Property publicstatus = null;
    static final String S_PUBLICSTATUSCOMMENT = "public-status-comment";
    public static Property publicstatuscomment = null;
    static final String S_ACTIVE = "active";
    public static Property active = null;
    static final String S_VISIBILITY = "visibility";
    public static Property visibility = null;
    static final String S_VALIDSTATUS = "valid-status";
    public static Property validstatus = null;
    static final String S_TYPE = "type";
    public static Property type = null;
    static final String S_HASCHILDREN = "has-children";
    public static Property haschildren = null;
    static final String S_LOCKDATE = "lock-date";
    public static Property lockdate = null;
    static final String S_LOCKSTATUS = "lock-status";
    public static Property lockstatus = null;
    static final String S_CONTENTCATEGORY = "content-category";
    public static Property contentcategory = null;
    static final String S_FILENAME = "file-name";
    public static Property filename = null;
    static final String S_FILESIZE = "file-size";
    public static Property filesize = null;
    static final String S_MIMETYPE = "mime-type";
    public static Property mimetype = null;
    static final String S_CHECKSUM = "checksum";
    public static Property checksum = null;
    static final String S_CHECKSUMALGORITHM = "checksum-algorithm";
    public static Property checksumalgorithm = null;
    static final String S_PID = "pid";
    public static Property pid = null;
    static final String S_CONTENTMODELSPECIFIC = "content-model-specific";
    public static Property contentmodelspecific = null;
    static final String S_VERSION = "version";
    public static Property version = null;
    static final String S_VERSIONNUMBER = "number";
    public static Property versionnumber = null;
    static final String S_VERSIONDATE = "date";
    public static Property versiondate = null;
    static final String S_VERSIONSTATUS = "status";
    public static Property versionstatus = null;
    static final String S_VERSIONMODIFIEDBY = "modified-by";
    public static Property versionmodifiedby = null;
    static final String S_VERSIONCOMMENT = "comment";
    public static Property versioncomment = null;
    static final String S_VERSIONPID = "pid";
    public static Property versionpid = null;
    static final String S_LATESTVERSION = "latest-version";
    public static Property latestversion = null;
    static final String S_LATESTRELEASE = "latest-release";
    public static Property latestrelease = null;
    static final String S_RELEASENUMBER = "number";
    public static Property releasenumber = null;
    static final String S_RELEASEDATE = "date";
    public static Property releasedate = null;
    static final String S_RELEASEPID = "pid";
    public static Property releasepid = null;
    static final String S_CONTEXTTITLE = "context-title";
    public static Property contexttitle = null;
    static final String S_CONTENTMODELTITLE = "content-model-title";
    public static Property contentmodeltitle = null;
    static final String S_MODIFIEDBYTITLE = "modified-by-title";
    public static Property modifiedbytitle = null;
    static final String S_CREATEDBYTITLE = "created-by-title";
    public static Property createdbytitle = null;
    static final String S_GRANTREMARK = "grant-remark";
    public static Property grantremark = null;
    static final String S_REVOCATIONREMARK = "revocation-remark";
    public static Property revocationremark = null;
    static final String S_REVOCATIONDATE = "revocation-date";
    public static Property revocationdate = null;
    static final String S_EXTERNALIDS = "external-ids";
    public static Property externalids = null;
    static final String S_ORGANIZATIONALUNITS = "otganizational-units";
    public static Property organizationalunits = null;
    static final String S_AFFILIATIONS = "affiliations";
    public static Property affiliations = null;
    static final String S_CREATEDBY = "created-by";
    public static Property createdby = null;
    static final String S_MODIFIEDBY = "modified-by";
    public static Property modifiedby = null;
    static final String S_REVOKEDBY = "revoked-by";
    public static Property revokedby = null;
    static final String S_GRANTEDTO = "granted-to";
    public static Property grantedto = null;
    static final String S_CONTEXT = "context";
    public static Property context = null;
    static final String S_CONTENTMODEL = "content-model";
    public static Property contentmodel = null;
    static final String S_COMPONENT = "component";
    public static Property component = null;
    static final String S_LOCKOWNER = "lock-owner";
    public static Property lockowner = null;
    static final String S_ORGANIZATIONALUNIT = "otganizational-unit";
    public static Property organizationalunit = null;
    static final String S_AFFILIATION = "affiliation";
    public static Property affiliation = null;
    static final String S_PERSON = "person";
    public static Property person = null;
    static final String S_ROLE = "role";
    public static Property role = null;
    static final String S_ASSIGNEDON = "assigned-on";
    public static Property assignedon = null;
    static final String S_PARENT = "parent";
    public static Property parent = null;
    static final String S_CHILD = "child";
    public static Property child = null;
    static final String S_PREDECESSOR = "predecessor";
    public static Property predecessor = null;
    static final String S_MEMBER = "member";
    public static Property member = null;
    static final String S_ITEM = "item";
    public static Property item = null;
    static final String S_CONTAINER = "container";
    public static Property container = null;
    
    // Instantiate the properties and the resource
    static
    {
        try
        {
            // Instantiate the properties
            locatorurl = new PropertyImpl(PROPERTIES_URI, S_LOCATORURL);
            name = new PropertyImpl(PROPERTIES_URI, S_NAME);
            description = new PropertyImpl(PROPERTIES_URI, S_DESCRIPTION);
            loginname = new PropertyImpl(PROPERTIES_URI, S_LOGINNAME);
            email = new PropertyImpl(PROPERTIES_URI, S_EMAIL);
            creationdate = new PropertyImpl(PROPERTIES_URI, S_CREATIONDATE);
            publicstatus = new PropertyImpl(PROPERTIES_URI, S_PUBLICSTATUS);
            publicstatuscomment = new PropertyImpl(PROPERTIES_URI, S_PUBLICSTATUSCOMMENT);
            active = new PropertyImpl(PROPERTIES_URI, S_ACTIVE);
            visibility = new PropertyImpl(PROPERTIES_URI, S_VISIBILITY);
            validstatus = new PropertyImpl(PROPERTIES_URI, S_VALIDSTATUS);
            type = new PropertyImpl(PROPERTIES_URI, S_TYPE);
            haschildren = new PropertyImpl(PROPERTIES_URI, S_HASCHILDREN);
            lockdate = new PropertyImpl(PROPERTIES_URI, S_LOCKDATE);
            lockstatus = new PropertyImpl(PROPERTIES_URI, S_LOCKSTATUS);
            contentcategory = new PropertyImpl(PROPERTIES_URI, S_CONTENTCATEGORY);
            filename = new PropertyImpl(PROPERTIES_URI, S_FILENAME);
            filesize = new PropertyImpl(PROPERTIES_URI, S_FILESIZE);
            mimetype = new PropertyImpl(PROPERTIES_URI, S_MIMETYPE);
            checksum = new PropertyImpl(PROPERTIES_URI, S_CHECKSUM);
            checksumalgorithm = new PropertyImpl(PROPERTIES_URI, S_CHECKSUMALGORITHM);
            pid = new PropertyImpl(PROPERTIES_URI, S_PID);
            contentmodelspecific = new PropertyImpl(PROPERTIES_URI, S_CONTENTMODELSPECIFIC);
            version = new PropertyImpl(PROPERTIES_URI, S_VERSION);
            latestversion = new PropertyImpl(PROPERTIES_URI, S_LATESTVERSION);
            latestrelease = new PropertyImpl(PROPERTIES_URI, S_LATESTRELEASE);
            contexttitle = new PropertyImpl(PROPERTIES_URI, S_CONTEXTTITLE);
            contentmodeltitle = new PropertyImpl(PROPERTIES_URI, S_CONTENTMODELTITLE);
            createdbytitle = new PropertyImpl(PROPERTIES_URI, S_CREATEDBYTITLE);
            modifiedbytitle = new PropertyImpl(PROPERTIES_URI, S_MODIFIEDBYTITLE);
            grantremark = new PropertyImpl(PROPERTIES_URI, S_GRANTREMARK);
            revocationremark = new PropertyImpl(PROPERTIES_URI, S_REVOCATIONREMARK);
            revocationdate = new PropertyImpl(PROPERTIES_URI, S_REVOCATIONDATE);
            externalids = new PropertyImpl(PROPERTIES_URI, S_EXTERNALIDS);
            organizationalunits = new PropertyImpl(PROPERTIES_URI, S_ORGANIZATIONALUNITS);
            affiliations = new PropertyImpl(PROPERTIES_URI, S_AFFILIATIONS);
            versionnumber = new PropertyImpl(VERSION_URI, S_VERSIONNUMBER);
            versiondate = new PropertyImpl(VERSION_URI, S_VERSIONDATE);
            versionstatus = new PropertyImpl(VERSION_URI, S_VERSIONSTATUS);
            versionmodifiedby = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_VERSIONMODIFIEDBY);
            versioncomment = new PropertyImpl(VERSION_URI, S_VERSIONCOMMENT);
            versionpid = new PropertyImpl(VERSION_URI, S_VERSIONPID);
            releasenumber = new PropertyImpl(RELEASE_URI, S_RELEASENUMBER);
            releasedate = new PropertyImpl(RELEASE_URI, S_RELEASEDATE);
            releasepid = new PropertyImpl(RELEASE_URI, S_RELEASEPID);
            createdby = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_CREATEDBY);
            modifiedby = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_MODIFIEDBY);
            revokedby = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_REVOKEDBY);
            grantedto = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_GRANTEDTO);
            context = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_CONTEXT);
            contentmodel = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_CONTENTMODEL);
            lockowner = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_LOCKOWNER);
            organizationalunit = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_ORGANIZATIONALUNIT);
            affiliation = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_AFFILIATION);
            person = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_PERSON);
            role = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_ROLE);
            assignedon = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_ASSIGNEDON);
            parent = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_PARENT);
            child = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_CHILD);
            predecessor = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_PREDECESSOR);
            member = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_MEMBER);
            item = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_ITEM);
            container = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_CONTAINER);
            component = new PropertyImpl(STRUCTURAL_RELATIONS_URI, S_COMPONENT);

            
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }
}
