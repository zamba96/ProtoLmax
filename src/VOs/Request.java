package VOs;

/**
 * Requests: Modela los requests que se le pueden hacer al logic.
 * @author juandavid
 *
 */
public class Request {

	/*
	 * id del Espacio de interes del request
	 */
	private Long id;
	
	/*
	 * tipo del request, debe ser un elemento del siguiente conunto:
	 * {get, getTime, ocupar, desocupar, add}
	 */
	private String type;
	
	/**
	 * direccion del Espacio que se quiere agregar
	 */
	private String direccion;
	
	/**
	 * la respuesta que se le agrega al request
	 */
	private Response response;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return "Id: " + id + ", type: " + type;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
	
}
