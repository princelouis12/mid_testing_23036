package util;

public class LoggingUtil {
    public static void logDebug(String message) {
        System.out.println("DEBUG: " + message);
    }
    
    public static void logError(String message, Exception e) {
        System.err.println("ERROR: " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }
}