import java.util.concurrent.TimeUnit;

public class Passenger extends Thread {
	int id;
	String type;
	Plane plane;
	
	Passenger(int id, String type, Plane plane) {
		this.id = id;
		this.type = type;
		this.plane = plane;
	}
	
	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void run() {
		try {
            // Simulate the time it takes for the passenger to disembark or embark
			// Generate a random number from 50 to 100
            int time = (int) (Math.random() * 51) + 50;
            if (type == "disembark") {
                Function.printMessage("Passenger " + id, "I'm getting off from Plane " + plane.getId() + " now." );
            } else if (type == "embark") {
            	Function.printMessage("Passenger " + id, "I'm boarding Plane " + plane.getId() + " now." );
            }
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}
