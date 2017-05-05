package de.mpg.mpdl.inge.db.repository;

import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.model_new.hibernate.MdsFileVOJsonUserType;
import de.mpg.mpdl.inge.model_new.hibernate.MdsPublicationVOJsonUserType;
import de.mpg.mpdl.inge.model_new.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model_new.valueobjects.PubItemVersionVO;
import de.mpg.mpdl.inge.model_new.valueobjects.VersionableId;



public interface OrganizationRepository extends JpaRepository<AffiliationVO, String> {

}
