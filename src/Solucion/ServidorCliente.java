package Solucion;



import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;



import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

public class ServidorCliente 
{

	
	 public static void main(String[] args) throws Exception {

    boolean ejecutar = true;
	 Socket jesus = null;
	 PrintWriter escritor = null;
	 BufferedReader lector = null;
	 Certificate certificadoServidor = null ;
	 
	 try {
		 jesus = new Socket("localhost", 443);
	 escritor = new PrintWriter(jesus.getOutputStream(), true);
	 lector = new BufferedReader(new InputStreamReader(
	 jesus.getInputStream()));
	 } catch (Exception e) {
	 System.err.println("Exception: " + e.getMessage());
	 System.exit(1);
	 }
	 BufferedReader stdIn = new BufferedReader(
	 new InputStreamReader(System.in));
	 String fromServer;
	 String fromUser;
	 while (ejecutar) {

		 fromUser ="INFORMAR";
		 System.out.println("Cliente: " + fromUser);
     	 escritor.println(fromUser);
     	fromUser = "";
	 if ((fromServer = lector.readLine()) != null)
	 {
	 System.out.println("Servidor: " + fromServer);
	 
	 //Algoritmos que se van a utilizar
	 fromUser ="ALGORITMOS:RSA:HMACMD5";
	 System.out.println("Cliente: " + fromUser);
 	 escritor.println(fromUser);
 	fromUser = "";
 	 if ((fromServer = lector.readLine()) != null)
	 {
	 System.out.println("Servidor: " + fromServer);
	 
	 //Inicia la primer etapa
	 
	
	   fromUser = 2 + ":CERTPA";
		 System.out.println("Cliente: " + fromUser);

	 	 escritor.println(fromUser);
	 	 
	    Certificado enviar = new Certificado();
	 	byte [] mybye = enviar.certificado.getEncoded();

		
			jesus.getOutputStream().write(mybye);
			jesus.getOutputStream().flush();
			 System.out.println("Cliente: " + "Se ha enviado el certificado ");

		 
	 	 
	 	if ((fromServer = lector.readLine()) != null  && fromServer.equals("RTA:OK"))
		 {
	 	   System.out.println("Servidor: " + fromServer);
	 	  fromServer = lector.readLine();
	 	   System.out.println("Servidor: " + fromServer);

	 	 
	 		CertificateFactory certificadoLLegada;
			try {
				certificadoLLegada = CertificateFactory.getInstance("X.509");
				byte[] arr = new byte[1024];
				InputStream soc = jesus.getInputStream();
				soc.read(arr);
				InputStream input = new ByteArrayInputStream(arr);
				certificadoServidor = certificadoLLegada.generateCertificate(input);

			} catch (Exception e) {
				e.printStackTrace();
			}

						
			 System.out.println("Servidor: " + "Le envio mi Certificado");
                 
			 //Etapa 2 autentificacion del cliente y servidor
			 if (certificadoServidor != null)
			 {
				 fromUser ="RTA:OK";
				 System.out.println("Cliente: " + fromUser);
			 	 escritor.println(fromUser);
			 	 String privateKEY = lector.readLine();
			 	 
			 	Cipher cifrado = Cipher.getInstance("RSA");
				cifrado.init(Cipher.DECRYPT_MODE, enviar.getPriv());
				
			
				
				
				
				
				
				
				
				

			 }
			 else
			 {
				 break;
			 }
			 
	 	  
	 		
	 		
	 		
		 }
	 	else
	 	{
	 		break;
	 	}

	 }
 	 else
 	 {
 		 break;
 	 }
 	 
 	 
	 
	 }
	 else
	 {
		 break;
	 }
	 }
	 escritor.close();
	 lector.close();
	    }
	
	


	
}
