package de.mpg.mpdl.migration.foxml;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;

public class ESCIDOCPROPERTIES extends Object
{
    // URIs for vocabulary elements
    protected static final String propUri = "http://escidoc.de/core/01/properties/";
    protected static final String srelUri = "http://escidoc.de/core/01/structural-relations/";
    protected static final String versionUri = "http://escidoc.de/core/01/properties/version/";
    protected static final String releaseUri = "http://escidoc.de/core/01/properties/release/";

    // Return properties URI for vocabulary elements
    public static String getPropertiesURI()
    {
        return propUri;
    }
    
    // Return structural-relations URI for vocabulary elements
    public static String getStructuralRelationsURI()
    {
        return srelUri;
    }
    
    // Return version URI for vocabulary elements
    public static String getVersionURI()
    {
        return versionUri;
    }
    
    // Return release URI for vocabulary elements
    public static String getReleaseURI()
    {
        return releaseUri;
    }

    // Define the property labels and objects
    static final String slocatorurl = "locator-url";
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
            locatorurl = new PropertyImpl(propUri, slocatorurl);
            name = new PropertyImpl(propUri, sname);
            description = new PropertyImpl(propUri, sdescription);
            loginname = new PropertyImpl(propUri, sloginname);
            email = new PropertyImpl(propUri, semail);
            creationdate = new PropertyImpl(propUri, screationdate);
            publicstatus = new PropertyImpl(propUri, spublicstatus);
            publicstatuscomment = new PropertyImpl(propUri, spublicstatuscomment);
            active = new PropertyImpl(propUri, sactive);
            visibility = new PropertyImpl(propUri, svisibility);
            validstatus = new PropertyImpl(propUri, svalidstatus);
            type = new PropertyImpl(propUri, stype);
            haschildren = new PropertyImpl(propUri, shaschildren);
            lockdate = new PropertyImpl(propUri, slockdate);
            lockstatus = new PropertyImpl(propUri, slockstatus);
            contentcategory = new PropertyImpl(propUri, scontentcategory);
            filename = new PropertyImpl(propUri, sfilename);
            filesize = new PropertyImpl(propUri, sfilesize);
            mimetype = new PropertyImpl(propUri, smimetype);
            checksum = new PropertyImpl(propUri, schecksum);
            checksumalgorithm = new PropertyImpl(propUri, schecksumalgorithm);
            pid = new PropertyImpl(propUri, spid);
            contentmodelspecific = new PropertyImpl(propUri, scontentmodelspecific);
            version = new PropertyImpl(propUri, sversion);
            latestversion = new PropertyImpl(propUri, slatestversion);
            latestrelease = new PropertyImpl(propUri, slatestrelease);
            contexttitle = new PropertyImpl(propUri, scontexttitle);
            contentmodeltitle = new PropertyImpl(propUri, scontentmodeltitle);
            createdbytitle = new PropertyImpl(propUri, screatedbytitle);
            modifiedbytitle = new PropertyImpl(propUri, smodifiedbytitle);
            grantremark = new PropertyImpl(propUri, sgrantremark);
            revocationremark = new PropertyImpl(propUri, srevocationremark);
            revocationdate = new PropertyImpl(propUri, srevocationdate);
            externalids = new PropertyImpl(propUri, sexternalids);
            organizationalunits = new PropertyImpl(propUri, sorganizationalunits);
            affiliations = new PropertyImpl(propUri, saffiliations);
            versionnumber = new PropertyImpl(versionUri, sversionnumber);
            versiondate = new PropertyImpl(versionUri, sversiondate);
            versionstatus = new PropertyImpl(versionUri, sversionstatus);
            versionmodifiedby = new PropertyImpl(srelUri, sversionmodifiedby);
            versioncomment = new PropertyImpl(versionUri, sversioncomment);
            versionpid = new PropertyImpl(versionUri, sversionpid);
            releasenumber = new PropertyImpl(releaseUri, sreleasenumber);
            releasedate = new PropertyImpl(releaseUri, sreleasedate);
            releasepid = new PropertyImpl(releaseUri, sreleasepid);
            createdby = new PropertyImpl(srelUri, screatedby);
            modifiedby = new PropertyImpl(srelUri, smodifiedby);
            revokedby = new PropertyImpl(srelUri, srevokedby);
            grantedto = new PropertyImpl(srelUri, sgrantedto);
            context = new PropertyImpl(srelUri, scontext);
            contentmodel = new PropertyImpl(srelUri, scontentmodel);
            lockowner = new PropertyImpl(srelUri, slockowner);
            organizationalunit = new PropertyImpl(srelUri, sorganizationalunit);
            affiliation = new PropertyImpl(srelUri, saffiliation);
            person = new PropertyImpl(srelUri, sperson);
            role = new PropertyImpl(srelUri, srole);
            assignedon = new PropertyImpl(srelUri, sassignedon);
            parent = new PropertyImpl(srelUri, sparent);
            child = new PropertyImpl(srelUri, schild);
            predecessor = new PropertyImpl(srelUri, spredecessor);
            member = new PropertyImpl(srelUri, smember);
            item = new PropertyImpl(srelUri, sitem);
            container = new PropertyImpl(srelUri, scontainer);
            component = new PropertyImpl(srelUri, scomponent);

            
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }
}
