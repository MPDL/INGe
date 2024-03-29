package de.mpg.mpdl.inge.model.valueobjects.publication;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ItemVO;

@SuppressWarnings("serial")
public class PubItemVO extends ItemVO {

  private static final Logger logger = Logger.getLogger(PubItemVO.class);

  /**
   * Default constructor.
   */
  public PubItemVO() {
    try {
      // TODO remove content Model after migration
      this.setContentModel("");
    } catch (Exception e) {
      logger.error("Unable to set publication content model", e);
    }
  }

  /**
   * Clone constructor.
   * 
   * @param itemVO The item to be copied.
   */
  public PubItemVO(ItemVO itemVO) {
    super(itemVO);
  }

  public MdsPublicationVO getMetadata() {
    if (getMetadataSets() != null && getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsPublicationVO) {
      return (MdsPublicationVO) getMetadataSets().get(0);
    } else {
      return null;
    }
  }

  public void setMetadata(MdsPublicationVO mdsPublicationVO) {
    if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsPublicationVO) {
      getMetadataSets().set(0, mdsPublicationVO);
    } else if (getMetadataSets() != null) {
      getMetadataSets().add(mdsPublicationVO);
    }
  }

}
