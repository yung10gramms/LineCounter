package lineCounter.model.consoleInterface;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class Filter extends DocumentFilter {

    private int promptPosition;

    public Filter(int promptPosition)
    {
        this.promptPosition = promptPosition;
    }

    public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr)
            throws BadLocationException {
        if (offset >= promptPosition) {
            super.insertString(fb, offset, string, attr);
        }
    }

    public void remove(final DocumentFilter.FilterBypass fb, final int offset, final int length) throws BadLocationException {
        if (offset >= promptPosition) {
            super.remove(fb, offset, length);
        }
    }

    public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs)
            throws BadLocationException {
        if (offset >= promptPosition) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}