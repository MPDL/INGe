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
    static final String sname = "name";
    public static Property name = null;
    static final String sdescription = "description";
    public static Resource description = null;
    static final String sloginname = "login-name";
    public static Property loginname = null;
    static final String semail = "email";
    public static Property email = null;
    static final String screationdate = "creation-date";
    public static Property creationdate = null;
    static final String spublicstatus = "public-status";
    public static Property publicstatus = null;
    static final String spublicstatuscomment = "public-status-comment";
    public static Property publicstatuscomment = null;
    static final String sactive = "active";
    public static Property active = null;
    static final String svisibility = "visibility";
    public static Property visibility = null;
    static final String svalidstatus = "valid-status";
    public static Property validstatus = null;
    static final String stype = "type";
    public static Property type = null;
    static final String shaschildren = "has-children";
    public static Property haschildren = null;
    static final String slockdate = "lock-date";
    public static Property lockdate = null;
    static final String slockstatus = "lock-status";
    public static Property lockstatus = null;
    static final String scontentcategory = "content-category";
    public static Property contentcategory = null;
    static final String sfilename = "file-name";
    public static Property filename = null;
    static final String sfilesize = "file-size";
    public static Property filesize = null;
    static final String smimetype = "mime-type";
    public static Property mimetype = null;
    static final String schecksum = "checksum";
    public static Property checksum = null;
    static final String schecksumalgorithm = "checksum-algorithm";
    public static Property checksumalgorithm = null;
    static final String spid = "pid";
    public static Property pid = null;
    static final String scontentmodelspecific = "content-model-specific";
    public static Property contentmodelspecific = null;
    static final String sversion = "version";
    public static Property version = null;
    static final String sversionnumber = "number";
    public static Property versionnumber = null;
    static final String sversiondate = "date";
    public static Property versiondate = null;
    static final String sversionstatus = "status";
    public static Property versionstatus = null;
    static final String sversionmodifiedby = "modified-by";
    public static Property versionmodifiedby = null;
    static final String sversioncomment = "comment";
    public static Property versioncomment = null;
    static final String sversionpid = "pid";
    public static Property versionpid = null;
    static final String slatestversion = "latest-version";
    public static Property latestversion = null;
    static final String slatestrelease = "latest-release";
    public static Property latestrelease = null;
    static final String sreleasenumber = "number";
    public static Property releasenumber = null;
    static final String sreleasedate = "date";
    public static Property releasedate = null;
    static final String sreleasepid = "pid";
    public static Property releasepid = null;
    static final String scontexttitle = "context-title";
    public static Property contexttitle = null;
    static final String scontentmodeltitle = "content-model-title";
    public static Property contentmodeltitle = null;
    static final String smodifiedbytitle = "modified-by-title";
    public static Property modifiedbytitle = null;
    static final String screatedbytitle = "created-by-title";
    public static Property createdbytitle = null;
    static final String sgrantremark = "grant-remark";
    public static Property grantremark = null;
    static final String srevocationremark = "revocation-remark";
    public static Property revocationremark = null;
    static final String srevocationdate = "revocation-date";
    public static Property revocationdate = null;
    static final String sexternalids = "external-ids";
    public static Property externalids = null;
    static final String sorganizationalunits = "otganizational-units";
    public static Property organizationalunits = null;
    static final String saffiliations = "affiliations";
    public static Property affiliations = null;
    static final String screatedby = "created-by";
    public static Property createdby = null;
    static final String smodifiedby = "modified-by";
    public static Property modifiedby = null;
    static final String srevokedby = "revoked-by";
    public static Property revokedby = null;
    static final String sgrantedto = "granted-to";
    public static Property grantedto = null;
    static final String scontext = "context";
    public static Property context = null;
    static final String scontentmodel = "content-model";
    public static Property contentmodel = null;
    static final String scomponent = "component";
    public static Property component = null;
    static final String slockowner = "lock-owner";
    public static Property lockowner = null;
    static final String sorganizationalunit = "otganizational-unit";
    public static Property organizationalunit = null;
    static final String saffiliation = "affiliation";
    public static Property affiliation = null;
    static final String sperson = "person";
    public static Property person = null;
    static final String srole = "role";
    public static Property role = null;
    static final String sassignedon = "assigned-on";
    public static Property assignedon = null;
    static final String sparent = "parent";
    public static Property parent = null;
    static final String schild = "child";
    public static Property child = null;
    static final String spredecessor = "predecessor";
    public static Property predecessor = null;
    static final String smember = "member";
    public static Property member = null;
    static final String sitem = "item";
    public static Property item = null;
    static final String scontainer = "container";
    public static Property container = null;
    
    // Instantiate the properties and the resource
    static
    {
        try
        {
            // Instantiate the properties
            locatorurl = new PropertyImpl(PROPERTIES_URI, S_LOCATORURL);
            name = new PropertyImpl(PROPERTIES_URI, sname);
            description = new PropertyImpl(PROPERTIES_URI, sdescription);
            loginname = new PropertyImpl(PROPERTIES_URI, sloginname);
            email = new PropertyImpl(PROPERTIES_URI, semail);
            creationdate = new PropertyImpl(PROPERTIES_URI, screationdate);
            publicstatus = new PropertyImpl(PROPERTIES_URI, spublicstatus);
            publicstatuscomment = new PropertyImpl(PROPERTIES_URI, spublicstatuscomment);
            active = new PropertyImpl(PROPERTIES_URI, sactive);
            visibility = new PropertyImpl(PROPERTIES_URI, svisibility);
            validstatus = new PropertyImpl(PROPERTIES_URI, svalidstatus);
            type = new PropertyImpl(PROPERTIES_URI, stype);
            haschildren = new PropertyImpl(PROPERTIES_URI, shaschildren);
            lockdate = new PropertyImpl(PROPERTIES_URI, slockdate);
            lockstatus = new PropertyImpl(PROPERTIES_URI, slockstatus);
            contentcategory = new PropertyImpl(PROPERTIES_URI, scontentcategory);
            filename = new PropertyImpl(PROPERTIES_URI, sfilename);
            filesize = new PropertyImpl(PROPERTIES_URI, sfilesize);
            mimetype = new PropertyImpl(PROPERTIES_URI, smimetype);
            checksum = new PropertyImpl(PROPERTIES_URI, schecksum);
            checksumalgorithm = new PropertyImpl(PROPERTIES_URI, schecksumalgorithm);
            pid = new PropertyImpl(PROPERTIES_URI, spid);
            contentmodelspecific = new PropertyImpl(PROPERTIES_URI, scontentmodelspecific);
            version = new PropertyImpl(PROPERTIES_URI, sversion);
            latestversion = new PropertyImpl(PROPERTIES_URI, slatestversion);
            latestrelease = new PropertyImpl(PROPERTIES_URI, slatestrelease);
            contexttitle = new PropertyImpl(PROPERTIES_URI, scontexttitle);
            contentmodeltitle = new PropertyImpl(PROPERTIES_URI, scontentmodeltitle);
            createdbytitle = new PropertyImpl(PROPERTIES_URI, screatedbytitle);
            modifiedbytitle = new PropertyImpl(PROPERTIES_URI, smodifiedbytitle);
            grantremark = new PropertyImpl(PROPERTIES_URI, sgrantremark);
            revocationremark = new PropertyImpl(PROPERTIES_URI, srevocationremark);
            revocationdate = new PropertyImpl(PROPERTIES_URI, srevocationdate);
            externalids = new PropertyImpl(PROPERTIES_URI, sexternalids);
            organizationalunits = new PropertyImpl(PROPERTIES_URI, sorganizationalunits);
            affiliations = new PropertyImpl(PROPERTIES_URI, saffiliations);
            versionnumber = new PropertyImpl(VERSION_URI, sversionnumber);
            versiondate = new PropertyImpl(VERSION_URI, sversiondate);
            versionstatus = new PropertyImpl(VERSION_URI, sversionstatus);
            versionmodifiedby = new PropertyImpl(STRUCTURAL_RELATIONS_URI, sversionmodifiedby);
            versioncomment = new PropertyImpl(VERSION_URI, sversioncomment);
            versionpid = new PropertyImpl(VERSION_URI, sversionpid);
            releasenumber = new PropertyImpl(RELEASE_URI, sreleasenumber);
            releasedate = new PropertyImpl(RELEASE_URI, sreleasedate);
            releasepid = new PropertyImpl(RELEASE_URI, sreleasepid);
            createdby = new PropertyImpl(STRUCTURAL_RELATIONS_URI, screatedby);
            modifiedby = new PropertyImpl(STRUCTURAL_RELATIONS_URI, smodifiedby);
            revokedby = new PropertyImpl(STRUCTURAL_RELATIONS_URI, srevokedby);
            grantedto = new PropertyImpl(STRUCTURAL_RELATIONS_URI, sgrantedto);
            context = new PropertyImpl(STRUCTURAL_RELATIONS_URI, scontext);
            contentmodel = new PropertyImpl(STRUCTURAL_RELATIONS_URI, scontentmodel);
            lockowner = new PropertyImpl(STRUCTURAL_RELATIONS_URI, slockowner);
            organizationalunit = new PropertyImpl(STRUCTURAL_RELATIONS_URI, sorganizationalunit);
            affiliation = new PropertyImpl(STRUCTURAL_RELATIONS_URI, saffiliation);
            person = new PropertyImpl(STRUCTURAL_RELATIONS_URI, sperson);
            role = new PropertyImpl(STRUCTURAL_RELATIONS_URI, srole);
            assignedon = new PropertyImpl(STRUCTURAL_RELATIONS_URI, sassignedon);
            parent = new PropertyImpl(STRUCTURAL_RELATIONS_URI, sparent);
            child = new PropertyImpl(STRUCTURAL_RELATIONS_URI, schild);
            predecessor = new PropertyImpl(STRUCTURAL_RELATIONS_URI, spredecessor);
            member = new PropertyImpl(STRUCTURAL_RELATIONS_URI, smember);
            item = new PropertyImpl(STRUCTURAL_RELATIONS_URI, sitem);
            container = new PropertyImpl(STRUCTURAL_RELATIONS_URI, scontainer);
            component = new PropertyImpl(STRUCTURAL_RELATIONS_URI, scomponent);

            
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }
}
