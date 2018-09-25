package exceptions;

public class EspacioNotFoundException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EspacioNotFoundException(String message) {
		super(message);
	}
	
	public EspacioNotFoundException() {
		super();
	}

}
