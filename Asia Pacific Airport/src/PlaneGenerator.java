import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PlaneGenerator extends Thread {
	static int planeCount;
	Random random = new Random();
	AirTrafficController atc;
	Refueler refueler;
	
	PlaneGenerator(int planeCount, AirTrafficController atc, Refueler refueler) {
		PlaneGenerator.planeCount = planeCount;
		this.atc = atc;
		this.refueler = refueler;
	}
	
	public void run() {
		for (int i = 1; i <= planeCount; i++) {
			// Generate a random number from 0 to 3
			int randomNum = (int) (Math.random() * 4);  
			try {
				Plane plane = new Plane(i, atc, refueler);
				if (i == planeCount) {
					plane.setPriority(Thread.MAX_PRIORITY);
				}
				Function.printMessage("Plane " + plane.getId(), 
						"Arriving at the airport with " + plane.getPassengerCount() + 
						" passengers in " + randomNum + (randomNum > 1 ? " seconds." : " second."));
				TimeUnit.SECONDS.sleep(randomNum);
				plane.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
