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

import java.util.ArrayList;
import java.util.List;

/**
 * A POJO
 * Holds an array action ID - IR Code pairs.
 * One instance of the class holds all the pairs for
 * one TV type.
 * 
 * @author Andrew
 */
public class TVCodeMap {
    private class Mapping {
        final int actionID;
        final int IRCode;
        
        Mapping(int a, int c) {
            actionID = a;
            IRCode = c;
        }
    };
    
    List<Mapping> theList = new ArrayList<>();
    
    TVCodeMap() {}
    
    void addMapping(int actionID, int IRCode) {
        theList.add(new Mapping(actionID, IRCode));
    }
    
    int getActionID(int IRCode) {
        for(Mapping m: theList) {
            if (m.IRCode == IRCode) return m.actionID;
        }
        return 0;
    }
    
    int getIRCode(int actionID) {
        for(Mapping m: theList) {
            if (m.actionID == actionID) return m.IRCode;
        }
        return 0;
    }
}
