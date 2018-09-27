package disruptors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import VOs.ErrorResponse;
import VOs.Request;
import VOs.Response;
import logic.Espacio;

public class PostgreSQLDisruptor extends Thread {

	OutBuffer buffer;
	boolean sigue;






	private final String url = "jdbc:postgresql://192.168.56.1:5432/myDB";
	private final String user = "Python_User";
	private final String password = "1234";


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
