package org.fao.oa.ingestion.faodoc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.fao.oa.ingestion.utils.IngestionProperties;
import org.mulgara.connection.ConnectionException;
import org.mulgara.connection.ConnectionFactory;
import org.mulgara.connection.JenaConnection;
import org.mulgara.connection.SessionConnection;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;

/**
 * utolity class to extract URIs and labels from the ag_skos_080422.rdf file.
 * requires a running instance of the Mulgara Triple Store server.
 * @author Wilhelm Frank (MPDL)
 *
 */
public class AgrovocSkos
{
    public final String AGROVOC_RDF = IngestionProperties.get("fao.agrovoc.skos");
    public final String SKOS_NS = "http://www.w3.org/2004/02/skos/core#";
    public final String SKOS_PREFLABEL = "<http://www.w3.org/2004/02/skos/core#prefLabel>";
    Model model;
    
    /**
     * public constructor.
     * connects to a running instance of the Mulgara Triple Store server
     * and establishes a JenaConnection to the agrovoc model.
     */
    public AgrovocSkos()
    {
        // create the mulgara connection factory
        ConnectionFactory factory = new ConnectionFactory();
        URI hostUri;
        SessionConnection sessconn = null;
        JenaConnection jenaconn = null;
        try
        {
            hostUri = new URI("rmi://localhost/server1");
            sessconn = (SessionConnection)factory.newConnection(hostUri);
            jenaconn = sessconn.getJenaConnection();
            model = jenaconn.connectModel(hostUri.toString() + "#agrovoc");
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        catch (ConnectionException e)
        {
            e.printStackTrace();
        }
        /*
        finally
        {
            try
            {
                sessconn.close();
                factory.closeAll();
            }
            catch (QueryException e)
            {
                e.printStackTrace();
            }
        }
        */

        //String server = "rmi://localhost/server1" ;
        //String graphURI = server+"#agrovoc" ;
              
        //model = JenaMulgara.connectModel(server, graphURI) ;
        
        //model = ModelFactory.createDefaultModel();
        //model.read(AGROVOC_RDF);
        
        RDFReader reader = model.getReader();
        reader.setProperty("error-mode", "lax");
        reader.setProperty("WARN_STRING_NOT_NORMAL_FORM_C", "EM_IGNORE");
        reader.setProperty("WARN_REDEFINITION_OF_ID","EM_IGNORE");
        //reader.read(model, AGROVOC_RDF);
        //reader.read(model, "rmi://frank02/server1#agrovoc");
        
    }
    
    /**
     * utility method to search a given label.
     * @param label {@link String}
     * @return {@link String}
     */
    public String search(String label)
    {
        String uri = null;
        Property skosLabel = new PropertyImpl(SKOS_NS, "prefLabel");
        ResIterator iter = model.listResourcesWithProperty(skosLabel, label + "@en");
        while (iter.hasNext())
        {
            Resource res = iter.nextResource();
            uri = res.getURI();
        }
        return uri;
    }
    
    /**
     * utility method to get the URI for a given label.
     * @param label {@link String}
     * @return {@link String}
     */
    public String getURI(String label)
    {
        String resURI = null;

        String qry = "SELECT ?S WHERE {?S <http://www.w3.org/2004/02/skos/core#prefLabel> \"" + label + "\"@en}";
        Query q = QueryFactory.create(qry);
        QueryExecution qexec = QueryExecutionFactory.create(q, model);
        ResultSet results = qexec.execSelect();
        while (results.hasNext())
        {
            QuerySolution sol = results.nextSolution();
            if (sol.get("?S") != null)
            {
                resURI = sol.get("?S").toString();
            }
        }
        qexec.close();
        return resURI;
    }
    
    /**
     * utility method to get all labels for a given URI.
     * @param uri {@link String}
     * @return {@link ArrayList} of labels.
     */
    public ArrayList<String> getLabels(String uri)
    {
        Resource res = model.getResource(uri);
        Property skos_label = new PropertyImpl(SKOS_NS, "prefLabel");
        StmtIterator iter = res.listProperties(skos_label);
        ArrayList<String> labels = new ArrayList<String>();

        while (iter.hasNext())
        {
            Statement stmt = iter.nextStatement();
            if (stmt.getLanguage().equalsIgnoreCase("en")
                    || stmt.getLanguage().equalsIgnoreCase("fr")
                    || stmt.getLanguage().equalsIgnoreCase("es")
                    || stmt.getLanguage().equalsIgnoreCase("ru")
                    || stmt.getLanguage().equalsIgnoreCase("zh")
                    || stmt.getLanguage().equalsIgnoreCase("ar")
                    )
            labels.add(stmt.getLiteral().getString() + "=" + stmt.getLanguage());
        }
        return labels;
    }
}
