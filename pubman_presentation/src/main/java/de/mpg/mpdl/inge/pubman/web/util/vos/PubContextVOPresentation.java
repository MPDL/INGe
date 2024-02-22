package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.text.Collator;
import java.util.Locale;

import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem;
import de.mpg.mpdl.inge.pubman.web.easySubmission.EasySubmission;
import de.mpg.mpdl.inge.pubman.web.easySubmission.EasySubmissionSessionBean;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.multipleimport.MultipleImport;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;

/**
 * Wrapper class for contexts to be used in the presentation.
 *
 * @author franke
 * @author $Author$
 * @version: $Revision$ $LastChangedDate: 2007-12-04 16:52:04 +0100 (Di, 04 Dez 2007)$
 */
@SuppressWarnings("serial")
public class PubContextVOPresentation extends ContextDbVO implements Comparable<PubContextVOPresentation> {

  private boolean selected = false;

  public PubContextVOPresentation(ContextDbVO item) {
    super(item);
  }

  public boolean getDisabled() {
    if (ContextDbVO.State.CLOSED.equals(this.getState())) {
      return Boolean.TRUE;
    } else {
      return Boolean.FALSE;
    }
  }

  public boolean getSelected() {
    return this.selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String select() {
    this.selected = true;

    if (CreateItem.SubmissionMethod.FULL_SUBMISSION == this.getCreateItem().getMethod()) {
      this.getItemControllerSessionBean().getCurrentPubItem().getObject().setContext(this);
      return EditItem.LOAD_EDITITEM;
    } else if (CreateItem.SubmissionMethod.MULTIPLE_IMPORT == this.getCreateItem().getMethod()) {
      MultipleImport multipleImport = FacesTools.findBean("MultipleImport");
      multipleImport.setContext(this);
      return MultipleImport.LOAD_MULTIPLE_IMPORT;
    } else {
      throw new RuntimeException("Submission method not set or unknown");
    }
  }

  public void selectForEasySubmission() {
    this.selected = true;

    // deselect all other contexts
    if (null != this.getContextListSessionBean().getDepositorContextList()) {
      for (int i = 0; i < this.getContextListSessionBean().getDepositorContextList().size(); i++) {
        this.getContextListSessionBean().getDepositorContextList().get(i).selected = false;
      }
    }

    this.getItemControllerSessionBean().createNewPubItem(EasySubmission.LOAD_EASYSUBMISSION, this);
    this.getEasySubmissionSessionBean().setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);

    if (this.getEasySubmissionSessionBean().getCurrentSubmissionMethod().equals(EasySubmissionSessionBean.SUBMISSION_METHOD_FETCH_IMPORT)) {
    } else {
    }
  }

  @Override
  public int compareTo(PubContextVOPresentation compareObject) {
    Collator collator = Collator.getInstance(Locale.getDefault());
    collator.setStrength(Collator.SECONDARY);
    return collator.compare(this.getName(), compareObject.getName());
  }

  private ContextListSessionBean getContextListSessionBean() {
    return FacesTools.findBean("ContextListSessionBean");
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return FacesTools.findBean("ItemControllerSessionBean");
  }

  private EasySubmissionSessionBean getEasySubmissionSessionBean() {
    return FacesTools.findBean("EasySubmissionSessionBean");
  }

  private CreateItem getCreateItem() {
    return FacesTools.findBean("CreateItem");
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof PubContextVOPresentation && 0 == compareTo((PubContextVOPresentation) obj);
  }
}
