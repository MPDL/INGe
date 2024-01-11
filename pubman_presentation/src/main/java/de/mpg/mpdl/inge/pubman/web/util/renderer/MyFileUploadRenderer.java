package de.mpg.mpdl.inge.pubman.web.util.renderer;

import org.primefaces.component.fileupload.FileUploadRenderer;

/**
 * Workaround for primefaces file uplaod until bug is resolved:
 * https://code.google.com/p/primefaces/issues/detail?id=6925
 * http://stackoverflow.com/questions/19262356
 * /file-upload-doesnt-work-with-ajax-in-primefaces-4-0-running-on-jsf-2-2-x/19752138#19752138
 * 
 * @author haarlae1
 * 
 */
public class MyFileUploadRenderer extends FileUploadRenderer {
  /*
  @Override
  public void decode(FacesContext context, UIComponent component) {
    if (context.getExternalContext().getRequestContentType().toLowerCase().startsWith("multipart/")) {
      super.decode(context, component);
    }
  }
  
   */
}
