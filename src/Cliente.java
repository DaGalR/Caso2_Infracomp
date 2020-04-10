import java.io.BufferedReader;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
	public static final int PUERTO = 8080;
	public static final String SERVIDOR = "localhost";
	
	public static void main(String[] args) throws IOException{
		Socket socket = null;
		PrintWriter escritor = null;
		BufferedReader lector = null;
		System.out.println("Cliente...");
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		
//		FileOutputStream archivoUsuarios = new FileOutputStream("docs/usuarios.txt");
//		ObjectOutputStream escritorUsuarios = new ObjectOutputStream(archivoUsuarios);
//		System.out.println("�Desea ingresar o registrarse?");
//		
//		
//		String tipoIngreso = stdIn.readLine();
//		String  nombre = "";
//		String usuario ="";
//		String contrasenia="";
//		
//		if(tipoIngreso.equals("registrarse"))
//		{
//			System.out.println("�Cu�l es su nombre?");
//			nombre = stdIn.readLine();
//			
//			System.out.println("�Qu� usuario le gustar�a tener?");
//			usuario = stdIn.readLine();
//			
//			//TO DO
//			while(usuario.equals("prueba"))
//			{
//				System.out.println("Usuario no disponible, escoga otro usuario");
//				usuario = stdIn.readLine();
//			}
//			
//			System.out.println("�Escriba una contrase�a?");
//			contrasenia= stdIn.readLine();
//			
//			System.out.println("Repita su contrase�a");
//			while(!contrasenia.contentEquals(stdIn.readLine()))
//			{
//				System.out.println("Las contrase�as no coinciden, escriba una nueva contrase�a");
//				contrasenia= stdIn.readLine();
//				System.out.println("Repita su contrase�a");
//			}
//			
//			escritorUsuarios.writeChars(nombre);
//			escritorUsuarios.writeChars(usuario);
//			escritorUsuarios.writeChars(contrasenia);
//			escritorUsuarios.close();
//			
//		}


		
		
		try {
			socket = new Socket(SERVIDOR,PUERTO);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch(IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		ProtocoloCliente.procesar(stdIn,lector,escritor);
		
		stdIn.close();
		escritor.close();
		lector.close();
		socket.close();
	}
	
}
