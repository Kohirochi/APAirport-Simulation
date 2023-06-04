import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Function {
	public static String padString(String str, int length) {
		return (str.length() >= length) ? str : String.format("%-" + length + "s", str);
	}
	
	public static String getCurrentDateTimeString() {
		return  "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "]";
	}
	
	public static void sleepRandomSeconds(int start, int end) {
	    // Ensure start is less than or equal to end
	    if (start > end) {
	        int temp = start;
	        start = end;
	        end = temp;
	    }
	    
	    try {
			TimeUnit.SECONDS.sleep((int) (Math.random() * (end - start + 1)) + start);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void printMessage(String subject, String message) {
		int paddingLength = 19;
		System.out.println(Function.getCurrentDateTimeString() + " " + padString(subject, paddingLength) + ": " + message);
	}
	
}
