import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Refueler extends Thread {
	int refuelledPlaneCount = 0;
	LinkedBlockingQueue<Plane> refuellingQueue;
	AirTrafficController atc;
	ReentrantLock refuellingTruck;
	
	Refueler() {
		this.refuellingQueue = new LinkedBlockingQueue<>();
		this.refuellingTruck = new ReentrantLock();
	}
	
	public void addPlaneToRefuellingQueue(Plane plane) throws InterruptedException {
		Function.printMessage("Refueler", "Plane " + plane.getId() + ", please join the queue and wait for the refuelling truck.");
		refuellingQueue.put(plane);
		synchronized(refuellingQueue) {
			refuellingQueue.notify();
		}
	}
	
	public void waitForQueue(LinkedBlockingQueue<?> refuellingQueue) {
		synchronized(refuellingQueue) {
			try {
				refuellingQueue.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		Plane plane;
		while (refuelledPlaneCount != PlaneGenerator.planeCount) {
			// If queue empty, wait
			if (refuellingQueue.isEmpty()) {
				waitForQueue(refuellingQueue);
			}
			
			// Take the plane without removing it from queue
			plane = refuellingQueue.peek();
			// If refuellingTruck available, lock
			if (refuellingTruck.tryLock()) {
				// Remove plane from queue
				plane = refuellingQueue.poll();
				Function.printMessage("Refueler", "Moving to Plane " + plane.getId() + " for refuelling.");
				Function.sleepRandomSeconds(1, 2);
				Function.printMessage("Refueler", "Connecting the hose to the fuel port of Plane " + plane.getId() + ".");
				Function.sleepRandomSeconds(1, 1);
				Function.printMessage("Refueler", "Refuelling process on Plane " + plane.getId() + " started.");
				Function.sleepRandomSeconds(1, 2);
				Function.printMessage("Refueler", "Disconnecting the hose and closing the fuel port of Plane " + plane.getId() + ".");
				Function.sleepRandomSeconds(1, 1);
				Function.printMessage("Refueler", "Successfully refueled Plane " + plane.getId() + ".");
				plane.setRefuelled(true);
				synchronized(plane) {
					plane.notify();
				}
				refuellingTruck.unlock();
				refuelledPlaneCount++;
			} else {
				Function.printMessage("Refueler", "Refuelling truck is in use. Plane " + plane.getId() + ", please wait.");
				waitForQueue(refuellingQueue);
			}
			
		}
	}
}
