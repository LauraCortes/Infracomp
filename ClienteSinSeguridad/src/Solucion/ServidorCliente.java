package Solucion;




import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;

import javax.crypto.Cipher;



import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import uniandes.gload.core.Task;

public class ServidorCliente extends Task
{
	
	public static String RTA_AFIRTMATIVA="RTA:OK";
	public static String ALGORITMO="RSA";
	public static String MAC="HMACMD5";

	int contador = 0;
	@Override
	public void fail() {
		
		try
		{
			
			contador ++;
			File cosa3 = new File("./tiempoSalida3.txt"); 
			FileWriter salida3 = new FileWriter(cosa3, true);
			salida3.write("\n" + contador);
			salida3.close();
			
		}catch(Exception e)
		{
			
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void success() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() 
	{
		
try 
{
	
	double NUM1=Math.random();
	 double NUM2;
	 long tiempoValidacion;
	 long tiempoRespuestaSolicitud;
	
	
		boolean ejecutar = true;
		Socket servidor = null;
		PrintWriter escritor = null;
		BufferedReader lector = null;
		Certificate certificadoServidor = null ;
		try 
		{
			servidor = new Socket("192.168.1.16", 443);
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
				fromUser ="ALGORITMOS:"+ALGORITMO+":"+MAC+":HmacSHA256";
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
					Certificado certificadoCliente = new Certificado();
					byte [] mybye = certificadoCliente.certificado.getEncoded();
					
					//ENVIO AL SERVIDOR DESDE EL CLIENTE
					servidor.getOutputStream().write(mybye);
					servidor.getOutputStream().flush();
					System.out.println("Cliente: " + "Se ha enviado el certificado ");
					
					System.out.println("Cliente: " + "Se inicia el conteo");

					long inicio = System.currentTimeMillis();

					if ((fromServer = lector.readLine()) != null  && fromServer.equals(RTA_AFIRTMATIVA))
					{
						//CERTIFICADO ACEPTADO
						System.out.println("Servidor: " + fromServer);
						
						//NUMERO ALEATORIO
						fromServer = lector.readLine();
						NUM2=Double.parseDouble(fromServer.split(":")[0]);
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
							String s3 = lector.readLine();

							
							
							if( s3 != "")
							{
								
								long acabo = System.currentTimeMillis();
								tiempoValidacion = (acabo - inicio );
								
						        System.out.println("Cliente: El tiempo de autentificacion fue de  "+ tiempoValidacion);

								File cosa = new File("./tiempoSalida1.txt"); 
								FileWriter salida = new FileWriter(cosa, true);
								salida.write("\n" + tiempoValidacion);
								salida.close();
								
								
								//ACEPTA EL CIFRADO DEL SERVIDOR
								fromUser =RTA_AFIRTMATIVA;
								System.out.println("Cliente: " + fromUser);
								escritor.println(fromUser);

								//CIFRA EL NUMERO 2
								
						        escritor.println(NUM2);
						        
								if ((fromServer = lector.readLine()) != null  &&fromServer.equals(RTA_AFIRTMATIVA))
								{		
									System.out.println("Servidor: "+fromServer);
									

									escritor.println("INIT:");
									
									long reporto = System.currentTimeMillis();

									
									System.out.println("Cliente: INIT:" +fromUser);
									
									byte[] keyBytes = certificadoServidor.getPublicKey().getEncoded();           

									String ordenes1 = "ordenes asd";

									escritor.println(ordenes1);
									escritor.println(ordenes1);


									

									
								
									
									
									System.out.println("Cliente: ordenes con mac: " +fromUser);

									fromServer = lector.readLine();
									if (fromServer.equals(RTA_AFIRTMATIVA))
									{
										
										
										long recibo = System.currentTimeMillis();
										
										tiempoRespuestaSolicitud = recibo - reporto;
										File cosa2 = new File("./tiempoSalida2.txt"); 
										FileWriter salida2 = new FileWriter(cosa2, true);
										salida2.write("\n" + tiempoRespuestaSolicitud);
										salida2.close();

										System.out.println("Cliente: Se demora " + tiempoRespuestaSolicitud);
										
										System.out.println("CORONAMOS");
										break;

									}
									

//									System.out.println("Jesus: "+fromUser);
								}
								else
								{
									System.out.println("Servidor: RTA:ERROR");
									break;
								}
							}
							else
							{
								//NO ACEPTA EL CIFRADO DEL SERVIDOR
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
	servidor.close();
	
	
} catch (Exception e) 
{
	
	
	try
	{
		
		contador ++;
		File cosa3 = new File("./tiempoSalida3.txt"); 
		FileWriter salida3 = new FileWriter(cosa3, true);
		salida3.write("\n" + contador);
		salida3.close();
		
	}catch(Exception j)
	{
		
	}
	
	
	
	// TODO: handle exception
}		
	}
	
}

