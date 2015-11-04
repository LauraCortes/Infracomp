package Solucion;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {

	private LoadGenerator g;
	
	public Generator ()
	{
		
		int papas = 10;
		while(papas != 0)
		{
			try {
		Task work =createTask();
		int numberofTask=80;
		int gapBetweenTask=100;
		
		g = new LoadGenerator("asdasd", numberofTask, work, gapBetweenTask);
		g.generate(); 
		papas--;
		
			} catch (Exception e) 
			{
				papas--;
				continue;
				
				// TODO: handle exception
			}
	}
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
