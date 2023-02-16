package components;

import dbObj.DistancePair;
import dbObj.Split;
import util.SimpleAction;
import util.SimpleArgAction;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitInput extends JPanel {

    private JTextField distanceField;
    private JComboBox<String> unitsField;
    private TimeInput timeField;
    private JButton deleteButton;

    private SimpleArgAction onDelete;

    public SplitInput() {
        distanceField = new JTextField(5);
        ((AbstractDocument)distanceField.getDocument()).setDocumentFilter(new DocumentFilter(){
            final Pattern regEx = Pattern.compile("^\\d*[.]?\\d*$");
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                if(!matcher.matches()){
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });
        unitsField = new JComboBox<>(new String[] {"mi", "km", "m"});
        timeField = new TimeInput();
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> delete());

        JPanel distanceRow = new JPanel();
        distanceRow.add(new JLabel("Distance: "));
        distanceRow.add(distanceField);
        distanceRow.add(unitsField);
        add(distanceRow);

        JPanel timeRow = new JPanel();
        timeRow.add(new JLabel("Time: "));
        timeRow.add(timeField);
        add(timeRow);

        JPanel deleteRow = new JPanel();
        deleteRow.add(deleteButton);
        add(deleteRow);
    }

    public Split getSplit() {
        if (distanceField.getText().length() <= 0) {
            return null;
        }
        final Pattern regEx = Pattern.compile("^\\d*[.]?\\d*$");
        if (!regEx.matcher(distanceField.getText()).matches()) {
            return null;
        }
        DistancePair dist = new DistancePair(Float.parseFloat(distanceField.getText()),
                unitsField.getSelectedItem().toString());
        return new Split(dist, timeField.getTime());
    }

    private void delete() {
        onDelete.call(this);
    }

    public void setDeleteAction(SimpleArgAction action) {
        this.onDelete = action;
    }

}
