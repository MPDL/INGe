package de.mpg.mpdl.inge.migration.beans;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.db.repository.YearbookRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsYearbookVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;

@Component
public class YearBookImportBean {

	static Logger log = Logger.getLogger(Migration.class.getName());

	@Value("${escidoc.url}")
	private String escidocUrl;
	@Value("${items.path}")
	private String itemsPath;
	@Value("${item.path}")
	private String itemPath;
	@Value("${maximum.records}")
	private int limit;
	@Autowired
	private YearbookRepository ybRepository;
	@Autowired
	private MigrationUtilBean utils;
	@Autowired
	private OrganizationService organizationService;

	public void importYearBooks() throws Exception {

		String contentModelId = "escidoc:748545";
		HttpClient client = utils.setup();

		URI uri = new URIBuilder(escidocUrl + itemsPath)
				.addParameter("query", "\"/properties/content-model/id\"=\"" + contentModelId + "\"")
				.addParameter("maximumRecords", String.valueOf(limit)).build();
		final HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);
		String xml = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

		SearchRetrieveResponseVO<PubItemVO> pubItemList = XmlTransformingService.transformToSearchRetrieveResponse(xml);

		pubItemList.getRecords().parallelStream().forEach(i -> getTheYearbook(i.getData()));

	}

	private void getTheYearbook(PubItemVO pubItemVo) {
		HttpClient client = null;
		try {
			client = utils.setup();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String objectId = pubItemVo.getVersion().getObjectId();
			log.info("Getting " + objectId);
			URI itemUri = new URIBuilder(escidocUrl + itemPath + "/" + objectId).build();
			final HttpGet requestItem = new HttpGet(itemUri);
			HttpResponse itemResponse = client.execute(requestItem);
			String itemXml = EntityUtils.toString(itemResponse.getEntity(), StandardCharsets.UTF_8);
			PubItemVO item = XmlTransformingService.transformToPubItem(itemXml);
			saveTheYearbook(item);

		} catch (Exception e) {
			log.error("FAILED Getting " + pubItemVo.getVersion().getObjectIdAndVersion(), e);
		}
	}

	private void saveTheYearbook(PubItemVO pubItem) {
		YearbookDbVO ybVo = null;
		try {
			ybVo = transformToYB(pubItem);
			log.info("Saving " + ybVo.getObjectId());
			ybRepository.save(ybVo);
		} catch (Exception e) {
			log.info("FAILED Saving " + ybVo.getObjectId());
			e.printStackTrace();
		}
	}

	private YearbookDbVO transformToYB(PubItemVO itemVo) throws Exception {
		AccountUserDbRO owner = new AccountUserDbRO();
		AccountUserDbRO modifier = new AccountUserDbRO();

		owner.setObjectId(utils.changeId("user", itemVo.getOwner().getObjectId()));

		modifier.setObjectId(utils.changeId("user", itemVo.getVersion().getModifiedByRO().getObjectId()));

		YearbookDbVO newYearbook = new YearbookDbVO();

		MdsYearbookVO ybMetaData = itemVo.getYearbookMetadata();

		OrganizationVO org = ybMetaData.getCreators().get(0).getOrganization();
		AffiliationDbVO organization = new AffiliationDbVO();
		organization = organizationService.get(utils.changeId("ou", org.getIdentifier()), null);
		newYearbook.setOrganization(organization);
		newYearbook.setYear(Integer.valueOf(ybMetaData.getYear()));
		newYearbook.setContextIds(ybMetaData.getIncludedContexts());
		// newYearbook.setName(ybMetaData.getTitle());
		newYearbook.setCreator(owner);
		newYearbook.setModifier(modifier);
		newYearbook.setObjectId(utils.changeId("yb", itemVo.getVersion().getObjectId()));
		if (itemVo.getVersion().getState().equals(ItemVO.State.PENDING)) {
			newYearbook.setState(YearbookDbVO.State.CREATED);
		} else {
			newYearbook.setState(newYearbook.getState().valueOf(itemVo.getVersion().getState().name()));
		}

		newYearbook.setCreationDate(itemVo.getCreationDate());
		newYearbook.setLastModificationDate(itemVo.getLatestVersion().getModificationDate());
		Set<String> itemIds = new HashSet<>();
		itemVo.getRelations()
				.forEach(relation -> itemIds.add(utils.changeId("item", relation.getTargetItemRef().getObjectId())));
		newYearbook.setItemIds(itemIds);
		return newYearbook;
	}

	public void importSingleYearbook(String id) {

		PubItemVO theItem = null;

		try {
			HttpClient client = utils.setup();

			URI uri = new URIBuilder(escidocUrl + itemPath + "/" + id).build();
			final HttpGet request = new HttpGet(uri);
			HttpResponse response = client.execute(request);
			String xml = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			log.info("Getting " + id);

			theItem = XmlTransformingService.transformToPubItem(xml);
			getTheYearbook(theItem);
		} catch (Exception e) {
			log.error("FAILED Getting " + id, e);
		}

	}

}
