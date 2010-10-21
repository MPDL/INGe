package de.mpg.escidoc.pubman.yearbook;

import java.util.Date;

import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

public class YearbookInvalidItemRO extends ItemRO implements Comparable<ItemRO>
{
    
    private ValidationReportVO validationReport;
    private Date lastModificationDate;

    public YearbookInvalidItemRO(String objectId, ValidationReportVO report, Date lmd)
    {
        super(objectId);
        this.validationReport = report;
        this.lastModificationDate = lmd;
        
    }
    
    public void setValidationReport(ValidationReportVO validationReport)
    {
        this.validationReport = validationReport;
    }

    public ValidationReportVO getValidationReport()
    {
        return validationReport;
        
    }

    public void setLastModificationDate(Date lastModificationDate)
    {
        this.lastModificationDate = lastModificationDate;
    }

    public Date getLastModificationDate()
    {
        return lastModificationDate;
    }

    public int compareTo(YearbookInvalidItemRO arg0)
    {
        return 0;
    }

    public int compareTo(ItemRO arg0)
    {
        return this.getObjectId().compareTo(arg0.getObjectId());
    }
    
    
    
    
    
}
