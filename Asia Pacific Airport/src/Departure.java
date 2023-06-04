import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

public class Departure extends Thread {
	int departedPlaneCount = 0;
	AirTrafficController atc;
	
	Departure(AirTrafficController atc) {
		this.atc = atc;
	}
	
	public void waitForQueue(LinkedBlockingQueue<?> departingQueue) {
		synchronized(departingQueue) {
			try {
				departingQueue.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		Plane planeToDepart;
		Plane planeToLand;
		Semaphore gate = atc.getGate();
		while (departedPlaneCount != PlaneGenerator.planeCount) {
			LinkedBlockingQueue<Plane> departingQueue = atc.getDepartingQueue();
			PriorityBlockingQueue<Plane> landingQueue = atc.getLandingQueue();
			// If queue empty, wait
			if (departingQueue.isEmpty()) {
				waitForQueue(departingQueue);
			}
			
			// Take the plane without removing it from queue
			planeToDepart = departingQueue.peek();
			if (!landingQueue.isEmpty()) {
				planeToLand = landingQueue.peek();
				if(gate.availablePermits() > 0 && planeToDepart.getPriority() < planeToLand.getPriority()) {
					waitForQueue(departingQueue);
				}
			}
			
			// If runway available, lock
			if (atc.getRunway().tryLock()) {
				// Remove plane from queue
				planeToDepart = departingQueue.poll();
				Function.printMessage("ATC", "Plane " + planeToDepart.getId() + " you are allowed to depart.");
				planeToDepart.undockFromAssignedGate();
				planeToDepart.coastToRunway();
				planeToDepart.depart();
				departedPlaneCount++;
				
				// Calculate wait time
				Long planeWaitTime = Duration.between(planeToDepart.getArrivalTime(), planeToDepart.getDepartureTime()).getSeconds();
				AirTrafficController.planesWaitTime.put(planeToDepart.getId(), planeWaitTime);
			} else {
				Function.printMessage("ATC", "Runway is currently busy. Plane " + planeToDepart.getId() + ", please wait for departing.");
				waitForQueue(departingQueue);
			}
			
		}
	}
}
