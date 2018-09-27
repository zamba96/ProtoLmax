package VOs;

public class ErrorResponse extends Response{

	public ErrorResponse(String error) {
		super();
		type = "error";
		className = "ErrorResponse";
		
	}
	
}
