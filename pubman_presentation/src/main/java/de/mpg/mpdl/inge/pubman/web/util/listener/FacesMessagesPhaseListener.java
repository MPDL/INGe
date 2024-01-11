package de.mpg.mpdl.inge.pubman.web.util.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

/**
 * 
 * With this listener, it can be avoided that Faces Messages are lost when doing an redirect instead
 * of applying a navigation rule
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@SuppressWarnings("serial")
public class FacesMessagesPhaseListener implements PhaseListener {
  private static final Logger logger = Logger.getLogger(FacesMessagesPhaseListener.class);

  private static final String sessionToken = "REDIRECT_MESSAGES_SUPPORT";

  /**
   * Caches Faces Messages after the Invoke Application phase and clears the cache after the Render
   * Response phase
   */
  @Override
  public synchronized void afterPhase(PhaseEvent event) {
    FacesMessagesPhaseListener.logger.trace(event.getPhaseId().toString() + " - After Phase");
    if (event.getPhaseId() == PhaseId.INVOKE_APPLICATION) {
      this.cacheMessages(event.getFacesContext());
    } else if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      this.removeFromCache(event.getFacesContext());
    }
  }

  /**
   * Restores the messages from the cache before the Restore View phase.
   */
  @Override
  public synchronized void beforePhase(PhaseEvent event) {
    FacesMessagesPhaseListener.logger.trace(event.getPhaseId().toString() + " - Before Phase");
    if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
      this.restoreMessages(event.getFacesContext());
    }
  }

  /**
   * Clears the whole cache
   * 
   * @param context
   */
  private void removeFromCache(FacesContext context) {
    this.getMessageCache(context).clear();
    FacesMessagesPhaseListener.logger.trace("Message Cache cleared");
  }

  /**
   * Caches messages from current faces context to a session object
   * 
   * @param context
   * @return
   */
  private int cacheMessages(FacesContext context) {
    int cachedCount = 0;
    final Iterator<String> clientIdsWithMessages = context.getClientIdsWithMessages();
    while (clientIdsWithMessages.hasNext()) {
      final String clientId = clientIdsWithMessages.next();
      final Iterator<FacesMessage> iterator = context.getMessages(clientId);
      Collection<FacesMessage> cachedMessages = this.getMessageCache(context).get(clientId);
      if (cachedMessages == null) {
        cachedMessages = new ArrayList<FacesMessage>();
        this.getMessageCache(context).put(clientId, cachedMessages);
      }
      while (iterator.hasNext()) {
        final FacesMessage facesMessage = iterator.next();
        if (cachedMessages.add(facesMessage)) {
          cachedCount++;
        }
      }
    }
    FacesMessagesPhaseListener.logger.trace("Saved " + cachedCount + " messages in cache");
    return cachedCount;
  }

  /**
   * Restores messages from session to faces context
   * 
   * @param context
   */
  private void restoreMessages(FacesContext context) {
    if (!this.getMessageCache(context).isEmpty()) {
      for (final String clientId : this.getMessageCache(context).keySet()) {
        for (final FacesMessage message : this.getMessageCache(context).get(clientId)) {
          context.addMessage(clientId, message);
        }
      }
      FacesMessagesPhaseListener.logger.trace("Restored Messages from Cache");
    }
  }

  @Override
  public PhaseId getPhaseId() {
    return PhaseId.ANY_PHASE;
  }

  private Map<String, Collection<FacesMessage>> getMessageCache(FacesContext context) {
    if (context.getExternalContext().getSessionMap().get(FacesMessagesPhaseListener.sessionToken) != null) {
      return (Map<String, Collection<FacesMessage>>) context.getExternalContext().getSessionMap()
          .get(FacesMessagesPhaseListener.sessionToken);
    } else {
      final Map<String, Collection<FacesMessage>> messageCache =
          Collections.synchronizedMap(new HashMap<String, Collection<FacesMessage>>());
      context.getExternalContext().getSessionMap().put(FacesMessagesPhaseListener.sessionToken, messageCache);
      return messageCache;
    }
  }
}
