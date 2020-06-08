package VOs;

import java.util.Collection;

import logic.Parqueadero;

public class ListResponse extends Response{

	private Collection<Parqueadero> parqueaderos;
	
	public ListResponse() {
		className = "ListResponse";
	}

	/**
	 * @return the parqueaderos
	 */
	public Collection<Parqueadero> getParqueaderos() {
		return parqueaderos;
	}

	/**
	 * @param parqueaderos the parqueaderos to set
	 */
	public void setParqueaderos(Collection<Parqueadero> collection) {
		parqueaderos = collection;
	}
	
}
