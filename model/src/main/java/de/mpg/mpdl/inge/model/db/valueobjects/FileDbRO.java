package de.mpg.mpdl.inge.model.db.valueobjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.MappedSuperclass;

@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
@MappedSuperclass
public class FileDbRO extends BasicDbRO {

}
