package VOs;

/**
 * Requests: Modela los requests que se le pueden hacer al logic.
 * @author juandavid
 *
 */
public class Request {

	/**
	 * id del request
	 */
	private Long id;
	
	/*
	 * id del Espacio de interes del request
	 */
	private Long espacioId;
	
	/*
	 * tipo del request, debe ser un elemento del siguiente conunto:
	 * {get, getTime, ocupar, desocupar, add, reserva}
	 */
	private String type;
	
	/**
	 * la respuesta que se le agrega al request
	 */
	private Response response;

	public Long getEspacioId() {
		return espacioId;
	}

	public void setEspacioId(Long id) {
		this.espacioId = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return "Id: " + espacioId + ", type: " + type;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
}
