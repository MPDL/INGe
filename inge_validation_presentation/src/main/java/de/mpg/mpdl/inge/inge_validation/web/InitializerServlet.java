package de.mpg.mpdl.inge.inge_validation.web;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import de.mpg.mpdl.inge.inge_validation.ItemValidating;

@SuppressWarnings("serial")
public class InitializerServlet extends HttpServlet {

  RefreshTask refreshTask;

  @EJB
  private ItemValidating itemValidating;

  @Override
  public final void init() throws ServletException {
    super.init();

    this.refreshTask = new RefreshTask();
    this.refreshTask.start();
  }

  @Override
  public void destroy() {
    super.destroy();

    this.refreshTask.interrupt();
  }

}
