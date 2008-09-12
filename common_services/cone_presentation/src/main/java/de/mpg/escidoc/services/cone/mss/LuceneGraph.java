package de.mpg.escidoc.services.cone.mss;


import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Triple;
import org.mulgara.query.QueryException;
import org.mulgara.server.JRDFSession;
import org.mulgara.server.Session;
import org.mulgara.server.SessionFactory;
import org.mulgara.server.driver.JRDFGraphFactory;
import org.mulgara.server.driver.SessionFactoryFinder;

import de.mpg.escidoc.services.framework.PropertyReader;

public class LuceneGraph {

    private static GraphElementFactory geFactory;
    private static URI serverURI;
    private static URI textModelURI;
    private final String s = "urn:issn:2223-2223";
    private final String p = "http://purl.org/dc/elements/1.1/subject";
    private final String o = "Web Development";

/*    public void testLocalLuceneModel() throws Exception {
        serverURI = new URI("local://localhost/server1");
        textModelURI = new URI("local://localhost/server1#text");

        LocalSessionFactory factory = (LocalSessionFactory)SessionFactoryFinder.newSessionFactory(serverURI, false);
        LocalJRDFSession session = (LocalJRDFDatabaseSession)factory.newJRDFSession();

        Graph graph = new JRDFGraph(session, textModelURI);
        geFactory = graph.getElementFactory();

        Set<Triple>triples = getTriples();
        query(session, triples);
        delete(session, triples);
    }*/

    public static void main(String...strings) throws Exception {
        
        String mulgaraServer = PropertyReader.getProperty("escidoc.cone.mulgara.server.name");
        String mulgaraPort = PropertyReader.getProperty("escidoc.cone.mulgara.server.port");

        serverURI = new URI("rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone");
        textModelURI = new URI("rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone#jnar_subject");
        LuceneGraph lg = new LuceneGraph();

        SessionFactory factory = SessionFactoryFinder.newSessionFactory(serverURI, true);
        JRDFSession session = (JRDFSession) factory.newJRDFSession();

        Graph graph = JRDFGraphFactory.newClientGraph(session, textModelURI);
        geFactory = graph.getElementFactory();

        Set<Triple>triples = lg.getTriples();
        //query(session, triples);
        for (Triple t : triples)
        {
            System.out.println(t.getSubject().toString());
            System.out.println(t.getPredicate().toString());
            System.out.println(t.getObject().toString());
            //graph.find(t);
        }
        lg.delete(session, triples);
    }

    private void query(Session session, Set<Triple>triples) throws Exception {
        //session.createModel(textModelURI, new URI(Mulgara.NAMESPACE+ "LuceneModel"));
    }

    private void delete(Session session, Set<Triple>triples) {
        try {
            session.delete(textModelURI, triples);
        } catch (QueryException e) {
            e.printStackTrace();
        }
    }

    private Set<Triple> getTriples() throws Exception {
        Triple triple = geFactory.createTriple(
                geFactory.createResource(new URI(s)),
                geFactory.createResource(new URI(p)),
                geFactory.createLiteral(o));
        Set<Triple> triples = new HashSet<Triple>();
        triples.add(triple);
        return triples;
    }
}
