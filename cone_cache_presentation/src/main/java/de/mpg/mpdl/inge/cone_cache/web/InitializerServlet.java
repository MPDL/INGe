package de.mpg.mpdl.inge.cone_cache.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import de.mpg.mpdl.inge.cone_cache.ConeCacheInitTask;

@SuppressWarnings("serial")
public class InitializerServlet extends HttpServlet {

  ConeCacheInitTask initTask;

  @Override
  public final void init() throws ServletException {
    super.init();

    this.initTask = new ConeCacheInitTask();
    this.initTask.start();
  }

  @Override
  public void destroy() {
    super.destroy();

    this.initTask.interrupt();
  }
}
