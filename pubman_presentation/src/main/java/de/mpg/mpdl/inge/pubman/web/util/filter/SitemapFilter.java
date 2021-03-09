package de.mpg.mpdl.inge.pubman.web.util.filter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.cone.SQLQuerier;
import de.mpg.mpdl.inge.pubman.web.util.threads.SiteMapTask;

public class SitemapFilter implements Filter {
  private static final Logger logger = Logger.getLogger(SQLQuerier.class);

  @Override
  public void destroy() {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
      final HttpServletRequest req = (HttpServletRequest) request;
      final HttpServletResponse resp = (HttpServletResponse) response;
      final String filename = req.getServletPath();

      if (filename != null && filename.matches("^/sitemap\\d*\\.xml$")) {
        final File sitemapFile = new File(SiteMapTask.SITEMAP_PATH + filename);

        if (!sitemapFile.exists()) {
          resp.sendError(HttpStatus.SC_NOT_FOUND);
          return;
        } else {
          resp.setContentType("text/xml");
          resp.setContentLength((int) sitemapFile.length());

          final OutputStream out = resp.getOutputStream();
          final BufferedInputStream in = new BufferedInputStream(new FileInputStream(sitemapFile));
          final byte[] buffer = new byte[8 * 1024];
          int count;
          while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
          }

          in.close();
          out.flush();
          out.close();
          return;
        }
      }
    }

    logger.info("################");
    logger.info(chain);
    logger.info(request);
    logger.info(response);
    logger.info("################");
    chain.doFilter(request, response);
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {}
}
