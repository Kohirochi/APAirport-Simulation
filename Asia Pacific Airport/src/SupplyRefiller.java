
public class SupplyRefiller extends Thread {
	int id;
	Plane plane;

	SupplyRefiller(int id, Plane plane) {
		this.id = id;
		this.plane = plane;
	}

	@Override
	public void run() {
		Function.printMessage("Supply Refiller " + id, "Refilling supplies process on Plane " + plane.getId() + " begins.");
		Function.printMessage("Supply Refiller " + id, "Replenishing food and drinks on Plane " + plane.getId() + ".");
		Function.sleepRandomSeconds(1, 2);
		Function.printMessage("Supply Refiller " + id, "Refilling lavatory supplies on Plane " + plane.getId() + ".");
		Function.sleepRandomSeconds(1, 2);
		Function.printMessage("Supply Refiller " + id, "Replacing used magazines and blankets on Plane " + plane.getId() + ".");
		Function.sleepRandomSeconds(1, 2);
		Function.printMessage("Supply Refiller " + id, "Successfully refilled supplies for Plane " + plane.getId() + ".");
		plane.setRefilled(true);
		synchronized(plane) {
			plane.notify();
		}
	}
}
