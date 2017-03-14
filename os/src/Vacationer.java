import java.util.concurrent.Semaphore;

public class Vacationer extends Thread {
	private int vid;//vacationer
	private int fish;
	private int totalfish;
	private Semaphore rod;
	private Semaphore bait;
	private boolean slack;//to check if slack mode is on
	public int slackGuy;//if the slack mode is on, generate the vacationer who is the slacker

	public Vacationer(int id,Semaphore rod,Semaphore bait,int slackoff) {
		// TODO Auto-generated constructor stub
		vid=id;
		this.rod =rod;
		this.bait = bait;
		slack = true;
		slackGuy = slackoff;
	}
	
	public Vacationer(int id,Semaphore rod,Semaphore bait) {
		// TODO Auto-generated constructor stub
		vid=id;
		this.rod =rod;
		this.bait = bait;
		slack = false;
	}
	
	public void run(){
	    try{
	    	while(true){
		    	rod.acquire();
		    	System.out.println("Vacationer "+ this.vid + " acquired rod");
		    	
		    	bait.acquire();
		    	System.out.println("Vacationer "+ this.vid + " acquired bait");
		    	System.out.println("Vacationer "+ this.vid + " starts fishing");  
		        Thread.sleep(1000);//wait for 20 minutes
		        fish=(int)(Math.random()*11);//get the number that a vacationer caught
		        totalfish=totalfish+fish;
		        //fishing finished
		        
		        bait.release();//unlock the bait
		        System.out.println("Vacationer "+ this.vid + " released bait and collecting fish into bucket");
		        //if slack mode is on, then go find who is the guy doing that and w8 for an extra 1 min
		        if(slack){
		        	if(this.vid==slackGuy){
				          Thread.sleep(50);
				          System.out.println("Vacationer "+ this.vid + " slacks off");
				        }
		        }
		        
		        Thread.sleep(50);//wait for 1 minutes
		        rod.release();;//unlock the rod
		        System.out.println("Vacationer "+ this.vid + " released rod");
		        System.out.println("--Vacationer " + this.vid + " caught " + fish +" fish");
		      }
	    }
	    catch(Exception e){
	      //System.out.println(e);
	    }
	  }
	
	//method to get the number of total fish caught by the vacationer
	public int gotTotalFish(){
		return totalfish;
	}
	 
	public int gotVid(){
		return vid;
	}
	public void reset(){
		fish=0;
		totalfish=0;
	}
}
