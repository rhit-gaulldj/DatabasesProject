package components;

import dbObj.Time;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeInput extends JPanel {

    private JTextField minsField;
    private JTextField secsField;
    private JTextField fractionalSecsField;

    public TimeInput() {
        DocumentFilter twoNumberFilter = new DocumentFilter(){
            final Pattern regEx = Pattern.compile("^\\d{0,2}$");
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                if (fb.getDocument().getLength() + text.length() > 2) {
                    return;
                }
                if(!matcher.matches()){
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        };
        DocumentFilter fracFilter = new DocumentFilter(){
            final Pattern regEx = Pattern.compile("^\\d{0,5}$");
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                if (fb.getDocument().getLength() + text.length() > 5) {
                    return;
                }
                if(!matcher.matches()){
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        };

        minsField = new JTextField(2);
        secsField = new JTextField(2);
        fractionalSecsField = new JTextField(5);
        ((AbstractDocument)minsField.getDocument()).setDocumentFilter(twoNumberFilter);
        ((AbstractDocument)secsField.getDocument()).setDocumentFilter(twoNumberFilter);
        ((AbstractDocument)fractionalSecsField.getDocument()).setDocumentFilter(fracFilter);

        add(minsField);
        add(new JLabel(":"));
        add(secsField);
        add(new JLabel("."));
        add(fractionalSecsField);
    }

    public Time getTime() {
        if (minsField.getText().length() <= 0 || secsField.getText().length() <= 0) {
            return null;
        }
        int mins = Integer.parseInt(minsField.getText());
        int secs = Integer.parseInt(secsField.getText());
        int fraction = 0;
        if (fractionalSecsField.getText().length() > 0) {
            fraction = Integer.parseInt(fractionalSecsField.getText());
        }
        return new Time(mins, secs, fraction);
    }

    public void clear() {
        minsField.setText("");
        secsField.setText("");
        fractionalSecsField.setText("");
    }
}
