package VOs;

import logic.Espacio;

public class Response {

	private String type;
	
	private Espacio espacio;
	
	private String className;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Espacio getEspacio() {
		return espacio;
	}

	public void setEspacio(Espacio espacio) {
		this.espacio = espacio;
	}
	
	public String toString() {
		return "Tipo: " + type + ", \nEspacio: " + espacio;
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
	
}
