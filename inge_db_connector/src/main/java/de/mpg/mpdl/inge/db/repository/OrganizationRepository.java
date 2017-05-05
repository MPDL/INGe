package de.mpg.mpdl.inge.db.repository;

import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.db.model.hibernate.MdsFileVOJsonUserType;
import de.mpg.mpdl.inge.db.model.hibernate.MdsPublicationVOJsonUserType;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.VersionableId;



public interface OrganizationRepository extends JpaRepository<AffiliationDbVO, String> {

}
