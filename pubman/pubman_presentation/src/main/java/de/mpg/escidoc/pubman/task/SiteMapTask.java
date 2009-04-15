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

package de.mpg.escidoc.pubman.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.xb.ltgfmt.TestCase.Files;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.TopLevelAffiliationFilter;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.search.query.OrgUnitsSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.SearchQuery;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class SiteMapTask extends Thread
{
    private static final Logger logger = Logger.getLogger(SiteMapTask.class);
    
    private Search search;
    private ArrayList<String> contentModels;
    private FileWriter fileWriter = null;
    private String instanceUrl;
    private String contextPath;
    private String itemPattern;
    private SimpleDateFormat dateFormat;
    private String contentModel;
    
    private int interval;
    
    private int maxItemsPerFile;
    private int maxItemsPerRetrieve;
    
    private int retrievalTimeout;

    private boolean signal = false;
    
    private List<File> files = new ArrayList<File>();
    
    /**
     * {@inheritDoc}
     */
    public void run()
    {
        
        try
        {
            
            logger.info("Starting to create Sitemap.");
            
            InitialContext context = new InitialContext();
            search = (Search) context.lookup(Search.SERVICE_NAME);
            
            instanceUrl = PropertyReader.getProperty("escidoc.pubman.instance.url");
            contextPath = PropertyReader.getProperty("escidoc.pubman.instance.context.path");
            itemPattern = PropertyReader.getProperty("escidoc.pubman.item.pattern");
            
            interval = Integer.parseInt(PropertyReader.getProperty("escidoc.pubman.sitemap.task.interval"));

            maxItemsPerFile = Integer.parseInt(PropertyReader.getProperty("escidoc.pubman.sitemap.max.items"));
            maxItemsPerRetrieve = Integer.parseInt(PropertyReader.getProperty("escidoc.pubman.sitemap.retrieve.items"));
            
            retrievalTimeout = Integer.parseInt(PropertyReader.getProperty("escidoc.pubman.sitemap.retrieve.timeout"));

            contentModel = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
            
            contentModels = new ArrayList<String>();
            contentModels.add(contentModel);
            
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            changeFile();
            
            work();
            
            String appPath;
            try
            {
                appPath = ResourceUtil.getResourceAsFile("EditItemPage.jsp").getAbsolutePath();
            }
            catch (Exception e) {
                logger.error("EditItemPage.jsp was not found in web root, terminating sitemap task", e);
                return;
            }
            appPath = appPath.substring(0, appPath.lastIndexOf(System.getProperty("file.separator")) + 1);
            
            if (files.size() == 1)
            {
                File finalFile = new File(appPath + "sitemap.xml");
                files.get(0).renameTo(finalFile);
            }
            else
            {
                String currentDate = dateFormat.format(new Date());
                
                File indexFile = File.createTempFile("sitemap", ".xml");
                FileWriter indexFileWriter = new FileWriter(indexFile);
                
                indexFileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" "
                        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                        + "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");
                
                for (int i = 0; i < files.size(); i++)
                {
                    File finalFile = new File(appPath + "sitemap" + (i + 1) + ".xml");
                    files.get(i).renameTo(finalFile);
                    
                    indexFileWriter.write("\t<sitemap>\n\t\t<loc>"
                            + instanceUrl + contextPath + "/sitemap"
                            + (i + 1) + ".xml</loc>\n\t\t<lastmod>"
                            + currentDate + "</lastmod>\n\t</sitemap>\n");
                    
                }
                
                indexFileWriter.write("</sitemapindex>\n");
                indexFileWriter.flush();
                indexFileWriter.close();
                
                File finalFile = new File(appPath + "sitemap.xml");
                indexFile.renameTo(finalFile);
            }
            
            logger.info("Finished creating Sitemap.");

            sleep(interval * 60 * 1000);
            
            if (!signal)
            {
                Thread nextThread = new SiteMapTask();
                nextThread.start();
            }
        }
        catch (Exception e)
        {
            logger.error("Error creating Sitemap", e);
        }

    }

    private void work()
    {
        
        
        int firstRecord = 0;
        int totalRecords = 0;
        
        //fileWriter.write("<ul>");
        do
        {

            ItemContainerSearchResult itemSearchResult = getItems(firstRecord);
            totalRecords = itemSearchResult.getTotalNumberOfResults().intValue();
            addItemsToSitemap(itemSearchResult);

            firstRecord += maxItemsPerRetrieve;
            
            if (firstRecord % maxItemsPerFile == 0)
            {
                changeFile();
            }
            
            try
            {
                sleep(retrievalTimeout * 1000);
            }
            catch (InterruptedException e)
            {
                logger.info("Sitemap task interrupted.");
            }
            
        }
        while (firstRecord < totalRecords);

        finishSitemap();
    }
    
    private void changeFile()
    {
        try
        {
            if (fileWriter != null)
            {
                finishSitemap();
            }
            
            File file = File.createTempFile("sitemap", ".xml");
            fileWriter = new FileWriter(file);
            files.add(file);

            startSitemap();

        }
        catch (Exception e)
        {
            logger.error("Error creating sitemap file.", e);
        }
    }

    /**
     * @param contentModels
     * @param orgUnit
     * @return
     * @throws TechnicalException
     * @throws Exception
     */
    private ItemContainerSearchResult getItems(int firstRecord)
    {
        SearchQuery itemQuery = new PlainCqlQuery("(escidoc.content-model.objid=" + contentModel + ")");
        itemQuery.setStartRecord(firstRecord + "");
        itemQuery.setMaximumRecords("100");
        try
        {
            ItemContainerSearchResult itemSearchResult = search.searchForItemContainer(itemQuery);
            return itemSearchResult;
        }
        catch (Exception e) {
            logger.error("Error getting items", e);
            return null;
        }
    }
    
    private void startSitemap()
    {
        try
        {
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            		+ "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" "
            		+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            		+ "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void addItemsToSitemap(ItemContainerSearchResult searchResult)
    {
        List<SearchResultElement> results = searchResult.getResultList();
        for (SearchResultElement result : results)
        {
            if (result instanceof ItemVO)
            {
                PubItemVO pubItemVO = new PubItemVO((ItemVO) result);
                TextVO title = pubItemVO.getMetadata().getTitle();
                try
                {
                    fileWriter.write("\t<url>\n\t\t<loc>");
                    fileWriter.write(instanceUrl);
                    fileWriter.write(contextPath);
                    fileWriter.write(itemPattern.replace("$1", pubItemVO.getVersion().getObjectId()));
                    fileWriter.write("</loc>\n\t\t<lastmod>");
                    fileWriter.write(dateFormat.format(pubItemVO.getModificationDate()));
                    fileWriter.write("</lastmod>\n\t</url>\n");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                logger.error("Search result is not an ItemVO, "
                        + "but a " + result.getClass().getSimpleName());
                if (result instanceof ContainerVO)
                {
                    logger.error("Container is " + ((ContainerVO) result).getVersion().getObjectId());
                }
            }
        }
    }
    
    private void finishSitemap()
    {
        try
        {
            fileWriter.write("</urlset>");
            fileWriter.flush();
            fileWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void terminate()
    {
        logger.info("Sitemap creation task signalled to terminate.");
        signal = true;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Thread nextThread = new SiteMapTask();
        nextThread.start();
    }
}
