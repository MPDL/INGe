package org.fao.oa.ingestion.faodoc;

import com.hp.hpl.jena.graph.Node_Literal;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.sparql.util.NodeFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

public class LanguageCodes
{
    public final String ISO639_RDF_URI = "file:///home/frank/data/AFDB/639-core.rdf";
    public final String ISO639_NS = "http://psi.oasis-open.org/iso/639/#";

    public String[] getIso639Codes(String lang)
    {
        String resURI = null;
        Model model = ModelFactory.createDefaultModel();
        model.read(ISO639_RDF_URI);
        String qry = "SELECT ?S ?P ?O WHERE {?S <http://www.w3.org/2000/01/rdf-schema#label> \"" + lang + "\"@en}";
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
        // ResultSetFormatter.out(System.out, results, q);
        qexec.close();
        Resource res = model.getResource(resURI);
        Property code_a2 = new PropertyImpl(ISO639_NS, "code-a2");
        Property code_a3b = new PropertyImpl(ISO639_NS, "code-a3b");
        if (res.getProperty(code_a2) != null && res.getProperty(code_a3b) != null)
        {
            String a2 = res.getProperty(code_a2).getObject().toString();
            String a3b = res.getProperty(code_a3b).getObject().toString();
            return new String[] { a2, a3b, lang };
        }
        return null;
    }

    public String[] getIso639Codes2(String lang)
    {
        String resURI = null;
        String label = null;
        Model model = ModelFactory.createDefaultModel();
        model.read(ISO639_RDF_URI);
        String qry = "SELECT ?S ?P ?O WHERE {?S <" + ISO639_NS + "code-a2> \"" + lang + "\"}";
        Query q = QueryFactory.create(qry);
        QueryExecution qexec = QueryExecutionFactory.create(q, model);
        ResultSet results = qexec.execSelect();
        if (results.hasNext())
        {
            while (results.hasNext())
            {
                QuerySolution sol = results.nextSolution();
                if (sol.get("?S") != null)
                {
                    resURI = sol.get("?S").toString();
                }
            }
        }
        else
        {
            System.out.println("NO resource");
        }
        qexec.close();
        Resource res = model.getResource(resURI);
        Property code_a3b = new PropertyImpl(ISO639_NS, "code-a3b");
        StmtIterator propIter = res.listProperties(RDFS.label);
        while (propIter.hasNext())
        {
            Statement stmt = propIter.nextStatement();
            if (stmt.getLanguage().equalsIgnoreCase("en"))
            {
                String tmp = stmt.getObject().toString();
                label = tmp.substring(0, tmp.length() -3);
            }
        }
        if (res.getProperty(code_a3b) != null)
        {
            String a3b = res.getProperty(code_a3b).getObject().toString();
            return new String[] { lang, a3b, label };
        }
        return null;
    }
}
