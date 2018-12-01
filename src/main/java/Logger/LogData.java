package Logger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/*
 * Create a single instance of the logger that needs to be used across all Java classes to create a single log file for the application
 * @author ksonar
 */
public class LogData {
	public static Logger log = null;
	
	public static void createLogger() {
		try {
			buildLogger();
		} catch (SecurityException | IOException e) {
			System.out.println("UNABLE TO CREATE LOGGER");
		}
	}
	
	private static void buildLogger() throws SecurityException, IOException {
		FileHandler file = new FileHandler("log.log");
		file.setFormatter(new SimpleFormatter());
		Handler console = new ConsoleHandler();
		console.setLevel(Level.WARNING);
		log = Logger.getLogger("LOG");
		log.addHandler(file);
		log.addHandler(console);
		log.setLevel(Level.FINE);
		log.setUseParentHandlers(false);
	}

}
