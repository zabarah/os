import java.util.Random;
import java.util.concurrent.Semaphore;

public class Lab3
{
	// Configuration
        final static int PORT0 = 0;
	final static int PORT1 = 1;
	final static int MAXLOAD = 5;

	public static void main(String args[]) 
	{
		final int NUM_CARS = 10;
		int i;

		Ferry fer = new Ferry(PORT0,10);

		Auto [] automobile = new Auto[NUM_CARS];
		for (i=0; i< 7; i++) automobile[i] = new Auto(i,PORT0,fer);
		for ( ; i<NUM_CARS ; i++) automobile[i] = new Auto(i,PORT1,fer);

		Ambulance ambulance = new Ambulance(PORT0,fer);

			/* Start the threads */
 		fer.start();   // Start the ferry thread.
		for (i=0; i<NUM_CARS; i++) automobile[i].start();  // Start automobile threads
		ambulance.start();  // Start the ambulance thread.

		try {fer.join();} catch(InterruptedException e) { }; // Wait until ferry terminates.
		System.out.println("Ferry stopped.");
		// Stop other threads.
		for (i=0; i<NUM_CARS; i++) automobile[i].interrupt(); // Let's stop the auto threads.
		ambulance.interrupt(); // Stop the ambulance thread.
	}
}


class Auto extends Thread { // Class for the auto threads.

	private int id_auto;
	private volatile int port;
	private volatile Ferry fry;
	private Semaphore sem0;
	private Semaphore sem1;
	private Semaphore semun0;
	private Semaphore semun1;

	public Auto(int id, int prt, Ferry ferry)
	{
		this.id_auto = id;
		this.port = prt;
		this.fry = ferry;
		this.sem0=ferry.getsemaphorep0();
		this.sem1=ferry.getsemaphorep1();
		this.semun0=ferry.getsemaphoreun0();
		this.semun1=ferry.getsemaphoreun1();
	}

	public void run() 
        {

	   while (true) 
           {
		// Delay
		try {sleep((int) (300*Math.random()));} catch (Exception e) { break;}
		System.out.println("Auto " + id_auto + " arrives at port " + port);
		//while(port!=fry.myport());
		if (port==0){
		try {
				sem0.acquire();
			} catch (InterruptedException e) {
				break;
				}
		}else{
			try {
				sem1.acquire();
			} catch (InterruptedException e) {
				break;
				}
			
		}

		// Board
		System.out.println("Auto " + id_auto + " boards on the ferry at port " + port);
		fry.addLoad();  // increment the ferry load
		// Arrive at the next port
		port = 1 - port ;   
		

		if (port==0){
		try {
			semun0.acquire();
		} catch (InterruptedException e) {
			break;
			}
	}else{
		try {
			semun1.acquire();
		} catch (InterruptedException e) {
			break;
			}
		
	}
	
	fry.reduceLoad();
		// disembark		
		System.out.println("Auto " + id_auto + " disembarks from ferry at port " + port);
		  // Reduce load

		// Terminate

		
		if(isInterrupted()) break;
           
           }
		
	   System.out.println("Auto "+id_auto+" terminated");
	}
 
}

class Ambulance extends Thread { // the Class for the Ambulance thread

	private volatile int port;
	private volatile Ferry fry;
	private Semaphore sem0;
	private Semaphore sem1;
	private Semaphore semun0;
	private Semaphore semun1;
	
	private int extra;

	public Ambulance(int prt, Ferry ferry)
	{
		this.port = prt;
		this.fry = ferry;
		this.sem0=ferry.getsemaphorep0();
		this.sem1=ferry.getsemaphorep1();
		this.semun0=ferry.getsemaphoreun0();
		this.semun1=ferry.getsemaphoreun1();
		
	}

	public void run() 
        {
	   while (true) 
           {
		// Attente
		try {sleep((int) (1000*Math.random()));} catch (Exception e) { break;}
		System.out.println("Ambulance arrives at port " + port);
		

		if (port==0){
		try {
				sem0.acquire();
				extra=sem0.drainPermits();
			} catch (InterruptedException e) {
				break;
				}
		}else{
			try {
				sem1.acquire();
				extra=sem1.drainPermits();
			} catch (InterruptedException e) {
				break;
				}
			
		}

		// Board
		System.out.println("Ambulance boards the ferry at port " + port);
		fry.addLoad();  // increment the load  
 		
		// Arrive at the next port
		port = 1 - port ; 
		if (port==0){
			try {
					semun0.acquire(1+extra);
					
					
				} catch (InterruptedException e) {
					break;
					}
			}else{
				try {
					semun1.acquire(1+extra);
				} catch (InterruptedException e) {
					break;
					}
				
			}
		
		
		
		//Disembarkment		
		System.out.println("Ambulance disembarks the ferry at port " + port);
		fry.reduceLoad();   // Reduce load

		// Terminate
		if(isInterrupted()) break;
	   }
	   System.out.println("Ambulance terminates.");
	}
 
}

class Ferry extends Thread // The ferry Class
{
	private volatile int port=0;  // Start at port 0
	private volatile int load=0;  // Load is zero
	private int numCrossings;  // number of crossings to execute
	private Semaphore semp0;// Semaphores
	private Semaphore semp1;// Semaphores
	private Semaphore sempun0;
	private Semaphore sempun1;
	//private volatile boolean ambulance=false;


	public Ferry(int prt, int nbtours)
	{
		this.port = prt;
		numCrossings = nbtours;
		semp0=new Semaphore(5, true);
		semp1=new Semaphore(5,true);
		sempun0=new Semaphore(5, true);
		sempun1=new Semaphore(5, true);
		semp0.drainPermits();
		semp1.drainPermits();
		sempun0.drainPermits();
		sempun1.drainPermits();
		
	}

	public void run() 
        {
	   int i;
	   System.out.println("Start at port " + port + " with a load of " + load + " vehicles");

	   // numCrossings crossings in our day
	   for(i=0 ; i < numCrossings ; i++)
           {
		   
		   if (port==0){
			   semp0.release(5);
		   }
		   else{
			   semp1.release(5);
		   }
		   
		   while(!(semp0.availablePermits()==0&&port==0)&&!(semp1.availablePermits()==0&&port==1));
		   
		   try {sleep(1);} catch (Exception e) { }
		   
		   // The crossing
		System.out.println("Departure from port " + port + " with a load of " + load + " vehicles");
		System.out.println("Crossing " + i + " with a load of " + load + " vehicles");
		
		try {sleep((int) (100*Math.random()));} catch (Exception e) { }
		port = 1 - port;
		// Arrive at port
		System.out.println("Arrive at port " + port + " with a load of " + load + " vehicles");
		// Disembarkment et loading
		if (port==0){
			   sempun0.release(5);
		   }
		   else{
			   sempun1.release(5);
		   }
		
		/// waits until everyone unloads of the ferry to start new trip 
		while(load>0);
		try {sleep(1);} catch (Exception e) { }
		
		
		}
	   
	   
	}

	// methodes to manipulate the load of the ferry
	public int getLoad()      { return(load); }
	public int myport(){return port;}
	//public void ambulance(){ambulance=true;}
	public Semaphore getsemaphorep0(){ return semp0;}
	public Semaphore getsemaphorep1(){ return semp1;}
	public Semaphore getsemaphoreun0(){ return sempun0;}
	public Semaphore getsemaphoreun1(){ return sempun1;}
	public void addLoad()  { load = load + 1; }
	public void reduceLoad()  { load = load - 1 ; }
}
