import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class AirTrafficController extends Thread {
	private PriorityBlockingQueue<Plane> landingQueue;
	private LinkedBlockingQueue<Plane> departingQueue;
	private ReentrantLock runway;
	private Semaphore gate;
	boolean[] gateStatus;
	static AtomicInteger totalPassengerBoarded = new AtomicInteger(0);
	static Map<Long, Long> planesWaitTime = new HashMap<>();
	
	AirTrafficController(ReentrantLock runway, Semaphore gate, boolean[] gateStatus) {
		//Comparator for priority field
		Comparator<Plane> prioritySorter = Comparator.comparing(Plane::getPriority).reversed().thenComparing(Plane::getArrivalTime);
		this.landingQueue = new PriorityBlockingQueue<>(1, prioritySorter);
		this.departingQueue = new LinkedBlockingQueue<>();
		this.runway = runway;
		this.gate = gate;
		this.gateStatus = gateStatus;
	}
	
	public PriorityBlockingQueue<Plane> getLandingQueue() {
		return landingQueue;
	}

	public void setLandingQueue(PriorityBlockingQueue<Plane> landingQueue) {
		this.landingQueue = landingQueue;
	}

	public LinkedBlockingQueue<Plane> getDepartingQueue() {
		return departingQueue;
	}

	public void setDepartingQueue(LinkedBlockingQueue<Plane> departingQueue) {
		this.departingQueue = departingQueue;
	}

	public ReentrantLock getRunway() {
		return runway;
	}

	public void setRunway(ReentrantLock runway) {
		this.runway = runway;
	}

	public Semaphore getGate() {
		return gate;
	}

	public void setGate(Semaphore gate) {
		this.gate = gate;
	}
	
	public boolean[] getGateStatus() {
		return gateStatus;
	}

	public void setGateStatus(boolean[] gateStatus) {
		this.gateStatus = gateStatus;
	}

	public void addPlaneToLandingQueue(Plane plane) {
		Function.printMessage("ATC", "Plane " + plane.getId() + ", please join the queue and wait for landing.");
		landingQueue.put(plane);
		synchronized(landingQueue) {
			landingQueue.notify();
		}
	}
	
	public void addPlaneToDepartingQueue(Plane plane) throws InterruptedException {
		Function.printMessage("ATC", "Plane " + plane.getId() + ", please join the queue and wait for departing.");
		departingQueue.put(plane);
		synchronized(departingQueue) {
			departingQueue.notify();
		}
	}
	
	public void checkGateIsEmpty() {
		if (gate.availablePermits() != gateStatus.length) {
			Function.printMessage("ATC", "There are still gates that are occupied.");
		}
		for (int i = 0; i < gateStatus.length; i++) {
			int gateNum = i + 1;
			if (gateStatus[i]) {
				Function.printMessage("ATC", "Gate " + gateNum + " is empty.");
			} else {
				Function.printMessage("ATC", "Gate " + gateNum + " is in used.");
			}
		}
	}
	
	public void printPlaneWaitTimeStatistics() {
		 // Find max, min, and average planeWaitTime
        long maxWaitTime = Collections.max(planesWaitTime.values());
        long minWaitTime = Collections.min(planesWaitTime.values());
        double avgWaitTime = planesWaitTime.values().stream().mapToLong(Long::longValue).average().orElse(0);
        Function.printMessage("ATC", "Maximum waiting time for a plane = " + maxWaitTime + " seconds.");
        Function.printMessage("ATC", "Average waiting time for a plane = " + String.format("%.2f", avgWaitTime) + " seconds.");
        Function.printMessage("ATC", "Minimum waiting time for a plane = " + minWaitTime + " seconds.");
	}
	
	@Override
	public void run(){
		Arrival arrival = new Arrival(this);
		Departure departure = new Departure(this);
		arrival.start();
		departure.start();		
		try {
			departure.join();
			if (departure.departedPlaneCount == PlaneGenerator.planeCount) {
				Function.printMessage("ATC", "All planes have departed successfully.");
			} else {
				Function.printMessage("ATC", "There are still planes that haven't depart.");
			}
			checkGateIsEmpty();
			printPlaneWaitTimeStatistics();
			Function.printMessage("ATC", "Total number of plane served = " + departure.departedPlaneCount);
			Function.printMessage("ATC", "Total number of passengers boarded = " + totalPassengerBoarded);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}	
}
