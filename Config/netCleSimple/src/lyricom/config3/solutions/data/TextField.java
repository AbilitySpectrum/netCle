package lyricom.config3.solutions.data;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import lyricom.config3.ui.Utils;

/**
 * A text field which is limited to a give length.
 * 
 * @author Andrew
 */
public class TextField extends JPanel {
    protected final JTextField field;
    private final int maxWidth;
    
    public TextField(int width) {
        super();
        maxWidth = width;
        
        FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
        layout.setVgap(0);
        setLayout(layout);
        
        field = new JTextField(width);
        field.setFont(Utils.MONO_FONT);
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        doc.setDocumentFilter( new DocFilterx() );
                
        field.setHorizontalAlignment(JTextField.LEFT);
        field.setBorder(BorderFactory.createCompoundBorder(
                field.getBorder(), 
                BorderFactory.createEmptyBorder(1, 2, 0, 2)));  
        
        add(field);
    }
    
    public void setValue(String value) {
        field.setText(value);
    }
    
    public String getValue() {
        String txt = field.getText();
        return txt;
    }    
    
    class DocFilterx extends DocumentFilter {
        // InsertString never seems to get called ... ???
        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, 
                String str, AttributeSet a) throws BadLocationException {
            super.insertString(fb, offset, str, a);
        }
        
        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int len, String str,
            AttributeSet attrs) throws BadLocationException {
            
            // Check final expected length
            // Note: len is # of chars that will be removed.
            int currentLen = fb.getDocument().getLength();
            int targetLen = currentLen + str.length() - len;
            if (targetLen > maxWidth) return;
            
            super.replace(fb, offset, len, str, attrs);
        }        
    }
}
