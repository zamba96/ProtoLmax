package VOs;


/**
 * Clase que modela un mensaje que viene del servidor o sale del servidor
 * @author juandavid
 *
 */
public class MensajeControl {

	private String serverId;
	
	private String type;
	
	private String param;

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}
	

	
}
