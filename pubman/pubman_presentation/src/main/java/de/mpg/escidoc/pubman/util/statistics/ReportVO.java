package de.mpg.escidoc.pubman.util.statistics;

import java.util.ArrayList;
import java.util.Collection;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * TODO Description
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ReportVO extends ValueObject
{
    private Collection<ReportRecordVO> records;
    private String reportDefinitionObjID;

    public ReportVO()
    {
        records = new ArrayList<ReportRecordVO>();
    }

    public String getReportDefinitionObjID()
    {
        return reportDefinitionObjID;
    }

    public void setReportDefinitionObjID(String reportDefinitionObjID)
    {
        this.reportDefinitionObjID = reportDefinitionObjID;
    }

    public void addRecord(ReportRecordVO p)
    {
        records.add(p);
    }

    public Collection<ReportRecordVO> getReportRecords()
    {
        return records;
    }
}
