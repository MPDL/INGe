package de.mpg.escidoc.services.batchprocess.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveRecordVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

public class CoreServiceHelper
{
    public static HashMap<String, String[]> createBasicFilter(String query, int numberOfRecord)
    {
        HashMap<String, String[]> filter = new HashMap<String, String[]>();
        filter.put("operation", new String[] { "searchRetrieve" });
        filter.put("version", new String[] { "1.1" });
        filter.put("query", new String[] { query });
        filter.put("maximumRecords", new String[] { Integer.toString(numberOfRecord) });
        filter.put("startRecord", new String[] { "0" });
        return filter;
    }

    public static List<ItemVO> transformSearchResultXmlToListOfItemVO(String seachResultXml) throws Exception
    {
        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
        SearchRetrieveResponseVO response = xmlTransforming.transformToSearchRetrieveResponse(seachResultXml);
        List<ItemVO> list = new ArrayList<ItemVO>();
        if (response.getRecords() != null)
        {
            for (SearchRetrieveRecordVO record : response.getRecords())
            {
                list.add((ItemVO)record.getData());
            }
        }
        return list;
    }
}
