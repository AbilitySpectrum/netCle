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
package lyricom.config3.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Andrew
 */
public class Utils {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    public static final Font STD_FONT = new Font("Dialog", Font.PLAIN, 12);
    public static final Font ITALIC_FONT = new Font("Dialog", Font.ITALIC, 12);
    public static final Font STD_BOLD_FONT = new Font("Dialog", Font.BOLD, 12);
    public static final Font SLIDER_LABEL_FONT = new Font("Dialog", Font.PLAIN, 10);
    public static final Font STATE_FONT = new Font("Dialog", Font.PLAIN, 14);
    public static final Font TITLE_FONT = new Font("Dialog", Font.BOLD, 16);
    public static final Font MONO_FONT = new Font("Monospaced", Font.PLAIN, 14);
    
    public static JLabel getLabel(String str, Dimension dim) {
        JLabel l = new JLabel(str);
        l.setPreferredSize(dim);
        return l;
    }
    
    public static JLabel getLabel(String str, int minWidth) {
        JLabel l = new JLabel(str);
        Dimension dim = l.getPreferredSize();
        if (dim.width < minWidth) {
            dim.width = minWidth;
        }
        l.setPreferredSize(dim);
        return l;
    }
    
    public static byte[] listToArray(List<Byte> bytes) {
        byte[] buffer;
        buffer = new byte[ bytes.size() ];
        int i = 0;
        for(Byte b: bytes) {
            buffer[i++] = b;
        }
        return buffer;
    }
    
    public static final int ICON_UPLOAD = 1;
    public static final int ICON_DOWNLOAD = 2;
    public static final int ICON_ADD = 3;
    public static final int ICON_DELETE = 4;
    public static final int ICON_CURSOR_SPEED = 5;
    public static final int ICON_SAVE_FILE = 6;
    private static ImageIcon uploadIcon = null;
    private static ImageIcon downloadIcon = null;
    private static ImageIcon addIcon = null;
    private static ImageIcon deleteIcon = null;
    private static ImageIcon cursorSpeedIcon = null;
    private static ImageIcon saveFileIcon = null;
    
    public static ImageIcon getIcon(int id) {
        if (uploadIcon == null) {
            // Load icons
            Utils u = new Utils();
            uploadIcon = u.createImageIcon("icons/Upload.png");
            downloadIcon = u.createImageIcon("icons/Download.png");
            addIcon = u.createImageIcon("icons/Add.png");
            deleteIcon = u.createImageIcon("icons/Delete.png");
            cursorSpeedIcon = u.createImageIcon("icons/CursorSpeed.png");
            saveFileIcon = u.createImageIcon("icons/SaveFile.png");
        }
        switch (id) {
            case ICON_UPLOAD:
                return uploadIcon;
            case ICON_DOWNLOAD:
                return downloadIcon;
            case ICON_ADD:
                return addIcon;
            case ICON_DELETE:
                return deleteIcon;
            case ICON_CURSOR_SPEED:
                return cursorSpeedIcon;
            case ICON_SAVE_FILE:
                return saveFileIcon;
            default:
                return null;
        }
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    private ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, "");
        } else {
            System.err.println(RES.getString("SYS_ERR_COULD_NOT_FIND_IMAGE_FILE") + " " + path);
            return null;
        }
    } 
}
