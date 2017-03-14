package de.mpg.mpdl.inge.pubman.web.servlet;

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

import de.mpg.mpdl.inge.pubman.web.task.SiteMapTask;

public class SitemapFilter implements Filter {
  @Override
  public void destroy() {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
      HttpServletRequest req = (HttpServletRequest) request;
      HttpServletResponse resp = (HttpServletResponse) response;
      String filename = req.getServletPath();

      if (filename != null && filename.matches("^/sitemap\\d*\\.xml$")) {
        File sitemapFile = new File(SiteMapTask.SITEMAP_PATH + filename);

        if (!sitemapFile.exists()) {
          resp.sendError(HttpStatus.SC_NOT_FOUND);
          return;
        } else {
          resp.setContentType("text/xml");
          resp.setContentLength((int) sitemapFile.length());

          OutputStream out = resp.getOutputStream();
          BufferedInputStream in = new BufferedInputStream(new FileInputStream(sitemapFile));
          byte[] buffer = new byte[8 * 1024];
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

    chain.doFilter(request, response);
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {}
}
