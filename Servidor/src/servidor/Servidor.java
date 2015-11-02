/**
 * 
 */
package servidor;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


/**
 * Esta clase implementa el servidor que atiende a los clientes. El servidor 
 * esta implemntado como un pool de threads. Cada vez que un cliente crea
 * una conexion al servidor, un thread se encarga de atenderlo el tiempo que
 * dure la sesion. 
 * Infraestructura Computacional Universidad de los Andes. 
 * Las tildes han sido eliminadas por cuestiones de compatibilidad.
 * 
 * @author Michael Andres Carrillo Pinzon 	 -  201320.
 * @author José Miguel Suárez Lopera 		 -  201510
 * @author Cristian Fabián Brochero Rodríguez-  201520
 */
public class Servidor implements Runnable  {

	/**
	 * Constante que especifica el tiempo máximo en milisegundos que se esperara 
	 * por la respuesta de un cliente en cada una de las partes de la comunicación
	 */
	private static final int TIME_OUT = 10000;

	/**
	 * Constante que especifica el numero de threads que se usan en el pool de conexiones.
	 */
	public static final int N_THREADS = 6;

	/**
	 * Puerto en el cual escucha el servidor.
	 |*/
	public static final int PUERTO = 443;

	/**
	 * El socket que permite recibir requerimientos por parte de clientes.
	 */
	private static ServerSocket socket;

	/**
	 * El semaforo que permite tomar turnos para atender las solicitudes.
	 */
	private Semaphore semaphore;

	/**
	 * El id del Thread
	 */
	private int id;

	/**
	 * Metodo main del servidor con seguridad que inicializa un 
	 * pool de threads determinado por la constante nThreads.
	 * @param args Los argumentos del metodo main (vacios para este ejemplo).
	 * @throws IOException Si el socket no pudo ser creado.
	 */
	public static void main(String[] args) throws IOException {

		// Adiciona la libreria como un proveedor de seguridad.
		// Necesario para crear llaves.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		

		// Crea el socket que escucha en el puerto seleccionado.
		socket = new ServerSocket(PUERTO);

		// Genera n threads que correran durante la sesion.
				Servidor [] threads = new Servidor[N_THREADS];
				
				//Nuevo implementacion del Pool de threads con las librerias nuevas.
				ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
				for ( int i = 0 ; i < N_THREADS ; i++) {
					executorService.execute(new Servidor(i));
				}
				System.out.println("El servidor esta listo para aceptar conexiones.");

	}

	/**
	 * Metodo que inicializa un thread y lo pone a correr.
	 * 
	 * @param socket
	 *            El socket por el cual llegan las conexiones.
	 * @throws InterruptedException
	 *             Si hubo un problema con el semaforo.
	 * @throws SocketException 
	 */
	public Servidor(int id) throws  SocketException {
		this.id =id;		
	}

	/**
	 * Metodo que atiende a los usuarios.
	 */
	@Override
	public void run() {
		while (true) {
			Socket s = null;
			// ////////////////////////////////////////////////////////////////////////
			// Recibe una conexion del socket.
			// ////////////////////////////////////////////////////////////////////////

			try 
			{
				System.out.println("Servidor " + id + " listo para recibir conexiones");
				s = socket.accept();
				s.setSoTimeout(TIME_OUT);
			} 
			catch (IOException e)
			{
				e.printStackTrace();
				continue;
			} 
			System.out.println("Thread " + id + " recibe a un cliente.");
			Protocolo p= new Protocolo();
			p.atenderCliente(s);
		}
	}
}
