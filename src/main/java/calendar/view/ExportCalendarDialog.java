package calendar.view;

import calendar.controller.guicontroller.ViewListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ExportCalendarDialog implements ActionListener {

  private final List<ViewListener> listeners;
  private JTextField fileNameField;
  private JDialog dialog;

  public ExportCalendarDialog(List<ViewListener> listeners) {
    this.listeners = listeners;
  }

  public void showExportDialog(JFrame parent) {

    this.dialog = new JDialog(parent, "Export Calendar", true);
    dialog.setSize(400, 150);
    dialog.setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    panel.add(new JLabel("File Name (with extension .csv or .ical):"));
    fileNameField = new JTextField("calendar_export.csv");
    panel.add(fileNameField);

    JPanel buttonPanel = new JPanel(new java.awt.FlowLayout());
    JButton exportButton = new JButton("Export");
    exportButton.addActionListener(this);
    exportButton.setActionCommand("export");
    buttonPanel.add(exportButton);

    JButton cancelButton = new JButton("Cancel");
//    cancelButton.addActionListener(e -> dialog.dispose());
    cancelButton.addActionListener(this);
    cancelButton.setActionCommand("cancel");
    buttonPanel.add(cancelButton);

    panel.add(buttonPanel);
    dialog.add(panel);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }

  private void emitExportCalendar(String fileName) {
    for (ViewListener listener : listeners) {
      listener.handleExportCalendar(fileName);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case "export":
        try {
          String fileName = fileNameField.getText();
          if (fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
          }
          emitExportCalendar(fileName);
          dialog.dispose();
        } catch (Exception ex) {
          throw new IllegalArgumentException("Failed to export: " + ex.getMessage());
        }
        break;
      case "cancel":
        dialog.dispose();
        break;
      default:
        throw new IllegalArgumentException("Unknown action command: " + e.getActionCommand());
    }
  }
}
