package de.mpg.mpdl.inge.migration.beans;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.rometools.rome.feed.rss.Source;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

public class MetadataCleanup {

  public static MdsPublicationVO purge(MdsPublicationVO item_metadata) {

    List<IdentifierVO> identifiers = item_metadata.getIdentifiers();
    pimpIds(identifiers);
    List<CreatorVO> creators = item_metadata.getCreators();
    pimpCreators(creators);
    List<SourceVO> sources = item_metadata.getSources();
    sources.forEach(source -> {
      List<IdentifierVO> ids = source.getIdentifiers();
      pimpIds(ids);
      List<CreatorVO> src_creators = source.getCreators();
      pimpCreators(src_creators);
    });
    return item_metadata;
  }

  private static List<IdentifierVO> pimpIds(List<IdentifierVO> ids2pimp) {

    ids2pimp.stream().forEach(id -> {
      if (id.getType() != null && id.getType().name().equals(IdentifierVO.IdType.CONE.name())) {
        String cone_id = id.getId();
        if (!cone_id.isEmpty()) {
          id.setId(cone_id.substring(cone_id.lastIndexOf("cone") + 4));
        }
      }
    });
    return ids2pimp;

  }

  private static List<CreatorVO> pimpCreators(List<CreatorVO> list2pimp) {

    list2pimp.stream().forEach(creator -> {
      if (creator.getType().name().equals(CreatorVO.CreatorType.ORGANIZATION.name())) {
        if (creator.getOrganization().getIdentifier() != null) {
          String ou_id = creator.getOrganization().getIdentifier();
          creator.getOrganization().setIdentifier(ou_id.replaceAll("escidoc:", "ou_"));
        } else {
          // creator.getOrganization().setIdentifier("ou_persistent22");
        }
      } else {
        try {
          if (creator.getPerson().getIdentifier() != null) {
            if (creator.getPerson().getIdentifier().getType().equals(IdentifierVO.IdType.CONE)) {
              String pers_id = creator.getPerson().getIdentifier().getId();
              if (!pers_id.isEmpty()) {
                creator.getPerson().getIdentifier().setId(pers_id.substring(pers_id.lastIndexOf("cone") + 4));
              } else {
                creator.getPerson().setIdentifier(null);
              }
            }
          }
          if (creator.getPerson().getOrganizationsSize() > 0) {
            creator.getPerson().getOrganizations().stream().forEach(ou -> {
              String ou_id = ou.getIdentifier();
              ou.setIdentifier(ou_id.replaceAll("escidoc:", "ou_"));
            });
          }
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });
    return list2pimp;
  }

}
