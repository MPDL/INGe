package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
@MappedSuperclass
public class FileDbRO extends BasicDbRO implements Serializable {

}
