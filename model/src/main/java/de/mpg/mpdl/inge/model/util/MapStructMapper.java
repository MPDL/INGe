package de.mpg.mpdl.inge.model.util;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BasicDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.FileRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;
import org.mapstruct.ValueMapping;
import org.mapstruct.control.DeepClone;

@Mapper(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, mappingControl = DeepClone.class,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class MapStructMapper {

  /* 1. Cloning methods for Database VOs */

  public abstract void updateItemVersionVO(ItemVersionVO source, @MappingTarget ItemVersionVO target);

  public abstract void updateFileDbVO(FileDbVO source, @MappingTarget FileDbVO fileDbVO);

  public abstract void updateContextDbVO(ContextDbVO source, @MappingTarget ContextDbVO target);

  public abstract void updateAffiliationDbVO(AffiliationDbVO source, @MappingTarget AffiliationDbVO target);

  public abstract void updateAccountUserDbVO(AccountUserDbVO source, @MappingTarget AccountUserDbVO target);

  @SubclassMapping(source = ContextDbVO.class, target = ContextDbVO.class)
  public abstract ContextDbRO map(ContextDbRO basicRo);

  //@SubclassMapping(source = AffiliationDbVO.class, target = AffiliationDbVO.class)
  //public abstract AffiliationDbRO map(AffiliationDbRO basicRo);

  @SubclassMapping(source = FileDbVO.class, target = FileDbVO.class)
  public abstract FileDbRO map(FileDbRO basicRo);

  /* 2. New DB VOs to old VOs and vice versa */

  @Mappings({ //
      @Mapping(source = "publicStatus", target = "object.publicState"), //
      @Mapping(source = "pid", target = "object.objectPid"), //
      @Mapping(source = "owner.objectId", target = "object.creator.objectId"), //
      @Mapping(source = "owner.title", target = "object.creator.name"), //
      @Mapping(source = "context.objectId", target = "object.context.objectId"), //
      @Mapping(source = "context.title", target = "object.context.name"), //
      @Mapping(source = "creationDate", target = "object.creationDate"), //
      @Mapping(source = "latestVersion", target = "object.latestVersion"), //
      @Mapping(source = "latestRelease", target = "object.latestRelease"), //
      @Mapping(source = "localTags", target = "object.localTags"), //
      @Mapping(source = "metadata", target = "metadata"), //
      @Mapping(source = "files", target = "files")})
  public abstract ItemVersionVO toItemVersionVO(PubItemVO pubItemVO);

  @InheritInverseConfiguration
  public abstract PubItemVO toPubItemVO(ItemVersionVO itemVersionVO);

  @AfterMapping
  protected void fillVersion(ItemVersionVO itemVersionVO, @MappingTarget PubItemVO pubItemVO) {
    pubItemVO.setVersion(toItemRO(itemVersionVO));
  }

  @Mappings({@Mapping(source = "versionNumber", target = "versionNumber"), //Set version number before objectId to enable version_based ids
      @Mapping(source = "objectId", target = "objectId"), //
      @Mapping(source = "modificationDate", target = "modificationDate"), //
      @Mapping(source = "state", target = "versionState"), //
      @Mapping(source = "pid", target = "versionPid"), //
      @Mapping(source = "modifiedByRO.objectId", target = "modifier.objectId"), //
      @Mapping(source = "modifiedByRO.title", target = "modifier.name")})
  public abstract ItemVersionRO toItemVersionRO(ItemRO itemRO);

  @InheritInverseConfiguration
  @Mapping(target = "objectIdAndVersion", ignore = true)
  public abstract ItemRO toItemRO(ItemVersionRO itemRO);

  @Mappings({ //
      @Mapping(source = "reference.objectId", target = "objectId"), //
      @Mapping(source = "name", target = "name"), //
      @Mapping(source = "createdByRO.objectId", target = "creator.objectId"), //
      @Mapping(source = "createdByRO.title", target = "creator.name"), //
      @Mapping(source = "lastModificationDate", target = "lastModificationDate"), //
      @Mapping(source = "creationDate", target = "creationDate"), //
      @Mapping(source = "defaultMetadata", target = "metadata"), //
      @Mapping(source = "defaultMetadata.size", target = "size"), //
      @Mapping(source = "contentCategory", target = "metadata.contentCategory"), //
      @Mapping(source = "description", target = "metadata.description")})
  public abstract FileDbVO toFileDbVO(FileVO fileVO);

  @InheritInverseConfiguration
  public abstract FileVO toFileVO(FileDbVO fileDbVO);

  @Mappings({ //
      @Mapping(source = "objectId", target = "objectId"), //
      @Mapping(source = "title", target = "name")})
  @SubclassMappings({ //
      @SubclassMapping(source = FileRO.class, target = FileDbRO.class), //
      @SubclassMapping(source = AffiliationRO.class, target = AffiliationDbRO.class), //
      @SubclassMapping(source = ContextRO.class, target = ContextDbRO.class)})
  public abstract BasicDbRO toBasicDbRO(ReferenceObject referenceObject);

  @InheritInverseConfiguration
  @SubclassMappings({ //
      @SubclassMapping(source = FileDbRO.class, target = FileRO.class), //
      @SubclassMapping(source = AffiliationDbRO.class, target = AffiliationRO.class), //
      @SubclassMapping(source = ContextDbRO.class, target = ContextRO.class),})
  public abstract ReferenceObject toReferenceObject(BasicDbRO referenceObject);

  @Mappings({ //
      @Mapping(source = "name", target = "name"), @Mapping(source = "creator.objectId", target = "creator.objectId"), //
      @Mapping(source = "creator.title", target = "creator.name"), //
      @Mapping(source = "modifiedBy.objectId", target = "modifier.objectId"), //
      @Mapping(source = "modifiedBy.title", target = "modifier.name"), //
      @Mapping(source = "lastModificationDate", target = "lastModificationDate"), //
      @Mapping(source = "state", target = "state"), //
      @Mapping(source = "adminDescriptor.allowedGenres", target = "allowedGenres"), //
      @Mapping(source = "adminDescriptor.allowedSubjectClassifications", target = "allowedSubjectClassifications"), //
      @Mapping(source = "adminDescriptor.workflow", target = "workflow"), //
      @Mapping(source = "adminDescriptor.contactEmail", target = "contactEmail")})
  public abstract ContextDbVO toContextDbVO(ContextVO contextVO);

  @InheritInverseConfiguration
  public abstract ContextVO toContextVO(ContextDbVO contextDbVO);

  @Mappings({ //
      @Mapping(source = "creator.objectId", target = "creator.objectId"), //
      @Mapping(source = "creator.title", target = "creator.name"), //
      @Mapping(source = "modifiedBy.objectId", target = "modifier.objectId"), //
      @Mapping(source = "modifiedBy.title", target = "modifier.name"), //
      @Mapping(source = "lastModificationDate", target = "lastModificationDate"), //
      @Mapping(source = "defaultMetadata", target = "metadata"), //
      @Mapping(source = "parentAffiliations", target = "parentAffiliation", qualifiedByName = "firstElement"), //
      @Mapping(source = "predecessorAffiliations", target = "predecessorAffiliations"), //
      @Mapping(source = "publicStatus", target = "publicStatus")})
  public abstract AffiliationDbVO toAffiliationDbVO(AffiliationVO affiliationVO);

  @InheritInverseConfiguration
  @Mapping(source = "parentAffiliation", target = "parentAffiliations", qualifiedByName = "toList")
  public abstract AffiliationVO toAffiliationVO(AffiliationDbVO affiliationDbVO);

  @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
  public abstract FileDbVO.Visibility map(FileVO.Visibility vis);

  @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
  public abstract FileVO.Visibility map(FileDbVO.Visibility vis);

  @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
  public abstract FileDbVO.Storage map(FileVO.Storage vis);

  @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
  public abstract FileVO.Storage map(FileDbVO.Storage vis);

  @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
  public abstract FileDbVO.ChecksumAlgorithm map(FileVO.ChecksumAlgorithm vis);

  @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
  public abstract FileVO.ChecksumAlgorithm map(FileDbVO.ChecksumAlgorithm vis);

  @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
  public abstract AffiliationDbVO.State map(String state);

  public abstract String map(AffiliationDbVO.State state);

  @Named("firstElement")
  public AffiliationDbRO firstElement(List<AffiliationRO> value) {
    if (null == value)
      return null;
    if (value.isEmpty())
      return null;
    return (AffiliationDbRO) toBasicDbRO(value.get(0));
  }

  @Named("toList")
  public List<AffiliationRO> toList(AffiliationDbRO value) {
    if (null == value)
      return null;
    List<AffiliationRO> affList = new ArrayList<>();
    affList.add((AffiliationRO) toReferenceObject(value));
    return affList;
  }

  int map(long value) {
    return Long.valueOf(value).intValue();
  }
}
