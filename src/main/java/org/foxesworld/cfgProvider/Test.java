package org.foxesworld.cfgProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Test class with GUI.
 * It displays a frame with a button to trigger an action and a window to show messages.
 *
 * @author AidenFox
 */
public class Test {

    private static CfgProvider test;
    private static Map<String, Object> testMap = null;

    public static void main(String[] args) {
        test = new CfgProvider("test.json");
        testMap = CfgProvider.getCfgMap(CfgProvider.getCurrentCfgName());

        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Test Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        JButton button = new JButton("Trigger Action");

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        button.addActionListener(e -> displayMapContents(textArea));

        panel.add(button, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add label to display read note
        JLabel readNoteLabel = new JLabel();
        panel.add(readNoteLabel, BorderLayout.SOUTH);

        frame.getContentPane().add(panel);
        frame.setPreferredSize(new Dimension(400, 300));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void displayMapContents(JTextArea textArea) {
        StringBuilder message = new StringBuilder();
        for (String key : testMap.keySet()) {
            Object value = testMap.get(key);
            message.append(key).append(": ").append(value).append("\n");
        }
        textArea.setText(message.toString());

        // Display read note
        String readNote = CfgProvider.getReadNote();
        JOptionPane.showMessageDialog(null, readNote, "Read Note", JOptionPane.INFORMATION_MESSAGE);
    }
}
