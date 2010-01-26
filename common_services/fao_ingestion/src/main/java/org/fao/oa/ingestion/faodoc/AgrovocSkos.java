package org.fao.oa.ingestion.faodoc;

import java.util.ArrayList;

import org.fao.oa.ingestion.utils.IngestionProperties;
import org.mulgara.jena.JenaMulgara;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.arp.ARPErrorNumbers;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.util.FileManager;

public class AgrovocSkos
{
    public final String AGROVOC_RDF = IngestionProperties.get("fao.agrovoc.skos");
    public final String SKOS_NS = "http://www.w3.org/2004/02/skos/core#";
    public final String SKOS_PREFLABEL = "<http://www.w3.org/2004/02/skos/core#prefLabel>";
    Model model;
    
    @SuppressWarnings("deprecation")
    public AgrovocSkos()
    {
        String server = "rmi://localhost/server1" ;
        String graphURI = server+"#agrovoc" ;
              
        model = JenaMulgara.connectModel(server, graphURI) ;
        
        //model = ModelFactory.createDefaultModel();
        //model.read(AGROVOC_RDF);
        
        RDFReader reader = model.getReader();
        reader.setProperty("error-mode", "lax");
        reader.setProperty("WARN_STRING_NOT_NORMAL_FORM_C", "EM_IGNORE");
        reader.setProperty("WARN_REDEFINITION_OF_ID","EM_IGNORE");
        //reader.read(model, AGROVOC_RDF);
        //reader.read(model, "rmi://frank02/server1#agrovoc");
        
    }
    
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
    
    public String getURI(String label)
    {
        String resURI = null;
        // String qry = "SELECT ?S ?P ?O WHERE {?S <http://www.w3.org/2004/02/skos/core#prefLabel> \"" + label + "\"@en}";
        //String qry = "SELECT ?S ?P ?O WHERE {?S <http://www.w3.org/2004/02/skos/core#prefLabel> ?O FILTER regex(str(?O), \"^"+ label +"$\", \"i\")}";

        String qry = "SELECT ?S WHERE {?S <http://www.w3.org/2004/02/skos/core#prefLabel> \"" + label + "\"@en}";
        System.out.println(qry);
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
