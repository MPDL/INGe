package de.mpg.escidoc.pubman.statistic_charts;

import java.util.Calendar;
import java.util.Date;

import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordParamVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;

public class StatisticReportRecordVOPresentation implements Comparable<StatisticReportRecordVOPresentation>
{
    private StatisticReportRecordVO statisticReportRecordVO;
    
    public StatisticReportRecordVOPresentation(StatisticReportRecordVO rr)
    {
        this.statisticReportRecordVO = rr;
    }
    public int getRequests()
    {
        for(StatisticReportRecordParamVO param : statisticReportRecordVO.getParamList())
        {
            if (param.getName().equals("itemrequests") || param.getName().equals("filerequests"))
            {
              return Integer.parseInt(param.getParamValue().getValue());
            }
        }
        return 0;
    }
    
    public int getMonth()
    {
        for(StatisticReportRecordParamVO param : statisticReportRecordVO.getParamList())
        {
            if (param.getName().equals("month"))
            {
              return Integer.parseInt(param.getParamValue().getValue());
            }
        }
        return 0;
    }
    
    public int getYear()
    {
        for(StatisticReportRecordParamVO param : statisticReportRecordVO.getParamList())
        {
            if (param.getName().equals("year"))
            {
              return Integer.parseInt(param.getParamValue().getValue());
            }
        }
        return 0;
    }

    public int compareTo(StatisticReportRecordVOPresentation rep2)
    {
        int month1= getMonth();
        int year1= getYear();
        int month2= rep2.getMonth();
        int year2= rep2.getYear();
        
      
        
        Calendar cal1 = Calendar.getInstance();
        cal1.set(year1, month1, 1);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.set(year2, month2, 1);
        
        return cal1.compareTo(cal2);
        

        
        
    }
    
}
