package de.mpg.escidoc.services.batchprocess.helper;

import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.axis.message.MessageElement;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchResultVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveRecordVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
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

    public static List<ItemVO> transformSearchResultXmlToListOfItemVO(String searchResultXml) throws Exception
    {
        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
        SearchRetrieveResponseVO response = xmlTransforming.transformToSearchRetrieveResponse(searchResultXml);
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

    public static List<ItemVO> transformSearchResultXmlToListOfItemVO(SearchRetrieveResponseType searchResult) throws Exception
    {
        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
        ArrayList<ItemVO> resultList = new ArrayList<ItemVO>();
        if (searchResult.getRecords() != null)
        {
            for (RecordType record : searchResult.getRecords().getRecord())
            {
                StringOrXmlFragment data = record.getRecordData();
                MessageElement[] messages = data.get_any();
                // Data is in the first record
                if (messages.length == 1)
                {
                    String searchResultItem = null;
                    try
                    {
                        searchResultItem = messages[0].getAsString();
                    }
                    catch (Exception e) 
                    {
                        throw new TechnicalException("Error getting search result message.", e);
                    }
                    SearchResultElement itemResult = xmlTransforming.transformToSearchResult(searchResultItem);
                    ItemVO itemVO = (ItemResultVO) itemResult;
                    resultList.add(itemVO);
                }
            }
        }
        return resultList;
    }
}
