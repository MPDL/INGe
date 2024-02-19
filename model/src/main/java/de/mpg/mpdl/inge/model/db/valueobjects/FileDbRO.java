package de.mpg.mpdl.inge.model.db.valueobjects;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.MappedSuperclass;

@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@MappedSuperclass
public class FileDbRO extends BasicDbRO {

}
