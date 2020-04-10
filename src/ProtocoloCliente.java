import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.Certificate;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
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
		String resServidor, resCliente, stringSerServidor;
		resServidor ="";
		stringSerServidor="";
		int contadorProtocolo = 0;
		String algElegido="";
		KeyPairGenerator generator;
		KeyPair keyPair;
		
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			keyPair = generator.generateKeyPair();
			PublicKey kPubServ=null;
			while(true) {
				
				

				if(resServidor != null || contadorProtocolo!=0) {
					System.out.println("RESPUESTA SERVIDOR " + resServidor);

				}
				
				System.out.println("Contador va en " + contadorProtocolo);	
				if(contadorProtocolo == 2 && algElegido != null && algElegido != "") {
					
					try {
						
						X509Certificate certificado = gc(keyPair,algElegido);
						byte[] cerBytes = certificado.getEncoded();
						String cerString = DatatypeConverter.printBase64Binary(cerBytes);
						System.out.println("Certificado cliente " + cerString);
						pOut.println(cerString);
						resServidor=pIn.readLine();
						
						if(resServidor.equals("OK")) {
							
							contadorProtocolo++;
							resServidor=pIn.readLine();
							stringSerServidor = resServidor;
							
							System.out.println("Certificado servidor " + stringSerServidor);
							
							byte[] cerServByte = DatatypeConverter.parseBase64Binary(stringSerServidor);
							CertificateFactory cf = CertificateFactory.getInstance("X.509");
							X509Certificate c = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cerServByte));
							
							boolean valido = verificarCertificado(c);
							
							if(valido) {
								pOut.println("OK");
								contadorProtocolo++;
							}
							else {
								pOut.println("ERROR");
								
							}
							kPubServ = c.getPublicKey();
							
						}
						else {
							System.out.println("Error del servidor: Su certificado no es válido");
							continue;
						}
						continue;
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
						algElegido = algs[3].substring(4, algs[3].length());
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
		return new JcaX509CertificateConverter().setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()).getCertificate(x509CertificateHolder);
	}
	
	public static boolean verificarCertificado(X509Certificate certificado) {
		PublicKey llave = certificado.getPublicKey();
		try {
			certificado.verify(llave);
			return true;
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e) {
			System.out.println("Error verificando certificado " + e.getMessage());
			return false;

		}
	}
}
