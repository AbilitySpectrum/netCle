/* * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    Copyright (C) 2019 Andrew Hodgson

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
    along with this netClé configuration software.  
    If not, see <https://www.gnu.org/licenses/>.   
 * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package lyricom.netCleConfig.model;

import java.util.HashSet;
import java.util.Set;

/**
 * If overwrite is set then the incoming triggers will overwrite all
 * of the existing triggers.
 * 
 * Otherwise, existing triggers with sensors in the deleteList will
 * have their triggers deleted and then new triggers will be appended.
 * 
 * Triggers that are in the tmpImport that are not selected by the user
 * are deleted from tmpImport in the ImportDlg code.
 * 
 * Note 'filter' is a misleading name.  Most of the filtering is 
 * done by code in the ImportDlg.  This just holds the replacement rules.
 * 
 * @author Andrew
 */
public class ImportFilter {
    private boolean overwrite = false;
    private Set<Sensor> deleteList = new HashSet<>();
    
    public ImportFilter() {
        
    }
    
    public void setOverwrite(boolean val) {
        overwrite = val;
    }
    
    public boolean isOverwrite() {
        return overwrite;
    }
    
    public void addToDeleteList(Sensor s) {
        deleteList.add(s);
    }
    
    public Set<Sensor> getDeleteList() {
        return deleteList;
    }
}
