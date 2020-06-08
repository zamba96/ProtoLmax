package logic;

/**
 * modela un usuario
 * @author juandavid
 *
 */
public class Usuario {

	/**
	 * id del usuario
	 */
	private Long id;
	
	/**
	 * nombre del usuario
	 */
	private String nombre;
	
	/**
	 * cedula del usuario
	 */
	private String cedula;

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

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the cedula
	 */
	public String getCedula() {
		return cedula;
	}

	/**
	 * @param cedula the cedula to set
	 */
	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
	
	
	
}
