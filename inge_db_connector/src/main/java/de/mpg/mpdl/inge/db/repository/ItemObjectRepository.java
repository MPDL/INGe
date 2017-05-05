package de.mpg.mpdl.inge.db.repository;

import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model_new.hibernate.MdsFileVOJsonUserType;
import de.mpg.mpdl.inge.model_new.hibernate.MdsPublicationVOJsonUserType;
import de.mpg.mpdl.inge.model_new.valueobjects.PubItemObjectVO;
import de.mpg.mpdl.inge.model_new.valueobjects.PubItemVersionVO;
import de.mpg.mpdl.inge.model_new.valueobjects.VersionableId;



public interface ItemObjectRepository extends JpaRepository<PubItemObjectVO, String> {



}
