

import java.security.Key;

import javax.crypto.*;

public class Cifrado 
{
	
	public static byte[] cifrar(Key llave, String algoritmo, String texto )
	{
		try
		{
		byte [] textoCifrado;
		Cipher cifrador = Cipher.getInstance(algoritmo);
		byte[] textoBytes= texto.getBytes();
		
		cifrador.init(Cipher.ENCRYPT_MODE, llave);
		textoCifrado = cifrador.doFinal(textoBytes);
		return textoCifrado;
		}
		catch(Exception e)
		{
			System.out.println("Excepcion: " + e.getMessage());
			return null;
		}
	}
	
	public static byte[] descifrar(Key llave, String algoritmo, byte[] texto )
	{
		try
		{
		byte [] textoBytes;
		Cipher cifrador = Cipher.getInstance(algoritmo);
		cifrador.init(Cipher.DECRYPT_MODE, llave);
		textoBytes = cifrador.doFinal(texto);
		return textoBytes;
		}
		catch(Exception e)
		{
			System.out.println("Excepcion: " + e.getMessage());
			return null;
		}
	}


}
