package Solucion;




import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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

public class ServidorCliente 
{
	public static String RTA_AFIRTMATIVA="RTA:OK";
	public static String ALGORITMO="RSA";
	public static String MAC="HMACMD5";
	private static double NUM1=Math.random();
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
							String privateKEY = lector.readLine();
							System.out.println("Servidor: " + privateKEY);

							//DESCIFRA EL NUMERO QUE ENVIO EL SERVIDOR
							Cipher desCifrado = Cipher.getInstance(ALGORITMO);
							desCifrado.init(Cipher.DECRYPT_MODE, certificadoServidor.getPublicKey());
							byte[] cipheredText = Transformacion.destransformar(privateKEY);
							byte [] clearText = desCifrado.doFinal(cipheredText);
							String s3 = new String(clearText);
							double respuestaServidor=Double.parseDouble(s3);
							
							if(privateKEY!=null && NUM1==respuestaServidor)
							{
								//ACEPTA EL CIFRADO DEL SERVIDOR
								fromUser =RTA_AFIRTMATIVA;
								System.out.println("Cliente: " + fromUser);
								escritor.println(fromUser);

								//CIFRA EL NUMERO 2
								Cipher cipher = Cipher.getInstance(ALGORITMO);
								byte [] num2 = (String.valueOf(NUM2)).getBytes();
								cipher.init(Cipher.ENCRYPT_MODE, certificadoCliente.getPriv());
								byte [] cipheredNum2 = cipher.doFinal(num2);
								fromUser=Transformacion.transformar(cipheredNum2);
						        System.out.println("Cliente: "+fromUser);
						        escritor.println(fromUser);
						        
								if ((fromServer = lector.readLine()) != null  &&fromServer.equals(RTA_AFIRTMATIVA))
								{		
									System.out.println("Servidor: "+fromServer);
									
									//ETAPA 4: ACTUALIZACION
									//LLAVE Kmac
						            SecretKeySpec signingKey = new SecretKeySpec("jesus".getBytes(), MAC);           
									fromUser=Transformacion.transformar(signingKey.getEncoded());

									//CIFRA CON LA LLAVE PUBLICA DEL SERVIDOR
						            Cipher privada = Cipher.getInstance(ALGORITMO);
									privada.init(Cipher.ENCRYPT_MODE, certificadoServidor.getPublicKey());
									byte[] hash = privada.doFinal(fromUser.getBytes());
									fromUser=Transformacion.transformar(hash);
						
									//CIFRA POR BLOQUE CON LA LLAVE PRIVADA DEL CLIENTE
									Cipher llaveHmac = Cipher.getInstance(ALGORITMO);
									llaveHmac.init(Cipher.ENCRYPT_MODE, certificadoCliente.getPriv());
									byte[]cifrar=fromUser.getBytes();
									int i=0;
									ArrayList<byte[]> cifrados=new ArrayList<byte[]>();
									int tamanio=0;
									while(cifrar.length>i)
									{
										byte[] temp = new byte[117];
										for(int j=0;j<117&&cifrar.length>i;j++)
										{
											temp[j]=cifrar[i];
											i++;
										}
										byte[] respC= llaveHmac.doFinal(temp);
										tamanio+=respC.length;
										cifrados.add(respC);
									}
									byte[] llave=new byte[tamanio];
									int voy=0;
									for(int j=0;j<cifrados.size();j++)
									{
										byte[] n = cifrados.get(j);
										for(int k=0;k<n.length;k++)
										{
											llave[voy]=n[k];
											voy++;
										}		
									}
									fromUser=Transformacion.transformar(llave);
									escritor.println("INIT:" + fromUser);
									
									System.out.println("Cliente: INIT:" +fromUser);
									
									byte[] keyBytes = certificadoServidor.getPublicKey().getEncoded();           

//									Mac mac = Mac.getInstance("HmacMD5");
//									mac.init(signingKey);	
//									byte[] macNumero2 = (NUM2+"").getBytes();
//									byte[] hMac = mac.doFinal(macNumero2);
//									String ordenes1 = "ordenes asd";
//									Cipher orden = Cipher.getInstance("RSA");
//									privada.init(Cipher.ENCRYPT_MODE, certificadoCliente.getPriv());
//									byte[] envio = privada.doFinal(ordenes1.getBytes());
//									fromUser=Transformacion.transformar(envio);
//									escritor.println("INIT:" + fromUser);
//
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
	}
}
