package de.mpg.mpdl.inge.service.feed;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.rometools.rome.feed.synd.*;
import de.mpg.mpdl.inge.model.valueobjects.*;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.util.HtmlUtils;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import de.mpg.mpdl.inge.util.UriBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


@Service
public class FeedServiceImpl {


  private static final Logger logger = Logger.getLogger(FeedServiceImpl.class);

  @Autowired
  private PubItemService pubItemService;

  public SyndFeed recentReleases() throws Exception {
    BoolQuery.Builder qb = new BoolQuery.Builder();
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_VERSION_STATE, State.RELEASED.name()));
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(), PubItemServiceDbImpl.INDEX_PUBLIC_STATE,
        State.RELEASED.name()));
    SyndFeed feed = getBasicSyndFeed("Recent releases in repository", "Recent releases in repository", qb.build()._toQuery());
    return feed;
  }

  public SyndFeed recentReleasesforOrganizationalUnit(String ouId) throws Exception {
    BoolQuery.Builder qb = new BoolQuery.Builder();
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_VERSION_STATE, State.RELEASED.name()));
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(), PubItemServiceDbImpl.INDEX_PUBLIC_STATE,
        State.RELEASED.name()));
    String[] indexes = new String[] {PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIERPATH,
        PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIERPATH};
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(), indexes, ouId));

    SyndFeed feed =
        getBasicSyndFeed("Recent releases for organization " + ouId, "Recent releases for organization " + ouId, qb.build()._toQuery());
    return feed;
  }

  public SyndFeed recentReleasesSearchQuery(Query givenQb) throws Exception {
    BoolQuery.Builder qb = new BoolQuery.Builder();
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_VERSION_STATE, State.RELEASED.name()));
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(), PubItemServiceDbImpl.INDEX_PUBLIC_STATE,
        State.RELEASED.name()));
    qb.must(givenQb);

    SyndFeed feed = getBasicSyndFeed("Search result as feed", "", qb.build()._toQuery());
    return feed;
  }

  public SyndFeed recentReleasesOAPublications() throws Exception {
    BoolQuery.Builder qb = new BoolQuery.Builder();
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_VERSION_STATE, State.RELEASED.name()));
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(), PubItemServiceDbImpl.INDEX_PUBLIC_STATE,
        State.RELEASED.name()));
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_FILE_VISIBILITY, FileVO.Visibility.PUBLIC.name()));
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(), PubItemServiceDbImpl.INDEX_FILE_STORAGE,
        FileVO.Storage.INTERNAL_MANAGED.name()));

    SyndFeed feed =
        getBasicSyndFeed("Recent Open Access Publications", "Feed for the Open Access Homepage of the MPG", qb.build()._toQuery());
    return feed;
  }

  private List<PubItemVO> search(Query qb) throws Exception {
    SearchRequest.of(sr -> sr.size(50).query(qb)
        .sort(s -> s.field(fs -> fs.field(PubItemServiceDbImpl.INDEX_LATESTRELEASE_DATE).order(SortOrder.Desc))));

    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO();
    srr.setLimit(50);
    srr.setQueryBuilder(qb);
    srr.setSortKeys(new SearchSortCriteria[] {
        new SearchSortCriteria(PubItemServiceDbImpl.INDEX_LATESTRELEASE_DATE, SearchSortCriteria.SortOrder.DESC)});

    SearchRetrieveResponseVO res = pubItemService.search(srr, null);

    return res.getRecords();
  }

  private SyndFeed getBasicSyndFeed(String title, String description, Query qb) throws Exception {
    SyndFeed feed = new SyndFeedImpl();
    feed.setTitle(title);
    feed.setDescription(description);
    //    feed.setCategories(getGeneralCategories());
    feed.setLanguage("en-US");

    feed.setEntries(transformToEntryList(search(qb)));
    return feed;
  }


  //  private List<SyndCategory> getGeneralCategories() {
  //    String categories = PropertyReader.getProperty(PropertyReader.INGE_FEED_CATEGORIES);
  //    List<SyndCategory> catList = new ArrayList<>();
  //    if (categories != null) {
  //      for (String cat : categories.split(",")) {
  //        SyndCategory syndCat = new SyndCategoryImpl();
  //        syndCat.setName(cat.trim());
  //      }
  //    }
  //
  //    return catList;
  //  }

  /**
   * Transformation method takes ItemList XML and transforms it to the list of syndication entries (
   * <code><List>SyndEntry</code>)
   * 
   * @param itemList
   * @return <List>SyndEntry
   */
  private List<SyndEntry> transformToEntryList(List<PubItemVO> itemList) {

    List<SyndEntry> entries = new ArrayList<SyndEntry>();



    for (ItemVO item : itemList) {

      SyndEntry se = new SyndEntryImpl();

      PubItemVO pi = (PubItemVO) item;
      MdsPublicationVO md = pi.getMetadata();

      // Content

      /*
       * SyndContent scont = new SyndContentImpl(); scont.setType("application/xml"); try { String
       * itemXml = replaceXmlHeader(xt.transformToItem( pi ));
       * 
       * scont.setValue( itemXml ); if ( "atom_0.3".equals(getFeedType()) )
       * scont.setMode(Content.XML); } catch (TechnicalException e) { throw new RuntimeException(
       * "Cannot transform to XML: ", e); };
       * 
       * se.setContents(Arrays.asList(scont));
       */
      //
      se.setTitle(HtmlUtils.removeSubSupIfBalanced(md.getTitle()));

      // Description ??? optional
      List<AbstractVO> abs = md.getAbstracts();
      SyndContent sc = new SyndContentImpl();
      if (abs.size() > 0) {
        sc.setValue(abs.get(0).getValue());
      }

      se.setDescription(sc);

      // Category
      String subj = md.getFreeKeywords();
      if (subj != null && !subj.isEmpty()) {
        List<SyndCategory> categories = new ArrayList<SyndCategory>();
        SyndCategory scat = new SyndCategoryImpl();
        scat.setName(subj);
        categories.add(scat);
        se.setCategories(categories);
      }


      if (md.getCreators() != null && md.getCreators().size() > 0) {
        List<SyndPerson> authors = new ArrayList<SyndPerson>();
        SyndPerson sp;
        StringBuffer allCrs = new StringBuffer();
        int counter = 0;
        for (CreatorVO creator : md.getCreators()) {

          String crs = creator.getPerson() != null ? creator.getPerson().getFamilyName() + ", " + creator.getPerson().getGivenName()
              : creator.getOrganization().getName();

          sp = new SyndPersonImpl();
          sp.setName(crs);
          authors.add(sp);


          allCrs.append(crs);
          if (counter + 1 != md.getCreators().size()) {
            allCrs.append("; ");
          }

          counter++;

        }

        se.setAuthor(allCrs.toString());
        se.setAuthors(authors);
        // se.setContributors(contributors);
      }

      // Contents ???
      // se.setContents(contents)

      try {
        se.setLink(UriBuilder.getItemObjectAndVersionLink(pi.getLatestRelease().getObjectId(), pi.getLatestRelease().getVersionNumber())
            .toString());
      } catch (URISyntaxException e) {
        logger.error("Error building URL", e);
      }

      // Uri ????
      se.setUri(se.getLink());

      // Entry UpdatedDate ???
      if (pi.getModificationDate() != null) { // gibt sonst NullPointerException
        se.setUpdatedDate(pi.getModificationDate());
      }

      // Entry PublishedDate ???
      if (pi.getLatestRelease().getModificationDate() != null) { // gibt sonst NullPointerException
        se.setPublishedDate(pi.getLatestRelease().getModificationDate());
      }


      entries.add(se);

    }

    return entries;


  }

}
