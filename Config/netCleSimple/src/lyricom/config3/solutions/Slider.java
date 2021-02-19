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
package lyricom.config3.solutions;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import lyricom.config3.ui.Utils;

/**
 *
 * @author Andrew
 */

public class Slider extends JPanel {
    JSlider slider;
    public Slider(String left, String right) {
        this(left, right, 0, 100, 50);
    }

    public Slider(String left, String right, int min, int max, int defaultValue) {
        FlowLayout flow = new FlowLayout(FlowLayout.LEFT);
        flow.setVgap(0);
        setLayout(flow);
        JLabel lleft = new JLabel(left);
        JLabel lright = new JLabel(right);
        lleft.setFont(Utils.SLIDER_LABEL_FONT);
        lright.setFont(Utils.SLIDER_LABEL_FONT);
        add(lleft);
        slider = new JSlider(JSlider.HORIZONTAL, min, max, defaultValue);
        add(slider);
        add(lright);
    }

    public int getValue() {
        return slider.getValue();
    }

    public void setValue(int val) {
        slider.setValue(val);
    }
}

