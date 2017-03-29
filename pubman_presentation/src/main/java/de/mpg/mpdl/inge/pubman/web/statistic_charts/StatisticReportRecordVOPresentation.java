package de.mpg.mpdl.inge.pubman.web.statistic_charts;

import java.util.Calendar;

import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportRecordParamVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportRecordVO;

public class StatisticReportRecordVOPresentation implements
    Comparable<StatisticReportRecordVOPresentation> {
  private final StatisticReportRecordVO statisticReportRecordVO;

  public StatisticReportRecordVOPresentation(StatisticReportRecordVO rr) {
    this.statisticReportRecordVO = rr;
  }

  public int getRequests() {
    for (final StatisticReportRecordParamVO param : this.statisticReportRecordVO.getParamList()) {
      if (param.getName().equals("itemrequests") || param.getName().equals("filerequests")) {
        return Integer.parseInt(param.getParamValue().getValue());
      }
    }

    return 0;
  }

  public int getMonth() {
    for (final StatisticReportRecordParamVO param : this.statisticReportRecordVO.getParamList()) {
      if (param.getName().equals("month")) {
        return Integer.parseInt(param.getParamValue().getValue());
      }
    }

    return 0;
  }

  public int getYear() {
    for (final StatisticReportRecordParamVO param : this.statisticReportRecordVO.getParamList()) {
      if (param.getName().equals("year")) {
        return Integer.parseInt(param.getParamValue().getValue());
      }
    }

    return 0;
  }

  @Override
  public int compareTo(StatisticReportRecordVOPresentation rep2) {
    final int month1 = this.getMonth();
    final int year1 = this.getYear();
    final int month2 = rep2.getMonth();
    final int year2 = rep2.getYear();

    final Calendar cal1 = Calendar.getInstance();
    cal1.set(year1, month1, 1);

    final Calendar cal2 = Calendar.getInstance();
    cal2.set(year2, month2, 1);

    return cal1.compareTo(cal2);
  }
}
