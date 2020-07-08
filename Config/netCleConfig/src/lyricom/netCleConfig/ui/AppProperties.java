package lyricom.netCleConfig.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 *
 * @author Andrew
 */
public class AppProperties {
    private static AppProperties instance = null;
    
    public static AppProperties getInstance() {
        if (instance == null) {
            instance = new AppProperties();
        }
        return instance;
    }
    
    private final static String PROP_FILE_NAME = ".netcle.props";
    private final String propsFile;
    private Properties props;
    
    private AppProperties()  {
        Path propsPath = Paths.get(System.getProperty("user.home"), PROP_FILE_NAME);
        propsFile = propsPath.toString();
        
        props = new Properties();
        try {
            FileInputStream in = new FileInputStream(propsFile);
            props.load(in);
            in.close();

        } catch (FileNotFoundException ex) {
            // Leave props empty.
        } catch (IOException ex) {
            
        }
    }
    
    public String getProperty(String key) {
        return props.getProperty(key, null);
    }
    
    public void setProperty(String key, String value) {
        props.setProperty(key, value);
        save();
    }
    
    void save() {
        try {
            FileOutputStream out = new FileOutputStream(propsFile);
            props.store(out, "---");
            out.close();
        } catch (Exception ex) {
            // Well, we tried ...
        }
    }
}
