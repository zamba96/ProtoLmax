package VOs;

public class ErrorResponse extends Response{

	private String message;
	public ErrorResponse(String error) {
		super();
		type = "error";
		className = "ErrorResponse";
		message = error;
		
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
}
