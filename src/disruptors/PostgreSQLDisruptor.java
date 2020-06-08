package disruptors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import VOs.Response;


/**
 * 
 * @author David Patino
 *
 */
public class PostgreSQLDisruptor extends Thread {

	OutBuffer buffer;
	boolean sigue;






	private final String url = "jdbc:postgresql://172.24.41.149:5432/nidoo_db";
	private final String user = "postgres";
	private final String password = "";


	public PostgreSQLDisruptor(OutBuffer buffer) {
		this.buffer=buffer;
		sigue=true;	
	}


	public Connection setConnection() throws SQLException {
		return DriverManager.getConnection(url,user,password);
	}

	public void run() {

		
		while(sigue) {
			
			Response req=buffer.getNextSlotPSQL();
			try {

				Connection conn = setConnection();
				switch(req.getType()) {
				//Get
				case "get":
					
					
					break;
				//GetTime
				case "getTime":

					break;
				//Ocupar
				case "ocupar":
					
					
					String SQL="INSERT INTO parqueadero_reservas(idUsuario,fecha,hora,idParqueadero) "
			                + "VALUES(?,?)";
					PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS); 
			 
			        pstmt.setString(1, req.getIdUsuario());
			            
			        pstmt.executeUpdate();
			            
					
					break;
				//desocupar
				case "desocupar":
					
					break;
				//agregar
				case "add":
					
				}

			} catch (Exception e) {
				// TODO: handle exception
			}


		}
	}

}
