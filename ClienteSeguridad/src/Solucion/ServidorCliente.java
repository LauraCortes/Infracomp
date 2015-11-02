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
import javax.crypto.spec.SecretKeySpec;
import uniandes.gload.core.Task;

public class ServidorCliente extends Task
{
	 private String RTA_AFIRTMATIVA="RTA:OK";
	 private String ALGORITMO="RSA";
	 private  String MAC="HMACMD5";
	private  double NUM1=Math.random();
	private  double NUM2;
	private  long tiempoValidacion;
	private  long tiempoRespuestaSolicitud;

	
	

	@Override
	public void fail() {
		// TODO Auto-generated method stub
		System.out.println(Task.MENSAJE_FAIL);
	}

	@Override
	public void success() {
		// TODO Auto-generated method stub
		System.out.println(Task.OK_MESSAGE);
		
	}

	@Override
	public void execute() 
	{
		
		try 
		{
			boolean ejecutar = true;
			Socket servidor=null;
			PrintWriter escritor = null;
			BufferedReader lector = null;
			Certificate certificadoServidor = null ;
			try 
			{
				System.out.println("hola");
				servidor = new Socket("186.28.23.17",443);
				
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
						System.out.println("Cliente: " + "Se conteo de tiempo de numero aleatorios ");

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
										
										
										long acabo = System.currentTimeMillis();
										tiempoValidacion = (acabo - inicio );
										
								        System.out.println("Cliente: El tiempo de autentificacion fue de  "+ tiempoValidacion);

										File cosa = new File("./tiempoSalida1.txt"); 
										FileWriter salida = new FileWriter(cosa, true);
										salida.write("\n" + tiempoValidacion);
										salida.close();
															
										System.out.println("Servidor: "+fromServer);
										
										//ETAPA 4: ACTUALIZACION
										//LLAVE Kmac
							            SecretKeySpec millave = new SecretKeySpec("jesus".getBytes(), MAC);           

										//CIFRA CON LA LLAVE PUBLICA DEL SERVIDOR
							            Cipher privada = Cipher.getInstance(ALGORITMO);
										privada.init(Cipher.ENCRYPT_MODE, certificadoServidor.getPublicKey());
										byte[] hash = privada.doFinal(millave.getEncoded());
										fromUser=Transformacion.transformar(hash);
							
										//CIFRA POR BLOQUE CON LA LLAVE PRIVADA DEL CLIENTE
										Cipher llaveHmac = Cipher.getInstance(ALGORITMO);
										llaveHmac.init(Cipher.ENCRYPT_MODE, certificadoCliente.getPriv());
										byte[]cifrar=hash;
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
																				
										long reporto = System.currentTimeMillis();
										
										System.out.println("Cliente: INIT:" +fromUser);
										
										byte[] keyBytes = certificadoServidor.getPublicKey().getEncoded();           

										String ordenes1 = "ordenes asd";
							
										Cipher orden = Cipher.getInstance("RSA");
										orden.init(Cipher.ENCRYPT_MODE, certificadoServidor.getPublicKey());
										byte[] envio = orden.doFinal(ordenes1.getBytes());
										fromUser=Transformacion.transformar(envio);
										escritor.println( fromUser);
										System.out.println("Cliente: Ordenes: " +fromUser);
										
										Mac mac = Mac.getInstance("HmacMD5");
										mac.init(millave);	
										byte[] macNumero2 = ordenes1.getBytes();
										byte[] hMac = mac.doFinal(macNumero2);

										Cipher integridadOrden = Cipher.getInstance("RSA");
										integridadOrden.init(Cipher.ENCRYPT_MODE, certificadoServidor.getPublicKey());
										byte[] intorder = integridadOrden.doFinal(hMac);
										fromUser=Transformacion.transformar(intorder);
										
										escritor.println(fromUser );
										
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
//										System.out.println("Jesus: "+fromUser);
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
		catch (Exception e) {
			
			
			System.out.println("PERDIDAS");

			// TODO: handle exception
		}
		
	}
}
