package de.mpg.escidoc.services.common.xmltransforming.wrappers;

import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;

public class MemberListVOWrapper extends ItemVOListWrapper
{
    
    protected List<? extends ContainerVO> containerVOList;

    public List<? extends ContainerVO> getContainerVOList()
    {
        return containerVOList;
    }

    public void setContainerVOList(List<? extends ContainerVO> containerVOList)
    {
        this.containerVOList = containerVOList;
    }


}
