import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProtocoloCliente {
	
	public static void procesar(BufferedReader stdIn, BufferedReader pIn,PrintWriter pOut) throws IOException{
		
		String[] algs;
		String resServidor, resCliente;
		resServidor ="";		
		
		while(true) {
			
			System.out.println("entro while antes de primer if");
			
			System.out.println("Escriba el mensaje a enviar al servidor: ");
			resCliente=stdIn.readLine();
			
			if(resCliente.equals("HOLA")) {
				pOut.println(resCliente);
			}
			else if(resCliente.contains("ALGORITMOS:")) {
				algs = resCliente.split(":");
				if(algs.length == 4) {
					resCliente.trim();
					pOut.println(resCliente);
				}
				else {
					System.out.println("Debe enviar una cadena del estilo ALGORITMOS:ALG1:ALG2:ALG3");
					continue;
				}
			}
			else {
				System.out.println("Error detectado en su mensaje, intente escribir de nuevo siguiendo el protocolo.");
				continue;
			}
			resServidor = pIn.readLine();

			if(resServidor.equals("ERROR")) {
				System.out.println("Error detectado en servidor, intente escribir de nuevo su respuesta.");
				continue;
				
			}
			else if(resServidor.equals("OK")) {
				System.out.println("Respuesta aceptada");
			}
			
		}
		
	
	}
}
