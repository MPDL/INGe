package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;

import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.search.bean.criterion.DegreeCriterion;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

/**
 * POJO bean to deal with one DegreeCriterionVO.
 * 
 * @author Friederike Kleinfercher
 */
public class DegreeCriterionBean extends CriterionBean
{
	public static final String BEAN_NAME = "DegreeCriterionBean";
	
	private DegreeCriterion degreeCriterionVO;
	
	// selection fields for the MdsPublicationVO.Degree enum
	private boolean searchDiploma, searchMaster, searchPHD, searchHab, searchBachelor, searchStaats, searchMagister;

    public DegreeCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new DegreeCriterion());
	}

	public DegreeCriterionBean(DegreeCriterion degreeCriterionVO)
	{
		setDegreeCriterionVO(degreeCriterionVO);
	}

	@Override
	public Criterion getCriterionVO()
	{
		return degreeCriterionVO;
	}

	public DegreeCriterion getDegreeCriterionVO()
	{
		return degreeCriterionVO;
	}

	public void setDegreeCriterionVO(DegreeCriterion degreeCriterionVO)
	{
	    this.degreeCriterionVO = degreeCriterionVO;
        if (degreeCriterionVO.getDegree() == null)
        {
            degreeCriterionVO.setDegree(new ArrayList<MdsPublicationVO.DegreeType>());
        }
            
        for (MdsPublicationVO.DegreeType degree : degreeCriterionVO.getDegree())
        {
            if (MdsPublicationVO.DegreeType.BACHELOR.equals(degree))
                searchBachelor = true;
            else if (MdsPublicationVO.DegreeType.DIPLOMA.equals(degree))
                searchDiploma = true;
            else if (MdsPublicationVO.DegreeType.HABILITATION.equals(degree))
                searchHab = true;
            else if (MdsPublicationVO.DegreeType.MAGISTER.equals(degree))
                searchMagister = true;
            else if (MdsPublicationVO.DegreeType.MASTER.equals(degree))
                searchMaster = true;
            else if (MdsPublicationVO.DegreeType.PHD.equals(degree))
                searchPHD = true;
            else if (MdsPublicationVO.DegreeType.STAATSEXAMEN.equals(degree))
                searchStaats = true;
        }
	}
	
	
	/**
	 * Action navigation call to clear the current part of the form
	 * @return null
	 */
	public String clearCriterion()
	{
        setSearchBachelor(false);
        setSearchDiploma(false);
        setSearchHab(false);
        setSearchMagister(false);
        setSearchMaster(false);
        setSearchPHD(false);
        setSearchStaats(false);

        degreeCriterionVO.getDegree().clear();
        degreeCriterionVO.setSearchString("");
        
        // navigation refresh
        return null;
	}

    public boolean isSearchDiploma()
    {
        return searchDiploma;
    }

    public void setSearchDiploma(boolean searchDiploma)
    {
        this.searchDiploma = searchDiploma;
        if (searchDiploma == true)
        {
            if (!degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.DIPLOMA))
            {
                degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.DIPLOMA);
            }
        }
        else
        {
            degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.DIPLOMA);
        }
    }

    public boolean isSearchMaster()
    {
        return searchMaster;
    }

    public void setSearchMaster(boolean searchMaster)
    {
        this.searchMaster = searchMaster;
        if (searchMaster == true)
        {
            if (!degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.MASTER))
            {
                degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.MASTER);
            }
        }
        else
        {
            degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.MASTER);
        }
    }

    public boolean isSearchPHD()
    {
        return searchPHD;
    }

    public void setSearchPHD(boolean searchPHD)
    {
        this.searchPHD = searchPHD;
        if (searchPHD == true)
        {
            if (!degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.PHD))
            {
                degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.PHD);
            }
        }
        else
        {
            degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.PHD);
        }
    }

    public boolean isSearchHab()
    {
        return searchHab;
    }

    public void setSearchHab(boolean searchHab)
    {
        this.searchHab = searchHab;
        if (searchHab == true)
        {
            if (!degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.HABILITATION))
            {
                degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.HABILITATION);
            }
        }
        else
        {
            degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.HABILITATION);
        }
    }

    public boolean isSearchBachelor()
    {
        return searchBachelor;
    }

    public void setSearchBachelor(boolean searchBachelor)
    {
        this.searchBachelor = searchBachelor;
        if (searchBachelor == true)
        {
            if (!degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.BACHELOR))
            {
                degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.BACHELOR);
            }
        }
        else
        {
            degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.BACHELOR);
        }
    }

    public boolean isSearchStaats()
    {
        return searchStaats;
    }

    public void setSearchStaats(boolean searchStaats)
    {
        this.searchStaats = searchStaats;
        if (searchStaats == true)
        {
            if (!degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.STAATSEXAMEN))
            {
                degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.STAATSEXAMEN);
            }
        }
        else
        {
            degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.STAATSEXAMEN);
        }
    }
    
    

    public boolean isSearchMagister()
    {
        return searchMagister;
    }

    public void setSearchMagister(boolean searchMagister)
    {
        this.searchMagister = searchMagister;
        if (searchMagister == true)
        {
            if (!degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.MAGISTER))
            {
                degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.MAGISTER);
            }
        }
        else
        {
            degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.MAGISTER);
        }
    }

}
