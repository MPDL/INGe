package de.mpg.mpdl.inge.model.db.valueobjects;

import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
@MappedSuperclass
public class FileDbRO extends BasicDbRO {

}
