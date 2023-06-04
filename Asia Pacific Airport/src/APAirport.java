// CHAI LI QI TP061156
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class APAirport {
	
	public static void main(String[] args) throws InterruptedException {
		int gateCount = 3;
		int planeCount = 6;
		boolean[] gateStatus = new boolean[gateCount];
		Arrays.fill(gateStatus, true); // [true, true, true]
		Semaphore gate = new Semaphore(gateCount);
		ReentrantLock runway = new ReentrantLock(); 
		AirTrafficController atc = new AirTrafficController(runway, gate, gateStatus);
		Refueler refueler = new Refueler();
		PlaneGenerator pg = new PlaneGenerator(planeCount, atc, refueler);
		atc.start();
		refueler.start();
		pg.start();
	}
}
