package nagp.java.assignment;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {

	private final Properties properties;

	ApplicationProperties() {
		this.properties = new Properties();
		try {
			properties.load(new FileReader("src/main/resources/configuration.properties"));

		} catch (IOException e) {
			System.out.println("IOException Occured while loading properties file::::" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String readProperty(String keyName) {
        return properties.getProperty(keyName, "Key not found in the property file");
    }
}