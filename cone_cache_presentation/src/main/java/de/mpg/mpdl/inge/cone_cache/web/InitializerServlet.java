package de.mpg.mpdl.inge.cone_cache.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

  // TODO: Authorisierung + Umstellung auf REST service
  // (dabei ConeCacheRefreshTask umstellen von @Scheduled(cron = "${inge.cron.conecache.refresh})"
  // auf @Scheduled(fixedDelay = 3600000, initialDelay = 0) da sonst beim Deployen der Cache nicht
  // initial refreshed wird)

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    this.initTask = new ConeCacheInitTask();
    this.initTask.start();
    resp.getWriter().write("CONE CACHE refresh requested!");
  }

  @Override
  public void destroy() {
    super.destroy();

    this.initTask.interrupt();
  }
}
