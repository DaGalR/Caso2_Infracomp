import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import javax.crypto.KeyGenerator;
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class ProtocoloCliente {
	
	public static void procesar(BufferedReader stdIn, BufferedReader pIn,PrintWriter pOut) throws IOException{
		
		String[] algs;
		String resServidor, resCliente;
		resServidor ="";		
		int contadorProtocolo = 0;
		String algElegido="";
		KeyPairGenerator generator;
		KeyPair keyPair;
		
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			keyPair = generator.generateKeyPair();
			
			while(true) {
				System.out.println("Contador va en " + contadorProtocolo);	
				if(contadorProtocolo == 2 && algElegido != null && algElegido != "") {
					System.out.println("entro if pro");
					try {
						X509Certificate certificado = gc(keyPair,"SHA512");
						byte[] cerBytes = certificado.getEncoded();
						String cerString = DatatypeConverter.printBase64Binary(cerBytes);
						pOut.println(cerString);
					} catch (OperatorCreationException | CertificateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				System.out.println("Escriba el mensaje a enviar al servidor: ");
				resCliente=stdIn.readLine();
				
				
				
				if(resCliente.equals("HOLA")) {
					pOut.println(resCliente);
				}
				else if(resCliente.contains("ALGORITMOS:")) {
					algs = resCliente.split(":");
					if(algs.length == 4) {
						algElegido = algs[3];
						System.out.println("ALGORITMO ELEGIDO " + algElegido);
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
					contadorProtocolo++;
				}
				
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		
		
	}
	
	public static X509Certificate gc(KeyPair keyPair, String alg) throws OperatorCreationException, CertificateException{
		
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.add(Calendar.YEAR, 10);
		X509v3CertificateBuilder x509v3CertificateBuilder = new X509v3CertificateBuilder(new X500Name("CN=localhost"), BigInteger.valueOf(1), 		Calendar.getInstance().getTime(), endCalendar.getTime(), new X500Name("CN=localhost"), 		SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));
		ContentSigner contentSigner = new JcaContentSignerBuilder(alg+"withRSA").build(keyPair.getPrivate());
		X509CertificateHolder x509CertificateHolder = x509v3CertificateBuilder.build(contentSigner);
		return new JcaX509CertificateConverter().setProvider(new 		org.bouncycastle.jce.provider.BouncyCastleProvider()).getCertificate(x509CertificateHolder);
	}
}
