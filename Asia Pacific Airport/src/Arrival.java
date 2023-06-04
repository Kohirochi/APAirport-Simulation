import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Arrival extends Thread {
	int landedPlaneCount = 0;
	AirTrafficController atc;
	
	Arrival(AirTrafficController atc) {
		this.atc = atc;
	}
	
	public void waitForQueue(PriorityBlockingQueue<?> landingQueue) {
		synchronized(landingQueue) {
			try {
				landingQueue.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		Plane plane;
		Semaphore gate = atc.getGate();
		while (landedPlaneCount != PlaneGenerator.planeCount) {
			PriorityBlockingQueue<Plane> landingQueue = atc.getLandingQueue();
			// If queue empty, wait
			if (landingQueue.isEmpty()) {
				Function.printMessage("ATC", "Waiting for planes to arrive.");
				waitForQueue(landingQueue);
			}
			// Take the plane without removing it from queue
			plane = landingQueue.peek();
			// If gate available, acquire
			if (gate.tryAcquire()) {
				// If runway available, lock
				if (atc.getRunway().tryLock()) {
					// Remove plane from queue
					plane = landingQueue.poll();
					Function.printMessage("ATC", "Plane " + plane.getId() + " you are allowed to land.");
					plane.land();
					landedPlaneCount++;
				} else {
					// Release the gate, if runway is busy
					gate.release();
					Function.printMessage("ATC", "Runway is currently busy. Plane " + plane.getId() + ", please wait for landing.");
					waitForQueue(landingQueue);
				}
			} else {
				Function.printMessage("ATC", "No gates available. Plane " + plane.getId() + ", please wait for landing.");
				waitForQueue(landingQueue);
			}
		}
	}
}
