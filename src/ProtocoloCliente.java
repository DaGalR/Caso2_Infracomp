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

import javax.crypto.BadPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
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
		String resServidor, resCliente, stringSerServidor,cifradoA,cifradoB, algHMACelegido,algAsimElegido, algSimElegido;
		resServidor ="";
		stringSerServidor="";
		cifradoA="";
		cifradoB="";
		algAsimElegido="";
		algSimElegido="";
		algHMACelegido="";
		KeyPairGenerator generator;
		KeyPair keyPair;
		int contadorProtocolo = 0;
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			keyPair = generator.generateKeyPair();
			PublicKey kPubServ=null;
			while(true) {
				
				

				if(resServidor != null || contadorProtocolo!=0) {
					System.out.println("RESPUESTA SERVIDOR " + resServidor);

				}
				
				System.out.println("Contador va en " + contadorProtocolo);	
				if(contadorProtocolo == 2 && algHMACelegido != null && algHMACelegido != "") {
					
					try {
						
						X509Certificate certificado = gc(keyPair,algHMACelegido);
						byte[] cerBytes = certificado.getEncoded();
						String cerString = DatatypeConverter.printBase64Binary(cerBytes);
						System.out.println("Generando su certificado de cliente... " + cerString);
						pOut.println(cerString);
						resServidor=pIn.readLine();
						
						if(resServidor.equals("OK")) {
							
							contadorProtocolo++;
							System.out.println("Recibiendo certificado del servidor...");
							resServidor=pIn.readLine();
							stringSerServidor = resServidor;
							
							System.out.println("Certificado servidor recibido para procesar... " + stringSerServidor);
							
							byte[] cerServByte = DatatypeConverter.parseBase64Binary(stringSerServidor);
							CertificateFactory cf = CertificateFactory.getInstance("X.509");
							X509Certificate c = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cerServByte));
							kPubServ = c.getPublicKey();
							
							boolean valido = verificarCertificado(c);
							
							if(valido) {
								
								System.out.println("El certificado recibido del servidor es válido, escriba 'OK' para continuar"); 
								resCliente=stdIn.readLine();
								pOut.println(resCliente);
								
								System.out.println("Recibiendo respuestas del servidor...");
								cifradoA = pIn.readLine();
								cifradoB = pIn.readLine();
								System.out.println("Recibidos dos cifrados, procesando...");
								byte[] k_sc = Cifrado.descifrar(keyPair.getPrivate(), "RSA", DatatypeConverter.parseBase64Binary(cifradoA), true);
								Key k_scPro = new SecretKeySpec(k_sc, 0, k_sc.length, algSimElegido );
								byte[] retoByte = Cifrado.descifrar(k_scPro, algSimElegido, DatatypeConverter.parseBase64Binary(cifradoB),false);
								String retoString = DatatypeConverter.printBase64Binary(retoByte);
								System.out.println("Reto descifrado "+ retoString);
								
								System.out.println("Cifrando y enviando el reto con llave del servidor...");
								System.out.println("llave publica de servidor: " + kPubServ);
								byte[] retoCifrado = Cifrado.cifrar(kPubServ, algAsimElegido, retoString, true);
								String retoCifradoString = DatatypeConverter.printBase64Binary(retoCifrado);

								System.out.println("Enviando reto cifrado de vuelta: "+ retoCifradoString);
								pOut.println(retoCifradoString);
								
								resServidor = pIn.readLine();
								System.out.println("Respuesta recibida de reto "+ resServidor);
								contadorProtocolo++;
								continue;
							}
							else {
								pOut.println("ERROR");
								
							}
							
							
						}
						else {
							System.out.println("Error del servidor: Su certificado no es válido");
							continue;
						}
						continue;
					} catch (Exception e) {
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
						algSimElegido=algs[1];
						algAsimElegido=algs[2];
						algHMACelegido = algs[3].substring(4, algs[3].length());
						System.out.println("ALGORITMO SIMETRICO ELEGIDO " + algSimElegido);
						System.out.println("ALGORITMO ASIMETRICO ELEGIDO " + algAsimElegido);
						System.out.println("ALGORITMO HMAC ELEGIDO " + algHMACelegido);

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
					System.out.println("Error detectado desde el servidor, intente escribir de nuevo su respuesta.");
					continue;
					
				}
				else if(resServidor.equals("OK")) {
					System.out.println("Respuesta aceptada");
					contadorProtocolo++;
				}
				
			}
		} catch (Exception e) {
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
