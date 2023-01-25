package bannerga.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigBuilder {


    public static Config build() {
        String fileName = "app.config";
        var properties = new Properties();
        try (FileInputStream stream = new FileInputStream(fileName)) {
            properties.load(stream);
        } catch (IOException ignored) {
            System.out.println("Failed to read config file in working directory = " + System.getProperty("user.dir"));
        }
        return new Config(
                properties.getProperty("client.id"),
                properties.getProperty("client.secret"),
                properties.getProperty("tenant.id"),
                properties.getProperty("resource.url"),
                properties.getProperty("service.root"),
                null);

    }
}
