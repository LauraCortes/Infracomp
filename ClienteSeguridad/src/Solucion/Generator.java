package Solucion;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {

	private LoadGenerator g;
	
	public Generator ()
	{
		Task work =createTask();
		int numberofTask=1;
		int gapBetweenTask=1;
		
		g = new LoadGenerator("asdasd", numberofTask, work, gapBetweenTask);
		g.generate();
	}
	
	private Task createTask()
	{
		return new ServidorCliente();
	}
	
	public static void main(String[] args)
	{
		Generator gen = new Generator();
	}
}
