package lyricom.config3.solutions;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;  
import lyricom.config3.solutions.data.CursorSpeedData;
import lyricom.config3.ui.MainFrame;

/**
 *
 * @author Andrew
 */
@XmlRootElement(name = "Solutions")
@XmlAccessorType (XmlAccessType.FIELD)
public class XMLSolutionsList {
    
    @XmlElement(name = "CursorSpeeds")
    private int[] cursorSpeeds;
    
    @XmlElement
    private boolean Bluetooth;

    @XmlElement(name = "Solution")
    private List<SolutionsDataBase> theList;

    
    public XMLSolutionsList() {
    }
    
    public void setList(List<SolutionsDataBase> list) {
        theList = list;
        cursorSpeeds = CursorSpeedData.getInstance().getSpeeds();  
        Bluetooth = MainFrame.getInstance().getBluetooth();
    }
    
    public List<SolutionsDataBase> getList() {
        if (cursorSpeeds != null) { 
            CursorSpeedData.getInstance().setSpeeds(cursorSpeeds);
        }
        MainFrame.getInstance().setBluetooth(Bluetooth);
        
        return theList;
    }    
}
