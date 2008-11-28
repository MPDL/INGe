package de.mpg.escidoc.pubman.test;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.util.RelationVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

public class RevisionsRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, OrderFilter>
{
    
    private int numberOfRecords;
    
    public RevisionsRetrieverRequestBean()
    {
        super((PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class));
    }
    
    @Override
    public void init()
    {
        // No initialization needed
    }
    
    
    @Override
    public int getTotalNumberOfRecords()
    {
        return numberOfRecords;
    }

    @Override
    public String getType()
    {
        return "RevisionList";
    }

    @Override
    public void readOutParameters()
    {
        // No parameters needed
    }

    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, OrderFilter additionalFilters)
    {
        
        //limit and offset is ignored because no paginator is used
        List<PubItemVO> pubItemVOList = new ArrayList<PubItemVO>();
        
        
        try
        {
            ItemControllerSessionBean icsb = (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
            //get Revisions
            List<RelationVOPresentation> relationVOList = icsb.retrieveRevisions(icsb.getCurrentPubItem()); 
            
            for (RelationVOPresentation relationVO : relationVOList)
            {
                PubItemVO sourceItem = relationVO.getSourceItem();
                
                if (sourceItem!=null && sourceItem.getVersion().getState().toString().equals(PubItemVO.State.RELEASED.toString())) 
                {
                    pubItemVOList.add(sourceItem);
                }

            }
            
            //get ParentItems
            
            List<RelationVOPresentation> relationVOList2 = icsb.retrieveParentsForRevision(icsb.getCurrentPubItem());
            
            for (RelationVOPresentation relationVO : relationVOList2)
            {
                PubItemVO targetItem = relationVO.getTargetItem();
                if (targetItem!=null && targetItem.getVersion().getState().toString().equals(PubItemVO.State.RELEASED.toString())) 
                {
                    pubItemVOList.add(targetItem);
                }

            }
        }
        catch (Exception e)
        {
            error("Error with retrieving revisions");
        }
        
        numberOfRecords = pubItemVOList.size();
        return CommonUtils.convertToPubItemVOPresentationList(pubItemVOList);
    }
    
    @Override
    public String getListPageName()
    {
        return "ViewItemRevisionsPage.jsp";
    }
}
