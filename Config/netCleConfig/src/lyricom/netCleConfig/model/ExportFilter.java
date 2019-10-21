/* * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    This file is part of the netClé Configuration software.

    netClé Configuration software is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    netClé Configuration software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this netClé Arduino software.  
    If not, see <https://www.gnu.org/licenses/>.   
 * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package lyricom.netCleConfig.model;

import java.util.Set;

/**
 * Used to limit an export stream to a particular set of sensors.
 * @author Andrew
 */
public class ExportFilter {
    private boolean exportAll;
    private boolean exportMouseSpeed;
    private Set<Sensor> sensors;
    
    public ExportFilter(Set<Sensor> sensors) {
        this.sensors = sensors;
        exportAll = false;       
    }
    
    public ExportFilter() {
        exportAll = true;
        exportMouseSpeed = true;
    }
    
    public void setExportMouseSpeed(boolean b) {
        exportMouseSpeed = b;
    }
    
    public boolean exportThis(Trigger t) {
        if (exportAll) {
            return true;
        } else {
            return sensors.contains(t.getSensor());
        }
    }
    
    boolean exportMouseSpeed() {
        return exportMouseSpeed;
    }
}
