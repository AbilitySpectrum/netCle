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
package lyricom.config3.ui.selection;

import java.awt.Color;
import java.awt.Font;

/**
 * A place to hold common formatting for Selection Dlg cascade.
 * @author Andrew
 */
public class FormatCtl {
    public static final Color HighlightColor = Color.LIGHT_GRAY;
    
    public static final Font ACTIVITY_PROMPT = new Font("Dialog", Font.BOLD, 14);
    public static final Font DEVICE_PROMPT = new Font("Dialog", Font.ITALIC, 13);
    public static final Font SOLUTIONS_PROMPT = new Font("Dialog", Font.ITALIC, 13);
    public static final Font ACTIVITY = new Font("Dialog", Font.PLAIN, 12);
    public static final Font DEVICE    = new Font("Dialog", Font.PLAIN, 12);
    public static final Font SOLUTION  = new Font("Dialog", Font.PLAIN, 12);
    
    /*
    public static final Font STD_FONT = new Font("Dialog", Font.PLAIN, 12);
    public static final Font ITALIC_FONT = new Font("Dialog", Font.ITALIC, 12);
    public static final Font STD_BOLD_FONT = new Font("Dialog", Font.BOLD, 12);
    public static final Font SLIDER_LABEL_FONT = new Font("Dialog", Font.PLAIN, 10);
    public static final Font STATE_FONT = new Font("Dialog", Font.PLAIN, 14);
    public static final Font TITLE_FONT = new Font("Dialog", Font.BOLD, 16);
    public static final Font MONO_FONT = new Font("Monospaced", Font.PLAIN, 14);
*/
}
