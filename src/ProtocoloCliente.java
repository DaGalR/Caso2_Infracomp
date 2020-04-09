import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProtocoloCliente {
	
	public static void procesar(BufferedReader stdIn, BufferedReader pIn,PrintWriter pOut) throws IOException{
		
		String resServidor, resCliente;
		
		while(true) {
			
//			if(resServidor.equals("ERROR")) {
//			
//		}
			resServidor = pIn.readLine();
			System.out.println("Recibido: "+resServidor);
			System.out.println("Escriba el mensaje a enviar al servidor: ");
			resCliente=stdIn.readLine();
			System.out.println();
			
			System.out.println("Escriba el mensaje para enviar: ");
			String fromUser = stdIn.readLine();
			
			pOut.println(fromUser);
			
			String fromServer="";
			
			if((fromServer = pIn.readLine()) !=null) {
				System.out.println("Respuesta del servidor: " + fromServer);
			}
		}
	
	}
}
