import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

public class Plane extends Thread {
	private int id;
	final private int planeMaxCapacity = 50;
	private int passengerCount;
	private Passenger[] passengers;
	private int gateNum;
	private LocalDateTime arrivalTime;
	private LocalDateTime readyTime;
	private LocalDateTime departureTime;
	AirTrafficController atc;
	Refueler refueler;
	private boolean isLanded = false, isCoastedToGate = false, 
			isDocked = false, isDisembarked = false, 
			isRefilled = false, isCleaned = false, 
			isRefuelled = false, isEmbarked = false;
	
	Plane(int id, AirTrafficController atc, Refueler refueler) {
		this.id = id;
		this.atc = atc;
		this.gateNum = -1;
		this.refueler = refueler;
		generatePassengers();
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPlaneMaxCapacity() {
		return planeMaxCapacity;
	}

	public int getPassengerCount() {
		return passengerCount;
	}

	public void setPassengerCount(int passengerCount) {
		this.passengerCount = passengerCount;
	}

	public AirTrafficController getAtc() {
		return atc;
	}

	public void setAtc(AirTrafficController atc) {
		this.atc = atc;
	}

	public String getArrivalTimeString() {
		return "[" + arrivalTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "]";
	}

	public LocalDateTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalDateTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public Passenger[] getPassengers() {
		return passengers;
	}

	public void setPassengers(Passenger[] passengers) {
		this.passengers = passengers;
	}

	public boolean isLanded() {
		return isLanded;
	}

	public void setLanded(boolean isLanded) {
		this.isLanded = isLanded;
	}

	public boolean isCoastedToGate() {
		return isCoastedToGate;
	}

	public void setCoastedToGate(boolean isCoastedToGate) {
		this.isCoastedToGate = isCoastedToGate;
	}

	public boolean isDocked() {
		return isDocked;
	}

	public void setDocked(boolean isDocked) {
		this.isDocked = isDocked;
	}

	public boolean isDisembarked() {
		return isDisembarked;
	}

	public void setDisembarked(boolean isDisembarked) {
		this.isDisembarked = isDisembarked;
	}

	public boolean isRefilled() {
		return isRefilled;
	}

	public void setRefilled(boolean isRefilled) {
		this.isRefilled = isRefilled;
	}

	public boolean isCleaned() {
		return isCleaned;
	}

	public void setCleaned(boolean isCleaned) {
		this.isCleaned = isCleaned;
	}

	public boolean isRefuelled() {
		return isRefuelled;
	}

	public void setRefuelled(boolean isRefuelled) {
		this.isRefuelled = isRefuelled;
	}

	public boolean isEmbarked() {
		return isEmbarked;
	}

	public void setEmbarked(boolean isEmbarked) {
		this.isEmbarked = isEmbarked;
	}


	public LocalDateTime getReadyTime() {
		return readyTime;
	}

	public void setReadyTime(LocalDateTime readyTime) {
		this.readyTime = readyTime;
	}

	public LocalDateTime getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(LocalDateTime departureTime) {
		this.departureTime = departureTime;
	}

	public void generatePassengers() {
		this.passengerCount = (int) (Math.random() * (planeMaxCapacity - 9)) + 10; // Generate between 10 to 50
		this.passengers = new Passenger[passengerCount];
		for (int i = 0; i < passengerCount; i++) {
			passengers[i] = new Passenger(i + 1,"disembark", this);
		}
	}

	public void requestForLanding() {
		this.setArrivalTime(LocalDateTime.now());
		if (id == 6) {
			Function.printMessage("Plane " + id,
					"Arrived. We have a fuel shortage. Requesting permission for emergency landing.");
		} else {
			Function.printMessage("Plane " + id, "Arrived. Requesting permission for landing.");
		}
		atc.addPlaneToLandingQueue(this);
	}

	public synchronized void land() {
		Function.printMessage("Plane " + id, "Landing.");
		Function.sleepRandomSeconds(1, 2);
		Function.printMessage("Plane " + id, "Successfully landed.");
		isLanded = true;
		// Set to low priority so that after a plane depart, a plane land
		this.setPriority(Thread.MIN_PRIORITY);
		notify();
		atc.getRunway().unlock();
		
		// Notify the departing queue to continue run after the runway is available
		LinkedBlockingQueue<Plane> departingQueue = atc.getDepartingQueue();
		if (!departingQueue.isEmpty()) {
			synchronized(departingQueue) {
				departingQueue.notify();
			}
		}
		
	}
	
	public synchronized int allocateGateNumber() throws InterruptedException {
		while (!isLanded)
			wait();
		boolean[] gateStatus = atc.getGateStatus();
		int gateNum = -1;
		synchronized (gateStatus) {
			for (int i = 0; i < gateStatus.length; i++) {
				if (gateStatus[i]) {
					// Set the gate status to occupied
					gateStatus[i] = false;
					gateNum = i + 1;
					break;
				}
			}
		}
		return gateNum;
	}

	public synchronized void coastToAssignedGate() {
		if (gateNum == -1) { // Check if gate number is assigned
			Function.printMessage("Plane " + id, "Gate is not assigned.");
			return;
		}

		Function.printMessage("ATC", "Plane " + this.getId() + ", please go to gate " + gateNum + ".");
		Function.printMessage("Plane " + id, "Coasting to gate " + gateNum + ".");
		Function.sleepRandomSeconds(1, 2);
		Function.printMessage("Plane " + id, "Successfully coasted to gate " + gateNum + ".");
		isCoastedToGate = true;
		notify();
	}

	public synchronized void dockToAssignedGate() throws InterruptedException {
		while (!isCoastedToGate)
			wait();

		Function.printMessage("Plane " + id, "Docking to gate " + (gateNum) + ".");
		Function.sleepRandomSeconds(1, 2);
		Function.printMessage("Plane " + id, "Successfully docked to gate " + (gateNum) + ".");
		isDocked = true;
		notify();
	}
	
	public synchronized void disembarkPassenger() throws InterruptedException {
		while (!isDocked)
			wait();
		Function.printMessage("Plane " + id, "Disembarking passengers.");
		// Start each passenger thread
		for (Passenger passenger : passengers) {
			passenger.start();
			passenger.join();
		}
		Function.printMessage("Plane " + id, "All passengers have disembarked.");
		isDisembarked = true;
		notify();
	}
	
	
	public synchronized void refillSupplyCleanRefuel() throws InterruptedException {
		while (!isDisembarked)
			wait();
		// Add to refuelling queue
		refueler.addPlaneToRefuellingQueue(this);
		refillSupply();
		clean();		
	}
	
	
	public void refillSupply() throws InterruptedException {
		SupplyRefiller supplyRefiller = new SupplyRefiller(id, this);
		supplyRefiller.start();
	}
	
	
	public void clean() throws InterruptedException {
		Cleaner cleaner = new Cleaner(id, this);
		cleaner.start();
	}
	
	
	public synchronized void receiveNewPassengers() throws InterruptedException {
		while (!isRefuelled || !isRefilled || !isCleaned)
			wait();
		int newPassengerCount = (int) (Math.random() * (planeMaxCapacity - 9)) + 10; // Generate between 10 to 50
		this.passengerCount = newPassengerCount;
		AirTrafficController.totalPassengerBoarded.addAndGet(newPassengerCount);
		this.passengers = new Passenger[passengerCount];
		for (int i = 0; i < passengerCount; i++) {
			passengers[i] = new Passenger(i + 1,"embark", this);
		}
		
		Function.printMessage("Plane " + id, "Receiving new passengers.");
		// Start each passenger thread
		for (Passenger passenger : passengers) {
			passenger.start();
			passenger.join();
		}
		
		Function.printMessage("Plane " + id, "All passengers have embarked.");
		isEmbarked = true;
		notify();
	}
	
	public void requestForDeparting() throws InterruptedException {
		while (!isEmbarked)
			wait();
		this.setReadyTime(LocalDateTime.now());
		Function.printMessage("Plane " + id, "We are ready for departure. Requesting permission for departing.");
		atc.addPlaneToDepartingQueue(this);
	}
	
	public synchronized void undockFromAssignedGate() {
		Semaphore gate = atc.getGate();
		boolean[] gateStatus = atc.getGateStatus();
		PriorityBlockingQueue<Plane> landingQueue = atc.getLandingQueue();
		Function.printMessage("Plane " + id, "Undocking from gate " + (gateNum) + ".");
		Function.sleepRandomSeconds(1, 2);
		Function.printMessage("Plane " + id, "Successfully undocked from gate " + (gateNum) + ".");
		gate.release();
		gateStatus[gateNum - 1] = true;

		if (!landingQueue.isEmpty()) {
			synchronized (landingQueue) {
				landingQueue.notify();
			}
		}
	}
	
	public synchronized void coastToRunway() {
		Function.printMessage("Plane " + id, "Coasting to runway.");
		Function.sleepRandomSeconds(1, 2);
		Function.printMessage("Plane " + id, "Successfully coasted to runway.");
	}
	
	public synchronized void depart() {
		PriorityBlockingQueue<Plane> landingQueue = atc.getLandingQueue();
		Function.printMessage("Plane " + id, "Departing.");
		Function.sleepRandomSeconds(2, 3);
		Function.printMessage("Plane " + id, "Successfully departed.");
		this.setDepartureTime(LocalDateTime.now());
		atc.getRunway().unlock();
		if (!landingQueue.isEmpty()) {
			synchronized (landingQueue) {
				landingQueue.notify();
			}
		}
	}
	
	@Override
	public void run() {
		try {
			requestForLanding();
			this.gateNum = allocateGateNumber();
			coastToAssignedGate();
			dockToAssignedGate();
			disembarkPassenger();
			refillSupplyCleanRefuel();
			receiveNewPassengers();
			requestForDeparting();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
