package de.mpg.mpdl.inge.pubman.web.util.filter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

    try {
      chain.doFilter(request, response);
    } catch (ServletException e) {
      logger.info("########################################");
      logger.info("chain:" + chain);
      logger.info("request:" + request);
      logger.info("response:" + response);
      logger.info("########################################");
      throw e;
    }
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {}
}
