package de.mpg.escidoc.services.batchprocess;

import java.util.SortedMap;
import java.util.TreeMap;

public class BatchProcessReport
{
    private SortedMap<String, ReportEntry> report = new TreeMap<String, ReportEntry>();

    public enum ReportEntryStatusType
    {
        FINE, PROBLEM, ERROR;
    }

    public void addEntry(String key, String msg, ReportEntryStatusType status)
    {
        report.put(key, new ReportEntry(msg, status));
    }

    public String printReport()
    {
        String text = "";
        for (ReportEntry entry : report.values())
        {
            text += entry.getMessage() + " : " + entry.getStatus() + " \n";
        }
        return text;
    }

    public class ReportEntry
    {
        private ReportEntryStatusType status = ReportEntryStatusType.FINE;
        private String msg = "";

        public ReportEntry(String msg, ReportEntryStatusType status)
        {
            this.msg = msg;
            this.status = status;
        }

        public String getMessage()
        {
            return msg;
        }

        public String getStatus()
        {
            return status.name();
        }
    }
}
