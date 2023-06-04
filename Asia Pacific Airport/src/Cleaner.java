
public class Cleaner extends Thread {
	int id;
	Plane plane;

	Cleaner(int id, Plane plane) {
		this.id = id;
		this.plane = plane;
	}

	@Override
	public void run() {
		Function.printMessage("Cleaner " + id, "Cleaning of the aircraft process on Plane " + plane.getId() + " begins.");
		Function.printMessage("Cleaner " + id, 
				"Removing trash and debris from the cabin, cockpit, and galley areas on Plane " + plane.getId() + ".");
		Function.sleepRandomSeconds(1, 1);
		Function.printMessage("Cleaner " + id, "Vacuuming and sweeping the floors, seats, and carpets on Plane " + plane.getId() + ".");
		Function.sleepRandomSeconds(1, 3);
		Function.printMessage("Cleaner " + id, "Cleaning the lavatories on Plane " + plane.getId() + ".");
		Function.sleepRandomSeconds(1, 2);
		Function.printMessage("Cleaner " + id, "Successfully cleaned Plane " + plane.getId() + ".");
		plane.setCleaned(true);
		synchronized(plane) {
			plane.notify();
		}
	}
}
