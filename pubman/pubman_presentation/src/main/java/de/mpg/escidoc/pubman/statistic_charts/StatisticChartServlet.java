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
package de.mpg.escidoc.pubman.statistic_charts;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.encoders.KeypointPNGEncoderAdapter;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.escidoc.services.pubman.PubItemSimpleStatistics;

/**
 * 
 * Servlet that delivers image files in PNG format which incorporate statistic charts built from data from eSciDoc statistic service.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class StatisticChartServlet extends HttpServlet
{
    
    private static final String CONTENT_TYPE = "image/png";
    
    private static final String numberOfMonthsParameterName = "months";
    
    private static final String idParameterName = "id";
    
    private static final String typeParameterName = "type";
    
    private Logger logger = Logger.getLogger(StatisticChartServlet.class);

    private String id;

    private int numberOfMonths;
    
    private String type;
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
 
    public synchronized void doGet(HttpServletRequest request, 
                      HttpServletResponse response) throws ServletException, IOException {
        
        String numberOfMonthsString = request.getParameter(numberOfMonthsParameterName);
        if (numberOfMonthsString == null)
        {
            numberOfMonths = 12;
        }
        else
        {
            numberOfMonths = Integer.parseInt(numberOfMonthsString);
        }
           
        
        id = request.getParameter(idParameterName);
        type = request.getParameter(typeParameterName);
        
        try
        {
       
            CategoryDataset dataset = createDataset();
            JFreeChart chart = createChart(dataset);
            BufferedImage img = chart.createBufferedImage(500, 250);
            byte[] image = new KeypointPNGEncoderAdapter().encode(img);
            
            response.setContentType(CONTENT_TYPE);
            ServletOutputStream out = response.getOutputStream();
            out.write(image);
            out.flush();
            out.close();
            
           
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (Exception e)
        {
            
        }
        
  
    }
    
    
    /**
     * Retrieves statistic data from the framework and creates the dataset for the visualisation.
     *
     * @return The dataset.
     */
    private CategoryDataset createDataset() throws Exception{

        // create the dataset...
        
        PubItemSimpleStatistics pubItemStatistic = null;
        try
        {
            InitialContext initialContext = new InitialContext();
            pubItemStatistic  = (PubItemSimpleStatistics) initialContext.lookup(PubItemSimpleStatistics.SERVICE_NAME);
        }
        catch (NamingException e1)
        {
            e1.printStackTrace();
        }
        
        //retrieve the statistic reports
        List<StatisticReportRecordVO> reportListAllUsers = new ArrayList<StatisticReportRecordVO>();
        List<StatisticReportRecordVO> reportListAnonymousUsers = new ArrayList<StatisticReportRecordVO>();
        
        if (type.equals("item"))
        {
            reportListAllUsers = pubItemStatistic.getStatisticReportRecord(PubItemSimpleStatistics.REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ALL_USERS, id, null);
            reportListAnonymousUsers = pubItemStatistic.getStatisticReportRecord(PubItemSimpleStatistics.REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ANONYMOUS, id, null);
        }
        else if (type.equals("file"))
        {
            reportListAllUsers = pubItemStatistic.getStatisticReportRecord(PubItemSimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS, id, null);
            reportListAnonymousUsers = pubItemStatistic.getStatisticReportRecord(PubItemSimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS, id, null);
        }
        
        
        //sort the report records by date, newest first
        List<StatisticReportRecordVOPresentation> sortingListAllUsers = new ArrayList<StatisticReportRecordVOPresentation>();
        for (StatisticReportRecordVO reportRec : reportListAllUsers)
        {
            sortingListAllUsers.add(new StatisticReportRecordVOPresentation(reportRec));
        
        }
        Collections.sort(sortingListAllUsers);
        //take the newest values
        if (sortingListAllUsers.size()>(numberOfMonths))
            sortingListAllUsers = sortingListAllUsers.subList(sortingListAllUsers.size()-(numberOfMonths)-1, sortingListAllUsers.size());
        
        List<StatisticReportRecordVOPresentation> sortingListAnonymousUsers = new ArrayList<StatisticReportRecordVOPresentation>();
        for (StatisticReportRecordVO reportRec : reportListAnonymousUsers)
        {
            sortingListAnonymousUsers.add(new StatisticReportRecordVOPresentation(reportRec));
        
        }
        Collections.sort(sortingListAnonymousUsers);
        if (sortingListAnonymousUsers.size()>(numberOfMonths))
            sortingListAnonymousUsers = sortingListAnonymousUsers.subList(sortingListAnonymousUsers.size()-(numberOfMonths)-1, sortingListAnonymousUsers.size());
        
        //Create the dataset with 2 series for anonmyous and logged-in users.
        String loggedInUsersSeries = "Logged-in Users";
        String anonymousUsersSeries = "Anonymous Users";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -(numberOfMonths-1));

        Iterator<StatisticReportRecordVOPresentation> iter = sortingListAllUsers.iterator();
        StatisticReportRecordVOPresentation currentAllUsersRecord = null;
        if (iter.hasNext()) currentAllUsersRecord = iter.next();
        
        Iterator<StatisticReportRecordVOPresentation> iterAnonymous = sortingListAnonymousUsers.iterator();
        StatisticReportRecordVOPresentation currentAnonymousUsersRecord = null;
        if (iterAnonymous.hasNext()) currentAnonymousUsersRecord = iterAnonymous.next();
        
        
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i=0; i<numberOfMonths ; i++)
        {
            String xLabel = cal.get(Calendar.MONTH)+1 +"/"+cal.get(Calendar.YEAR);
            int allUserRequests = 0;
            int anonymousUserrequests = 0;
            
            if (currentAllUsersRecord!=null && currentAllUsersRecord.getMonth()==cal.get(Calendar.MONTH)+1 && currentAllUsersRecord.getYear()==cal.get(Calendar.YEAR))
            {
                allUserRequests = currentAllUsersRecord.getRequests();
                if (iter.hasNext()) currentAllUsersRecord = iter.next();
            }
            else
            {
                allUserRequests = 0;
            }
            
            if (currentAnonymousUsersRecord!=null && currentAnonymousUsersRecord.getMonth()==cal.get(Calendar.MONTH)+1 && currentAnonymousUsersRecord.getYear()==cal.get(Calendar.YEAR))
            {
                anonymousUserrequests = currentAnonymousUsersRecord.getRequests();
                if (iterAnonymous.hasNext()) currentAnonymousUsersRecord = iterAnonymous.next();
            }
            else
            {
                anonymousUserrequests = 0;
            }
           
           
           dataset.addValue(allUserRequests-anonymousUserrequests, loggedInUsersSeries, xLabel );
           dataset.addValue(anonymousUserrequests, anonymousUsersSeries, xLabel);
           
           cal.add(Calendar.MONTH, +1);   
        }

      
        /*
        //fill dataset in reverse order
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(int i=0; i<loggedInRequestsList.size();i++)
        {
            
          dataset.addValue(loggedInRequestsList.get(i), loggedInUsersSeries, xLabel );
          dataset.addValue(anonymousUserrequests, anonymousUsersSeries, xLabel);
        }
        dataset.getV
        */
        return dataset;

    }

    /**
     * Creates the statistic chart.
     *
     * @param dataset  the dataset.
     *
     * @return The chart.
     */
    private JFreeChart createChart(CategoryDataset dataset) {

        
        // create the chart...
        JFreeChart chart = ChartFactory.createStackedBarChart(
            null,       // chart title
            "",               // domain axis label
            "",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);
        
        //plot from oldest to newest date
      
        
        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLowerBound(0);
        // disable bar outlines...
        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64));
        renderer.setSeriesPaint(1, gp0);
       
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.red,
                0.0f, 0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(0, gp1);
        
        //Labels in bars
        /*
        renderer.setSeriesItemLabelsVisible(0, true);
        renderer.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator());
        renderer.setSeriesItemLabelPaint(0, Color.white);
        renderer.setSeriesItemLabelsVisible(1, true);
        renderer.setSeriesItemLabelGenerator(1, new StandardCategoryItemLabelGenerator());
        renderer.setSeriesItemLabelPaint(1, Color.white);
          */

        //setCategorySummary(dataset);


        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                        Math.PI / 6.0));

        // OPTIONAL CUSTOMISATION COMPLETED.
        return chart;
        
        
        
        
        
        
        /*
        
        CategoryAxis categoryAxis = new CategoryAxis("Month");
        categoryAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                        Math.PI / 6.0));
        //categoryAxis.setMaximumCategoryLabelWidthRatio(10.0f);
        ValueAxis valueAxis = new NumberAxis("Item Retrievals");
        valueAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());


        CategoryPlot plot = new CategoryPlot(dataset, 
                                             categoryAxis,
                                             valueAxis,
                                             new LayeredBarRenderer());
        
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);
        
        JFreeChart chart = new JFreeChart(plot);
        chart.getLegend().setVisible(true);

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        LayeredBarRenderer renderer = (LayeredBarRenderer) plot.getRenderer();
        
        renderer.setDrawBarOutline(false);
        // we can set each series bar width individually or let the renderer manage a standard view.
        // the width is set in percentage, where 1.0 is the maximum (100%).
        renderer.setSeriesBarWidth(0, 1);
        renderer.setSeriesBarWidth(1, 1);
//        renderer.setSeriesBarWidth(2, 0.4);
        
        
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64));
        renderer.setSeriesPaint(0, gp0);
       
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.red,
                0.0f, 0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(1, gp1);

        renderer.setItemMargin(0.01);
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.25);
        domainAxis.setUpperMargin(0.05);
        domainAxis.setLowerMargin(0.05);
        
        return chart;
*/

    }
    
    
 

}
