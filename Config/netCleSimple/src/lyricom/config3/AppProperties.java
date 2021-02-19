package lyricom.config3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
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
    private final static String LAST_DOWNLOAD_FILE_NAME = ".netcle.last.download.xml";
    private final File propsFile;
    private final File lastDownloadFile;
//    private final File jarFileDirectory;
    private Properties props;
    
    private AppProperties()  {
/*                
 *  Code to get Jar file location - if we ever need it.
        CodeSource codeSource = AppProperties.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        jarFileDirectory = jarFile.getParentFile();    
 */       

        String home = System.getProperty("user.home");
        File homeFile = new File(home);
        propsFile = new File(homeFile, PROP_FILE_NAME);
        lastDownloadFile = new File (homeFile, LAST_DOWNLOAD_FILE_NAME);
        
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
    
    public File getLastDownloadLocation() {
        return lastDownloadFile;
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
