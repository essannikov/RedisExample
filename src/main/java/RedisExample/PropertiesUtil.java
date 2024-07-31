package RedisExample;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    private final static String FILE_NAME = "src/main/resources/application.properties";

    private static PropertiesUtil instance;

    private Properties properties;

    private PropertiesUtil(){
        File file = new File(FILE_NAME);

        properties = new Properties();
        try {
            properties.load(new FileReader(file));
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }
    }

    public static PropertiesUtil getInstance(){
        if (instance == null){
            instance = new PropertiesUtil();
        }
        return instance;
    }

    public String getRedis(){
        return (String) properties.get("redis");
    }
}