package de.mpg.escidoc.services.common.xmltransforming.wrappers;

import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.ItemVO;

public class MemberListVOWrapper extends ItemVOListWrapper
{
    
    protected List<Object> containerVOList;
    

    public List<Object> getContainerVOList()
    {
        return containerVOList;
    }

    public void setContainerVOList(List<Object> containerVOList)
    {
        this.containerVOList = containerVOList;
    }


}
