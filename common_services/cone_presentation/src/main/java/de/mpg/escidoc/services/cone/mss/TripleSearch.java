/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.cone.mss;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.util.ClosableIterator;
import org.jrdf.vocabulary.RDF;
import org.mulgara.query.Answer;
import org.mulgara.query.ArrayAnswer;
import org.mulgara.query.ConstraintImpl;
import org.mulgara.query.ModelResource;
import org.mulgara.query.Order;
import org.mulgara.query.Query;
import org.mulgara.query.QueryException;
import org.mulgara.query.SelectElement;
import org.mulgara.query.TuplesException;
import org.mulgara.query.UnconstrainedAnswer;
import org.mulgara.query.Variable;
import org.mulgara.query.rdf.Mulgara;
import org.mulgara.query.rdf.URIReferenceImpl;
import org.mulgara.server.JRDFSession;
import org.mulgara.server.NonRemoteSessionException;
import org.mulgara.server.Session;
import org.mulgara.server.SessionFactory;
import org.mulgara.server.driver.JRDFGraphFactory;
import org.mulgara.server.driver.SessionFactoryFinder;
import org.mulgara.server.driver.SessionFactoryFinderException;

import de.mpg.escidoc.services.framework.PropertyReader;

public class TripleSearch
{
    ResourceBundle mts = ResourceBundle.getBundle("properties.mulgara");
    private static URI sysModel_URI;
    private static URI mulgara_URI;
    private static URI modelType_URI, luceneModelType_URI, fsModelType_URI;
    private static final Logger log = Logger.getLogger(TripleSearch.class);
    static
    {
        try
        {
            
            String mulgaraServer = PropertyReader.getProperty("escidoc.cone.mulgara.server.name");
            String mulgaraPort = PropertyReader.getProperty("escidoc.cone.mulgara.server.port");
            
            mulgara_URI = new URI("rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone");
            sysModel_URI = new URI("rmi://" + mulgaraServer + ":" + mulgaraPort + "/cone#");
            modelType_URI = new URI(Mulgara.NAMESPACE + "Model");
            luceneModelType_URI = new URI(Mulgara.NAMESPACE + "LuceneModel");
            fsModelType_URI = new URI(Mulgara.NAMESPACE + "FileSystemModel");
        }
        catch (URISyntaxException use)
        {
            log.error(use.getMessage(), use);
        }
        catch (IOException ioe)
        {
            log.error(ioe.getMessage(), ioe);
        }
    }

    public List<String[]> getModelList()
    {
        try
        {
            SessionFactory sf = SessionFactoryFinder.newSessionFactory(mulgara_URI, true);
            Session session = sf.newSession();
            try
            {
                ArrayList<String[]> results = new ArrayList<String[]>();
                Variable mulgaraModel = new Variable("model");
                URIReferenceImpl rdftype = new URIReferenceImpl(RDF.TYPE);
                Variable modelType = new Variable("type");
                URIReferenceImpl mulgaraModelType = new URIReferenceImpl(modelType_URI);
                List<SelectElement> select = new ArrayList<SelectElement>(2);
                select.add(mulgaraModel);
                select.add(modelType);
                log.info("selecting " + select.toString() + " from " + sysModel_URI + " where " + mulgaraModel + " "
                        + rdftype + " " + mulgaraModelType);
                Answer answer = new ArrayAnswer(session.query(new Query(select, new ModelResource(sysModel_URI),
                        new ConstraintImpl(mulgaraModel, rdftype, modelType), null, Collections
                                .singletonList(new Order(modelType, false)), null, 0, new UnconstrainedAnswer())));
                log.info("got instance of " + answer.getClass().getName() + " with rowCount: " + answer.getRowCount());
                if (answer.getRowCount() > 0)
                {
                    while (answer.next())
                    {
                        String modelname = answer.getObject(0).toString();
                        String modeltype = answer.getObject(1).toString().substring(27);
                        String allowedAction = null;
                        if (modeltype.equals("Model") && !modelname.contains("?"))
                        {
                            allowedAction = "q i d";
                        }
                        if (modeltype.equals("LuceneModel"))
                        {
                            allowedAction = "i";
                        }
                        if (modeltype.equals("FileSystemModel"))
                        {
                            allowedAction = "q";
                        }
                        String[] model = { modelname, modeltype, allowedAction };
                        results.add(model);
                    }
                    return results;
                }
            }
            catch (TuplesException e)
            {
                e.printStackTrace();
            }
            finally
            {
                session.close();
            }
        }
        catch (SessionFactoryFinderException e)
        {
            e.printStackTrace();
        }
        catch (NonRemoteSessionException e)
        {
            e.printStackTrace();
        }
        catch (QueryException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getNodeList(String selectedModel, String node)
    {
        try
        {
            SessionFactory sf = SessionFactoryFinder.newSessionFactory(mulgara_URI, false);
            Session session = sf.newSession();
            Answer answer = null;
            try
            {
                ArrayList<String> results = new ArrayList<String>();
                Variable sub = new Variable("subject");
                Variable pre = new Variable("predicate");
                Variable obj = new Variable("object");
                List<SelectElement> select = new ArrayList<SelectElement>(1);
                URI selectedModel_URI = new URI(selectedModel);
                if (node.equals("sub"))
                {
                    select.add(sub);
                    answer = new ArrayAnswer(session.query(new Query(select, new ModelResource(selectedModel_URI),
                            new ConstraintImpl(sub, pre, obj), null, Collections.singletonList(new Order(sub, true)),
                            null, 0, new UnconstrainedAnswer())));
                }
                else if (node.equals("pre"))
                {
                    select.add(pre);
                    answer = new ArrayAnswer(session.query(new Query(select, new ModelResource(selectedModel_URI),
                            new ConstraintImpl(sub, pre, obj), null, Collections.singletonList(new Order(pre, true)),
                            null, 0, new UnconstrainedAnswer())));
                }
                else if (node.equals("obj"))
                {
                    select.add(obj);
                    answer = new ArrayAnswer(session.query(new Query(select, new ModelResource(selectedModel_URI),
                            new ConstraintImpl(sub, pre, obj), null, Collections.singletonList(new Order(obj, true)),
                            null, 0, new UnconstrainedAnswer())));
                }
                else
                {
                    log.error("ERROR getting NodeList");
                }
                if (answer.getRowCount() > 0)
                {
                    while (answer.next())
                    {
                        String node2add = answer.getObject(0).toString();
                        results.add(node2add);
                    }
                    return results;
                }
            }
            catch (TuplesException e)
            {
                e.printStackTrace();
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
            finally
            {
                session.close();
            }
        }
        catch (SessionFactoryFinderException e)
        {
            e.printStackTrace();
        }
        catch (NonRemoteSessionException e)
        {
            e.printStackTrace();
        }
        catch (QueryException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public List<Triple> queryGraph(String model, String s, String p, String o)
    {
        try
        {
            SessionFactory sf = SessionFactoryFinder.newSessionFactory(mulgara_URI, false);
            JRDFSession session = (JRDFSession)sf.newJRDFSession();
            try
            {
                URI model_URI = new URI(model);
                Graph g = JRDFGraphFactory.newClientGraph(session, model_URI);
                // log.info("Number of Triples: " + g.getNumberOfTriples());
                GraphElementFactory gef = g.getElementFactory();
                URIReference subject = null;
                URIReference predicate = null;
                //URIReference object = null;
                Literal object = null;
                if (s != null && s != "")
                {
                    subject = gef.createResource(new URI(s));
                }
                if (p != null && p != "")
                {
                    predicate = gef.createResource(new URI(p));
                }
                if (o != null && o != "" )
                {
                    object = gef.createLiteral(o);
                }
                
                Triple triple = null;
                triple = gef.createTriple(subject, predicate, object);
                List<Triple> triples = new ArrayList<Triple>();
                ClosableIterator result = g.find(triple);
                while (result.hasNext())
                {
                    triples.add(((Triple)result.next()));
                    // System.out.println(((Triple)result.next()).toString());
                }
                return triples;
            }
            catch (GraphException e)
            {
                e.printStackTrace();
            }
            catch (GraphElementFactoryException e)
            {
                e.printStackTrace();
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
            finally
            {
                session.close();
            }
        }
        catch (SessionFactoryFinderException e)
        {
            e.printStackTrace();
        }
        catch (NonRemoteSessionException e)
        {
            e.printStackTrace();
        }
        catch (QueryException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void updateTriple(int i, String m, String s, String p, String o)
    {
        try
        {
            SessionFactory sf = SessionFactoryFinder.newSessionFactory(mulgara_URI, false);
            JRDFSession session = (JRDFSession)sf.newJRDFSession();
            try
            {
                URI model_URI = new URI(m);
                Graph g = JRDFGraphFactory.newClientGraph(session, model_URI);
                GraphElementFactory gef = g.getElementFactory();
                Triple t = gef.createTriple(gef.createResource(new URI(s)), gef.createResource(new URI(p)), gef
                        .createLiteral(o));
                Set<Triple> triples = new HashSet<Triple>();
                triples.add(t);
                switch (i)
                {
                    case 0:
                        session.delete(model_URI, triples);
                        break;
                    case 1:
                        session.insert(model_URI, triples);
                        break;
                }
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
            catch (GraphException e)
            {
                e.printStackTrace();
            }
            catch (GraphElementFactoryException e)
            {
                e.printStackTrace();
            }
            finally
            {
                session.close();
            }
        }
        catch (SessionFactoryFinderException e)
        {
            e.printStackTrace();
        }
        catch (NonRemoteSessionException e)
        {
            e.printStackTrace();
        }
        catch (QueryException e)
        {
            e.printStackTrace();
        }
        finally
        {
        }
    }

    public void updateModel(int i, String m, String t, String f)
    {
        try
        {
            SessionFactory sf = SessionFactoryFinder.newSessionFactory(mulgara_URI, false);
            Session session = (Session)sf.newSession();
            URI model_URI = null, type_URI = null, file_URI = null;
            try
            {
                model_URI = new URI(m);
                if (t != null)
                {
                    type_URI = new URI(t);
                }
                if (f != null)
                {
                    file_URI = new URI(f);
                }
                switch (i)
                {
                    case 0:
                        session.removeModel(model_URI);
                        break;
                    case 1:
                        session.createModel(model_URI, type_URI);
                        break;
                    case 2:
                        //session.backup(model_URI, file_URI);
                        session.backup(file_URI);
                        break;
                    case 3:
                        //session.restore(model_URI, file_URI);
                        session.restore(file_URI);
                        break;
                    case 4:
                        session.setModel(model_URI, new ModelResource(file_URI));
                }
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
            finally
            {
            }
        }
        catch (SessionFactoryFinderException e)
        {
            e.printStackTrace();
        }
        catch (NonRemoteSessionException e)
        {
            e.printStackTrace();
        }
        catch (QueryException e)
        {
            e.printStackTrace();
        }
        finally
        {
        }
    }
}
