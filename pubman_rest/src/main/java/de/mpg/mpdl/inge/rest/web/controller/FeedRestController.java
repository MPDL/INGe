package de.mpg.mpdl.inge.rest.web.controller;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedOutput;

import de.mpg.mpdl.inge.service.feed.FeedServiceImpl;

@RestController
@RequestMapping("/feed")
public class FeedRestController {

  private final static String DEFAULT_FEEDTYPE = "atom_1.0";
  private final static String DEFAULT_PRODUCE_MIMETYPE = MediaType.APPLICATION_ATOM_XML_VALUE;


  @Autowired
  private FeedServiceImpl feedService;

  @RequestMapping(value = "/recent", method = RequestMethod.GET,
      produces = DEFAULT_PRODUCE_MIMETYPE)
  public String getRecentReleases() throws Exception {
    SyndFeed feed = feedService.recentReleases();
    feed.setFeedType(DEFAULT_FEEDTYPE);
    return new SyndFeedOutput().outputString(feed);

  }

  @RequestMapping(value = "/organization/{ouId}", method = RequestMethod.GET,
      produces = DEFAULT_PRODUCE_MIMETYPE)
  public String getRecentReleasesforOu(@PathVariable(value = "ouId", required = true) String ouId)
      throws Exception {
    SyndFeed feed = feedService.recentReleasesforOrganizationalUnit(ouId);
    feed.setFeedType(DEFAULT_FEEDTYPE);
    return new SyndFeedOutput().outputString(feed);

  }

  @RequestMapping(value = "/search", method = RequestMethod.GET,
      produces = DEFAULT_PRODUCE_MIMETYPE)
  public String getSearchAsFeed(@RequestParam(value = "q", required = true) String query)
      throws Exception {
    QueryBuilder qb = QueryBuilders.wrapperQuery(query);
    SyndFeed feed = feedService.recentReleasesSearchQuery(qb);
    feed.setFeedType(DEFAULT_FEEDTYPE);
    return new SyndFeedOutput().outputString(feed);

  }

  @RequestMapping(value = "/oa", method = RequestMethod.GET, produces = DEFAULT_PRODUCE_MIMETYPE)
  public String getRecentOa() throws Exception {
    SyndFeed feed = feedService.recentReleasesOAPublications();
    feed.setFeedType(DEFAULT_FEEDTYPE);
    return new SyndFeedOutput().outputString(feed);

  }
}