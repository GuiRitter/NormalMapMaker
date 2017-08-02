package io.github.guiritter.normalmapmaker;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

/**
 * Main graphics user interface class and frame.
 * @author Guilherme Alan Ritter
 * @param <StyleType>
 */
public abstract class GUI<StyleType> {

    private final JFileChooser chooser;

    private final HashMap<Components, JTextField> componentMap;

    public static final String ERROR_DIALOG_TITLE = "Error";

    public static final Font font = new Font("DejaVu Sans", 0, 12); // NOI18N

    /**
     * Main graphics user interface frame.
     */
    private final JFrame frame;

    private final JSpinner heightSpinner;

    private final JTextField inputField;

    private final JTextField outputField;

    private final JProgressBar progressBarArray[];

    private final JComboBox<StyleType> styleComboBox;

    public static final String WARNING_DIALOG_TITLE = "Warning";

    private final JSpinner widthSpinner;

    public enum Components {
        INPUT_TEXT_FIELD,
        OUTPUT_TEXT_FIELD
    }

    public JFrame getFrame() {
        return frame;
    }

    public final int getHeight() {
        return ((SpinnerNumberModel) heightSpinner.getModel())
         .getNumber().intValue();
    }

    public final StyleType getStyle() {
        return styleComboBox.getItemAt(styleComboBox.getSelectedIndex());
    }

    public final int getWidth() {
        return ((SpinnerNumberModel) widthSpinner.getModel())
         .getNumber().intValue();
    }

    public abstract void onInputButtonPressed();

    public abstract void onMakeButtonPressed();

    public abstract void onOutputButtonPressed();

    public final void setFieldText(Components textField, String text) {
        componentMap.get(textField).setText(text);
    }

    public final void setProgressBarMaximumValue(int index, int maximumValue) {
        progressBarArray[index].setMaximum(maximumValue);
    }

    public final void setProgressBarReset() {
        for (int i = 0; i < progressBarArray.length; i++) {
            progressBarArray[i].setMaximum(1);
            progressBarArray[i].setValue(1);
        }
    }

    public final void setProgressBarValue(int index, int value) {
        progressBarArray[index].setValue(value);
    }

    public final void showMessageDialog(
     Object message, String title, int messageType) {
        JOptionPane.showMessageDialog(frame, message, title, messageType);
    }

    public final File showOpenSaveDialog(boolean open) {
        if ((open
         ? chooser.showOpenDialog(frame) : chooser.showSaveDialog(frame))
         != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return chooser.getSelectedFile();
    }

    static {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        UIManager.put("Button.font",             font);
        UIManager.put("CheckBox.font",           font);
        UIManager.put("ComboBox.font",           font);
        UIManager.put("InternalFrame.titleFont", font);
        UIManager.put("Label.font",              font);
        UIManager.put("List.font",               font);
        UIManager.put("MenuItem.font",           font);
        UIManager.put("TextField.font",          font);
        UIManager.put("ToolTip.font",            font);
    }

    /**
     * Builds the main graphics user interface frame and shows it.
     * @param styles Style combo box items
     */
    public GUI(StyleType styles[]) {
        JLabel label = new JLabel("—"); // m dash
        int spaceValue = label.getPreferredSize().width;
        frame = new JFrame("Normal Map Maker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints;

        JButton button = new JButton("Input STL:");
        button.addActionListener((ActionEvent e) -> {
            onInputButtonPressed();
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets
         = new Insets(spaceValue, spaceValue, 0, spaceValue);
        frame.getContentPane().add(button, gridBagConstraints);

        inputField = new JTextField();
        inputField.setEditable(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets
         = new Insets(0, spaceValue, 0, spaceValue);
        frame.getContentPane().add(inputField, gridBagConstraints);

        button = new JButton("Output PNG:");
        button.addActionListener((ActionEvent e) -> {
            onOutputButtonPressed();
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets
         = new Insets(spaceValue, spaceValue, 0, spaceValue);
        frame.getContentPane().add(button, gridBagConstraints);

        outputField = new JTextField();
        outputField.setEditable(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets
         = new Insets(0, spaceValue, spaceValue, spaceValue);
        frame.getContentPane().add(outputField, gridBagConstraints);

        label = new JLabel("Width:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets
         = new Insets(0, spaceValue, 0, 0);
        frame.getContentPane().add(label, gridBagConstraints);

        label = new JLabel("Height:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets
         = new Insets(0, spaceValue, 0, 0);
        frame.getContentPane().add(label, gridBagConstraints);

        label = new JLabel("Style:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets
         = new Insets(0, spaceValue, 0, spaceValue);
        frame.getContentPane().add(label, gridBagConstraints);

        widthSpinner = new JSpinner();
        widthSpinner.setModel(
         new SpinnerNumberModel(512, 0, Short.MAX_VALUE, 1));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets
         = new Insets(0, spaceValue, 0, 0);
        frame.getContentPane().add(widthSpinner, gridBagConstraints);

        heightSpinner = new JSpinner();
        heightSpinner.setModel(
         new SpinnerNumberModel(512, 0, Short.MAX_VALUE, 1));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets
         = new Insets(0, spaceValue, 0, 0);
        frame.getContentPane().add(heightSpinner, gridBagConstraints);

        styleComboBox = new JComboBox<>(styles);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets
         = new Insets(0, spaceValue, 0, spaceValue);
        frame.getContentPane().add(styleComboBox, gridBagConstraints);

        button = new JButton("Make normal map");
        button.addActionListener((ActionEvent e) -> {
            onMakeButtonPressed();
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets
         = new Insets(spaceValue, spaceValue, spaceValue, spaceValue);
        frame.getContentPane().add(button, gridBagConstraints);

        progressBarArray = new JProgressBar[Algorithm.PROGRESS_BAR_AMOUNT];
        for (int i = 0; i < Algorithm.PROGRESS_BAR_AMOUNT; i++) {
            progressBarArray[i] = new JProgressBar(0, 1);
            progressBarArray[i].setStringPainted(true);
            progressBarArray[i].setValue(1);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 7 + i;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets
             = new Insets(0, spaceValue,
              i == (Algorithm.PROGRESS_BAR_AMOUNT - 1) ? spaceValue : 0, spaceValue);
            frame.getContentPane().add(progressBarArray[i], gridBagConstraints);
        }

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        componentMap = new HashMap<>();
        componentMap.put(Components.INPUT_TEXT_FIELD, inputField);
        componentMap.put(Components.OUTPUT_TEXT_FIELD, outputField);
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }
}
