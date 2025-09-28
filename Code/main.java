import java.util.*;

public class main {

    
    private static final double DANGER_P_M2 = 6.0;     
    private static final int    MIN_SUSTAIN_SEC = 30;  
    private static final int    WINDOW_SEC = 60;       
    private static final int    TIMEOUT_MS = 1500;     
    private static final double MAX_REASONABLE = 12.0; 

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Zone id (e.g., MATAF-GATE-7): ");
            String zone = sc.nextLine().trim();
            if (zone.isEmpty()) throw new IllegalArgumentException("Zone id is required."); // 2) input check

            System.out.print("Average density (people/m^2): ");
            double avg = Double.parseDouble(sc.nextLine().trim());
            validateDensity(avg); // 2) input check

            System.out.print("Seconds sustained at/above that density: ");
            int sec = Integer.parseInt(sc.nextLine().trim());
            validateDuration(sec); // 6) bounds/range checks

            if (isDangerSustained(avg, sec)) {
                System.out.print("Simulated actuator delay in ms (e.g., 900 or 2000): ");
                int delay = Integer.parseInt(sc.nextLine().trim());
                boolean ok = sendClose(zone, delay);  // 7) timeouts

                
                String lastAction = ok ? "CLOSE" + zone : "NONE";
                System.out.println(ok
                        ? "AUDIT: " + lastAction + " recorded."
                        : "WARNING: Actuator timed out — please retry or use manual override.");
            } else {
                System.out.println("Status: density OK — no action needed.");
            }
        } catch (Exception ex) {                             
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    

    private static void validateDensity(double d) {
        if (Double.isNaN(d) || d < 0 || d > MAX_REASONABLE)
            throw new IllegalArgumentException("Density must be 0–" + MAX_REASONABLE + " people/m^2.");
    }

    private static void validateDuration(int sec) {
        if (sec < 0 || sec > WINDOW_SEC)
            throw new IllegalArgumentException("Seconds must be 0–" + WINDOW_SEC + ".");
    }

    private static boolean isDangerSustained(double avgDensity, int seconds) {
        
        double eps = 1e-9;
        return (avgDensity + eps >= DANGER_P_M2) && (seconds >= MIN_SUSTAIN_SEC);
    }

    private static boolean sendClose(String zoneId, int simulatedDelayMs) {
        
        if (simulatedDelayMs > TIMEOUT_MS) return false;
        System.out.println("ACTION: CLOSE issued to zone " + zoneId);
        return true;
    }
}
