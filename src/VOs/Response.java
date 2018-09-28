package VOs;

import logic.Parqueadero;

public class Response {
	
	/**
	 * el id del request al cual corresponde este response
	 */
	private Long id;

	/**
	 * tipo del response
	 */
	protected String type;
	
	/**
	 * parqueadero del response
	 */
	private Parqueadero espacio;
	
	/**
	 * nombre de la clase o subclase del response
	 */
	protected String className;
	
	public Response() {
		type = "Response";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Parqueadero getEspacio() {
		return espacio;
	}

	public void setEspacio(Parqueadero espacio) {
		this.espacio = espacio;
	}
	
	public String toString() {
		return "Tipo: " + type + ", Espacio: " + espacio;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getIdUsuario() {
		// TODO Auto-generated method stub
		return null;
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
