package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.DegreeCriterion;

/**
 * POJO bean to deal with one DegreeCriterionVO.
 * 
 * @author Friederike Kleinfercher
 */
@SuppressWarnings("serial")
public class DegreeCriterionBean extends CriterionBean {
  private DegreeCriterion degreeCriterionVO;

  // selection fields for the MdsPublicationVO.Degree enum
  private boolean searchDiploma, searchMaster, searchPHD, searchHab, searchBachelor, searchStaats,
      searchMagister;

  public DegreeCriterionBean() {
    // ensure the parentVO is never null;
    this(new DegreeCriterion());
  }

  public DegreeCriterionBean(DegreeCriterion degreeCriterionVO) {
    this.setDegreeCriterionVO(degreeCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return this.degreeCriterionVO;
  }

  public DegreeCriterion getDegreeCriterionVO() {
    return this.degreeCriterionVO;
  }

  public void setDegreeCriterionVO(DegreeCriterion degreeCriterionVO) {
    this.degreeCriterionVO = degreeCriterionVO;
    if (degreeCriterionVO.getDegree() == null) {
      degreeCriterionVO.setDegree(new ArrayList<MdsPublicationVO.DegreeType>());
    }

    for (final MdsPublicationVO.DegreeType degree : degreeCriterionVO.getDegree()) {
      if (MdsPublicationVO.DegreeType.BACHELOR.equals(degree)) {
        this.searchBachelor = true;
      } else if (MdsPublicationVO.DegreeType.DIPLOMA.equals(degree)) {
        this.searchDiploma = true;
      } else if (MdsPublicationVO.DegreeType.HABILITATION.equals(degree)) {
        this.searchHab = true;
      } else if (MdsPublicationVO.DegreeType.MAGISTER.equals(degree)) {
        this.searchMagister = true;
      } else if (MdsPublicationVO.DegreeType.MASTER.equals(degree)) {
        this.searchMaster = true;
      } else if (MdsPublicationVO.DegreeType.PHD.equals(degree)) {
        this.searchPHD = true;
      } else if (MdsPublicationVO.DegreeType.STAATSEXAMEN.equals(degree)) {
        this.searchStaats = true;
      }
    }
  }


  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public void clearCriterion() {
    this.setSearchBachelor(false);
    this.setSearchDiploma(false);
    this.setSearchHab(false);
    this.setSearchMagister(false);
    this.setSearchMaster(false);
    this.setSearchPHD(false);
    this.setSearchStaats(false);

    this.degreeCriterionVO.getDegree().clear();
    this.degreeCriterionVO.setSearchString("");
  }

  public boolean isSearchDiploma() {
    return this.searchDiploma;
  }

  public void setSearchDiploma(boolean searchDiploma) {
    this.searchDiploma = searchDiploma;
    if (searchDiploma == true) {
      if (!this.degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.DIPLOMA)) {
        this.degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.DIPLOMA);
      }
    } else {
      this.degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.DIPLOMA);
    }
  }

  public boolean isSearchMaster() {
    return this.searchMaster;
  }

  public void setSearchMaster(boolean searchMaster) {
    this.searchMaster = searchMaster;
    if (searchMaster == true) {
      if (!this.degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.MASTER)) {
        this.degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.MASTER);
      }
    } else {
      this.degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.MASTER);
    }
  }

  public boolean isSearchPHD() {
    return this.searchPHD;
  }

  public void setSearchPHD(boolean searchPHD) {
    this.searchPHD = searchPHD;
    if (searchPHD == true) {
      if (!this.degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.PHD)) {
        this.degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.PHD);
      }
    } else {
      this.degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.PHD);
    }
  }

  public boolean isSearchHab() {
    return this.searchHab;
  }

  public void setSearchHab(boolean searchHab) {
    this.searchHab = searchHab;
    if (searchHab == true) {
      if (!this.degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.HABILITATION)) {
        this.degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.HABILITATION);
      }
    } else {
      this.degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.HABILITATION);
    }
  }

  public boolean isSearchBachelor() {
    return this.searchBachelor;
  }

  public void setSearchBachelor(boolean searchBachelor) {
    this.searchBachelor = searchBachelor;
    if (searchBachelor == true) {
      if (!this.degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.BACHELOR)) {
        this.degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.BACHELOR);
      }
    } else {
      this.degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.BACHELOR);
    }
  }

  public boolean isSearchStaats() {
    return this.searchStaats;
  }

  public void setSearchStaats(boolean searchStaats) {
    this.searchStaats = searchStaats;
    if (searchStaats == true) {
      if (!this.degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.STAATSEXAMEN)) {
        this.degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.STAATSEXAMEN);
      }
    } else {
      this.degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.STAATSEXAMEN);
    }
  }



  public boolean isSearchMagister() {
    return this.searchMagister;
  }

  public void setSearchMagister(boolean searchMagister) {
    this.searchMagister = searchMagister;
    if (searchMagister == true) {
      if (!this.degreeCriterionVO.getDegree().contains(MdsPublicationVO.DegreeType.MAGISTER)) {
        this.degreeCriterionVO.getDegree().add(MdsPublicationVO.DegreeType.MAGISTER);
      }
    } else {
      this.degreeCriterionVO.getDegree().remove(MdsPublicationVO.DegreeType.MAGISTER);
    }
  }

}
