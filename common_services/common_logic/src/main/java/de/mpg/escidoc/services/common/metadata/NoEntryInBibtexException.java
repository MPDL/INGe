package de.mpg.escidoc.services.common.metadata;

public class NoEntryInBibtexException extends Exception {

	public NoEntryInBibtexException() {
	}

	public NoEntryInBibtexException(String message) {
		super(message);
	}

	public NoEntryInBibtexException(Throwable cause) {
		super(cause);
	}

	public NoEntryInBibtexException(String message, Throwable cause) {
		super(message, cause);
	}

}
