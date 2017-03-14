import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Fishing {

	public static void main(String[] args){
		//declaration
		int overalltotal=0;
		int totalfish=0;
		int[][] storage=new int[5][10];// storage for 5 attempts
		int slackGuy; // flag for slack mode(if true then somebody slack off)
		Vacationer[] vacationer=new Vacationer[10];
		
		Scanner scan=new Scanner(System.in);
		//create rods and baits
		System.out.println("please enter the number of rod");
		int numRod=scan.nextInt();
		System.out.println("please enter the number of bait");
		int numBait=scan.nextInt();

		//create vacationers & decide to turn on slack mode or not
		
		System.out.println("please enter (Y/N) to enable slack mode(Y for enable slack)");
		String mode = scan.next();
		//create a array with 10 vacationers

		for(int j=0; j<5; j++){
			
			Semaphore bait=new Semaphore(numBait,true);
			Semaphore rod=new Semaphore(numRod,true);
			slackGuy=(int)(Math.random()*10);
			if(mode.toLowerCase().equals("y")){
				System.out.println("Vacationer "+slackGuy+" is the slacker");
				for(int i=0; i<10;i++){
					vacationer[i]=new Vacationer(i,rod,bait,slackGuy);//create vacationer with id and the rod and bait
				}
			}
			
			else{
				for(int i=0; i<10;i++){
					vacationer[i]=new Vacationer(i,rod,bait);//create vacationer with id and the rod and bait
				}
			}
			
			//start the vacationer threads
			for(int i=0; i<10;i++){
				vacationer[i].start();
			}
			//for(;counter<totaltime;counter++){
			try{
				Thread.sleep(24000);//let the fishing time being control into 8 hour and 20 minutes for every fishing
			}
			catch(Exception e){
				System.out.println(e);
				//}
			}

			//stop the process
			for(int i=0; i<10;i++){
				vacationer[i].interrupt();
			}
			System.out.println("***********************************************************");
			System.out.println("results for trial: "+(j+1));
			System.out.println("***********************************************************");
			for(int i=0; i<10;i++){
				System.out.println("Vacationer " + vacationer[i].gotVid() + " caught " + (Math.ceil((vacationer[i].gotTotalFish())/5)));
				totalfish= totalfish+vacationer[i].gotTotalFish();//get the total number of the fishing being caught
				storage[j][i]=vacationer[i].gotTotalFish();

			}
			
			System.out.println("there are " + totalfish + " fish being caught by all the Vacationers");
			System.out.println("************************************************************************");
		}
		System.out.println("***********************************************************");
		System.out.println("Results of all trials");
		System.out.println("***********************************************************");
		for(int i=0; i<10;i++){
			System.out.println("Average number of fishes caught by "+ i +" is "+ ((storage[0][i]+storage[1][i]+storage[2][i]+storage[3][i]+storage[4][i] )/5));
			System.out.println("Total number of fishes (for 5 runs) caught by "+ i +" is "+ (storage[0][i]+storage[1][i]+storage[2][i]+storage[3][i]+storage[4][i]) );
			overalltotal=overalltotal+(storage[0][i]+storage[1][i]+storage[2][i]+storage[3][i]+storage[4][i]);
		}
		System.out.println("Total fish for all trials: "+ overalltotal);
		System.out.println("***********************************************************");

	}

}

