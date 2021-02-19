/* * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    Copyright (C) 2021 Andrew Hodgson

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
package lyricom.config3.solutions.data;

import java.io.PrintStream;
import lyricom.config3.model.CursorSpeedTransfer;
import lyricom.config3.model.CursorSpeedTransferInterface;
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.Slider;
import lyricom.config3.solutions.SolutionsDataBase;

/**
 *
 * @author Andrew
 */
public class CursorSpeedData extends SolutionsDataBase implements CursorSpeedTransferInterface {
    private static CursorSpeedData instance = null;
    public static CursorSpeedData getInstance() {
        if (instance == null) {
            instance = new CursorSpeedData();
        }
        return instance;
    }
    
    private final Slider firstSpeed;
    private final Slider secondSpeed;
    private final Slider thirdSpeed;
    private final NumericField firstInterval;
    private final NumericField secondInterval;
        
    private CursorSpeedData() {
        super(null);
        String slow = RES.getString("CS_SLOW");
        String fast = RES.getString("CS_FAST");
        
        firstSpeed = new Slider(slow, fast, 350, 650, 420);
        secondSpeed = new Slider(slow, fast, 350, 650, 480);
        thirdSpeed = new Slider(slow, fast, 350, 650, 540);
        
        firstInterval = new NumericField(RES.getString("CS_SPEED_INTERVAL"), 5, 0, 10000);
        secondInterval = new NumericField(RES.getString("CS_SPEED_INTERVAL"), 5, 0, 10000);
        firstInterval.setValue(500);
        secondInterval.setValue(500);
        
        CursorSpeedTransfer.getInstance().registerUIComponent(this);
    }

    @Override
    public void compile() {
        // Do nothing.  
    }
    
    @Override
    public void printDescription(PrintStream out ) {
        
    }
 
    public Slider getFirstSpeed() {
        return firstSpeed;
    }

    public Slider getSecondSpeed() {
        return secondSpeed;
    }

    public Slider getThirdSpeed() {
        return thirdSpeed;
    }

    public NumericField getFirstInterval() {
        return firstInterval;
    }

    public NumericField getSecondInterval() {
        return secondInterval;
    }

    @Override
    public int[] getSpeeds() {
        int[] vals = new int[5];
        vals[0] = firstSpeed.getValue();
        vals[1] = secondSpeed.getValue();
        vals[2] = thirdSpeed.getValue();
        vals[3] = firstInterval.getValue();
        vals[4] = secondInterval.getValue();
        return vals;
    }

    @Override
    public void setSpeeds(int[] values) {
        firstSpeed.setValue(values[0]);
        secondSpeed.setValue(values[1]);
        thirdSpeed.setValue(values[2]);
        firstInterval.setValue(values[3]);
        secondInterval.setValue(values[4]);
    }


    public EPort getPortUsed() {
        return null;
    }

    @Override
    public int getSensorCount() {
        return 0;
    }
}
