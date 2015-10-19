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



import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

public class ServidorCliente 
{
	public static String RTA_AFIRTMATIVA="RTA:OK";
	
	private static double NUM1=2;
	private static double NUM2;
	
	public static void main(String[] args) throws Exception 
	{
		boolean ejecutar = true;
		Socket servidor = null;
		PrintWriter escritor = null;
		BufferedReader lector = null;
		Certificate certificadoServidor = null ;
		try 
		{
			servidor = new Socket("localhost", 443);
			escritor = new PrintWriter(servidor.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(
					servidor.getInputStream()));
		} 
		catch (Exception e) 
		{
			System.err.println("Exception: " + e.getMessage());
			System.exit(1);
		}
		
		String fromServer;
		String fromUser;
		
		//SE EJECUTA MIENTRAS EL SERVIDOR ESTE ACTIVO
		while (ejecutar) 
		{
			//ETAPA 1: SELECCIONAR ALGORITMOS E INICIAR SESION
			fromUser ="INFORMAR";
			System.out.println("Cliente: " + fromUser);
			escritor.println(fromUser);
			fromUser = "";
			
			if ((fromServer = lector.readLine()) != null)
			{
				System.out.println("Servidor: " + fromServer);

				//AlGORTIMOS QUE SE VAN A UTILIZAR
				fromUser ="ALGORITMOS:RSA:HMACMD5";
				System.out.println("Cliente: " + fromUser);
				escritor.println(fromUser);
				fromUser = "";
				if((fromServer = lector.readLine())!= null && fromServer.equals(RTA_AFIRTMATIVA))
				{
					//ALGORITMOS ACEPTADOS
					System.out.println("Servidor: " + fromServer);

					//ETAPA 2: INTERCAMBIO DE CERTIFICADOS
					fromUser = NUM1 + ":CERTPA";
					System.out.println("Cliente: " + fromUser);
					escritor.println(fromUser);

					//FLUJO DE BYTES DEL CERTIFICADO
					Certificado enviar = new Certificado();
					byte [] mybye = enviar.certificado.getEncoded();
					
					//ENVIO AL SERVIDOR DESDE EL CLIENTE
					servidor.getOutputStream().write(mybye);
					servidor.getOutputStream().flush();
					System.out.println("Cliente: " + "Se ha enviado el certificado ");

					if ((fromServer = lector.readLine()) != null  && fromServer.equals(RTA_AFIRTMATIVA))
					{
						//CERTIFICADO ACEPTADO
						System.out.println("Servidor: " + fromServer);
						
						//NUMERO ALEATORIO
						fromServer = lector.readLine();
						NUM2=Double.parseDouble(fromServer.split(":")[0]);
						System.out.println("Num2: "+NUM2);
						System.out.println("Servidor: " + fromServer);

						//RECIBE CERTIFICADO DEL SERVIDOR
						CertificateFactory certificadoLLegada;
						try 
						{
							certificadoLLegada = CertificateFactory.getInstance("X.509");
							byte[] arr = new byte[1024];
							InputStream soc = servidor.getInputStream();
							soc.read(arr);
							InputStream input = new ByteArrayInputStream(arr);
							certificadoServidor = certificadoLLegada.generateCertificate(input);
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}
						System.out.println("Servidor: " + "Le envio mi Certificado");

						//VERIFICA CERTIFICADO SERVIDOR
						if (certificadoServidor != null)
						{
							//ACEPTA CERTIFICADO SERVIDOR
							fromUser =RTA_AFIRTMATIVA;
							System.out.println("Cliente: " + fromUser);
							escritor.println(fromUser);
							
							//ETAPA 3: AUTENTICACION DEL CLIENTE Y EL SERVIDOR
							String privateKEY = lector.readLine();
							System.out.println("Servidor: " + privateKEY);

							
							if(privateKEY!=null)
							{
								//ACEPTA LLAVE SERVIDOR
								fromUser =RTA_AFIRTMATIVA;
								System.out.println("Cliente: " + fromUser);
								escritor.println(fromUser);
								
								
								Cipher cifrado = Cipher.getInstance("RSA");
								cifrado.init(Cipher.ENCRYPT_MODE, enviar.getPriv());

								byte [] envio1 = (NUM1 + "").getBytes();
							    byte[] funciona =  cifrado.doFinal(envio1);
							    String s2 = new String (funciona);

								System.out.println("Cliente: Este es el mensaje cifrado " + s2);

								escritor.println(s2);

								
								//Etapa 4
								
								if ((fromServer = lector.readLine()) != null  )
								{
									
									
									System.out.println("Cliente: Este es el mensaje cifrado " + s2);


								}
								else
								{
									System.out.println("Paila");

									break;

								}
							    
							}
							else
							{
								//NO ACEPTA LA LLAVE DEL SERVIDOR
								fromUser ="RTA:ERROR";
								System.out.println("Error  Cliente: " + fromUser);
								escritor.println(fromUser);
								break;
							}
						}
						else
						{
							//NO ACEPTA EL CERTIFICADO DEL SERVIDOR
							fromUser ="RTA:ERROR";
							System.out.println("Cliente: " + fromUser);
							escritor.println(fromUser);
							break;
						}
					}
					else
					{
						if(fromServer!=null)
							System.out.println("Certificado rechazado:"+" "+fromServer);
						else
							System.out.println("El servidor no responde");
						
						break;
					}
				}
				else
				{
					if(fromServer!=null)
						System.out.println("Algoritmos rechazados:"+" "+fromServer);
					else
						System.out.println("El servidor no responde");
					
					break;
				}
			}
			else
			{
				System.out.println("No empieza la conexion");
				break;
			}
		}
		escritor.close();
		lector.close();
	}
}
