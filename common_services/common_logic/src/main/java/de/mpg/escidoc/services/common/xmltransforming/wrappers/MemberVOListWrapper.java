package de.mpg.escidoc.services.common.xmltransforming.wrappers;

import java.io.Serializable;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.MemberVO;

public class MemberVOListWrapper implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    protected List<MemberVO> memberVOList;
    

    public List<MemberVO> getMemberVOList()
    {
        return memberVOList;
    }

    public void setMemberVOList(List<MemberVO> memberVOList)
    {
        this.memberVOList = memberVOList;
    }


}
