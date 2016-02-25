package se.altrusoft.docserv.converter;

public class UnsuportedConversionException extends Exception {

	private static final long serialVersionUID = -5170559818200700487L;

	public UnsuportedConversionException() {
		super();
	}

	public UnsuportedConversionException(String message) {
		super(message);
	}

	public UnsuportedConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsuportedConversionException(Throwable cause) {
		super(cause);
	}
}