package de.mpg.mpdl.inge.service.feed;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndCategoryImpl;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.SyndPersonImpl;

import de.mpg.mpdl.inge.model.valueobjects.FileVO.Storage;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.util.HtmlUtils;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import de.mpg.mpdl.inge.util.PropertyReader;


@Service
public class FeedServiceImpl {


  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private OrganizationService organizationService;

  public SyndFeed recentReleases() throws Exception {
    QueryBuilder qb =
        SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
            PubItemServiceDbImpl.INDEX_VERSION_STATE, State.RELEASED.name());
    SyndFeed feed =
        getBasicSyndFeed("Recent releases in repository", "Recent releases in repository", qb);
    return feed;
  }

  public SyndFeed recentReleasesforOrganizationalUnit(String ouId) throws Exception {
    BoolQueryBuilder qb = QueryBuilders.boolQuery();
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_VERSION_STATE, State.RELEASED.name()));
    List<String> ouIdsWithChild = organizationService.getChildIdPath(ouId);
    String[] indexes =
        new String[] {PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIER,
            PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIER};
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
        indexes, ouIdsWithChild.toArray(new String[] {})));

    SyndFeed feed =
        getBasicSyndFeed("Recent releases for organization " + ouId,
            "Recent releases for organization " + ouId, qb);
    return feed;
  }

  public SyndFeed recentReleasesSearchQuery(QueryBuilder givenQb) throws Exception {
    BoolQueryBuilder qb = QueryBuilders.boolQuery();
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_VERSION_STATE, State.RELEASED.name()));
    qb.must(givenQb);

    SyndFeed feed = getBasicSyndFeed("Search result as feed", givenQb.toString(), qb);
    return feed;
  }

  public SyndFeed recentReleasesOAPublications() throws Exception {
    BoolQueryBuilder qb = QueryBuilders.boolQuery();
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_VERSION_STATE, State.RELEASED.name()));
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_FILE_VISIBILITY, Visibility.PUBLIC.name()));
    qb.must(SearchUtils.baseElasticSearchQueryBuilder(pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_FILE_STORAGE, Storage.INTERNAL_MANAGED.name()));

    SyndFeed feed =
        getBasicSyndFeed("Recent Open Access Publications",
            "Feed for the Open Access Homepage of the MPG", qb);
    return feed;
  }

  private List<PubItemVO> search(QueryBuilder qb) throws Exception {
    SearchSourceBuilder ssb = new SearchSourceBuilder();
    ssb.size(50);
    ssb.query(qb);
    ssb.sort(PubItemServiceDbImpl.INDEX_LATESTRELEASE_DATE, SortOrder.DESC);

    SearchResponse resp = pubItemService.searchDetailed(ssb, null);

    List<PubItemVO> itemList =
        SearchUtils.getSearchRetrieveResponseFromElasticSearchResponse(resp, PubItemVO.class);

    return itemList;
  }

  private SyndFeed getBasicSyndFeed(String title, String description, QueryBuilder qb)
      throws Exception {
    SyndFeed feed = new SyndFeedImpl();
    feed.setTitle(title);
    feed.setDescription(description);
    feed.setCategories(getGeneralCategories());
    feed.setLanguage("en-US");

    feed.setEntries(transformToEntryList(search(qb)));
    return feed;
  }


  private List<SyndCategory> getGeneralCategories() {
    String categories = PropertyReader.getProperty("inge.feed.categories");
    List<SyndCategory> catList = new ArrayList<>();
    if (categories != null) {
      for (String cat : categories.split(",")) {
        SyndCategory syndCat = new SyndCategoryImpl();
        syndCat.setName(cat.trim());
      }
    }

    return catList;


  }

  /**
   * Transformation method takes ItemList XML and transforms it to the list of syndication entries (
   * <code><List>SyndEntry</code>)
   * 
   * @param itemListXml
   * @return <List>SyndEntry
   * @throws SyndicationException
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

          String crs =
              creator.getPerson() != null ? creator.getPerson().getFamilyName() + ", "
                  + creator.getPerson().getGivenName() : creator.getOrganization().getName();

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

      se.setLink(PropertyReader.getProperty("escidoc.pubman.instance.url") + "/pubman/item/"
          + pi.getLatestRelease().getObjectIdAndVersion());

      // Uri ????
      se.setUri(se.getLink());

      // Entry UpdatedDate ???
      se.setUpdatedDate(pi.getModificationDate());

      // Entry PublishedDate ???
      se.setPublishedDate(pi.getLatestRelease().getModificationDate());


      entries.add(se);

    }

    return entries;


  }

}
