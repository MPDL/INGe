package de.mpg.mpdl.inge.rest.web.controller;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedOutput;
import de.mpg.mpdl.inge.service.feed.FeedServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.StringReader;

@RestController
@RequestMapping("/feed")
@Tag(name = "Atom Feeds")
public class FeedRestController {

  private static final String DEFAULT_FEEDTYPE = "atom_1.0";
  private static final String DEFAULT_PRODUCE_MIMETYPE = MediaType.APPLICATION_ATOM_XML_VALUE + ";charset=UTF-8";

  @Autowired
  private FeedServiceImpl feedService;

  @RequestMapping(value = "/recent", method = RequestMethod.GET, produces = DEFAULT_PRODUCE_MIMETYPE)
  public String getRecentReleases() throws Exception {
    SyndFeed feed = feedService.recentReleases();
    feed.setFeedType(DEFAULT_FEEDTYPE);

    return new SyndFeedOutput().outputString(feed);
  }

  @RequestMapping(value = "/organization/{ouId}", method = RequestMethod.GET, produces = DEFAULT_PRODUCE_MIMETYPE)
  public String getRecentReleasesforOu(@PathVariable(value = "ouId", required = true) String ouId) throws Exception {
    SyndFeed feed = feedService.recentReleasesforOrganizationalUnit(ouId);
    feed.setFeedType(DEFAULT_FEEDTYPE);

    return new SyndFeedOutput().outputString(feed);
  }

  @RequestMapping(value = "/search", method = RequestMethod.GET, produces = DEFAULT_PRODUCE_MIMETYPE)
  public String getSearchAsFeed(@RequestParam(value = "q", required = true) String query) throws Exception {
    Query qb = Query.of(q -> q.withJson(new StringReader(query)));
    //QueryBuilder qb = QueryBuilders.wrapperQuery(query);
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
