package VOs;

public class TimeResponse extends Response{

	private long minutos;

	public long getMinutos() {
		return minutos;
	}

	public void setMinutos(long minutos) {
		this.minutos = minutos;
	}
	
	public TimeResponse() {
		super();
	}
	
	public String toString() {
		return super.toString() + ", minutos: " + minutos;
	}
}
