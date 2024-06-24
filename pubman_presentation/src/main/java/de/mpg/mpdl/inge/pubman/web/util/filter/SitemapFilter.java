package de.mpg.mpdl.inge.pubman.web.util.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

public class SitemapFilter implements Filter {
  //  private static final Logger logger = LogManager.getLogger(SitemapFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    //    if ((request instanceof HttpServletRequest req) && (response instanceof HttpServletResponse resp)) {
    //      String filename = req.getServletPath();
    //
    //      if (null != filename && filename.matches("^/sitemap\\d*\\.xml$")) {
    //        File sitemapFile = new File(SiteMapTask.SITEMAP_PATH + filename);
    //
    //        if (!sitemapFile.exists()) {
    //          resp.sendError(HttpStatus.SC_NOT_FOUND);
    //          return;
    //        } else {
    //          resp.setContentType("text/xml");
    //          resp.setContentLength((int) sitemapFile.length());
    //
    //          OutputStream out = resp.getOutputStream();
    //          BufferedInputStream in = new BufferedInputStream(new FileInputStream(sitemapFile));
    //          byte[] buffer = new byte[8 * 1024];
    //          int count;
    //          while (-1 != (count = in.read(buffer))) {
    //            out.write(buffer, 0, count);
    //          }
    //
    //          in.close();
    //          out.flush();
    //          out.close();
    //          return;
    //        }
    //      }
    //    }
    //
    //    try {
    //      chain.doFilter(request, response);
    //    } catch (ServletException e) {
    //      logger.info("########################################");
    //      logger.info("chain:" + chain);
    //      logger.info("request:" + request);
    //      logger.info("response:" + response);
    //      logger.info("########################################");
    //      throw e;
    //    }
  }

}
