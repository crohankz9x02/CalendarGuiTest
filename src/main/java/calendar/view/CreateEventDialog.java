package calendar.view;

import calendar.controller.guicontroller.ViewListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;

/**
 * Dialog for creating and editing events.
 */
public class CreateEventDialog {

  private final List<ViewListener> listeners;

  public CreateEventDialog(List<ViewListener> listeners) {
    this.listeners = listeners;
  }


  /**
   * Shows a dialog for creating a new event.
   *
   * @param parent the parent frame
   * @param selectedDate the currently selected date
   */
  public void showCreateDialog(JFrame parent, LocalDate selectedDate) {
    JDialog dialog = new JDialog(parent, "Create Event", true);
    dialog.setSize(500, 650);
    dialog.setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    // Subject
    panel.add(new JLabel("Subject:"));
    JTextField subjectField = new JTextField(30);
    panel.add(subjectField);

    // Date picker
    panel.add(new JLabel("Date:"));
    SpinnerDateModel dateModel = new SpinnerDateModel(
        Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
        null, null, Calendar.DAY_OF_MONTH);
    JSpinner dateSpinner = new JSpinner(dateModel);
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
    dateSpinner.setEditor(dateEditor);
    panel.add(dateSpinner);

    // All day checkbox
    JPanel allDayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JCheckBox allDayCheck = new JCheckBox("All Day Event");
    allDayPanel.add(allDayCheck);
    panel.add(allDayPanel);

    // Start time
    panel.add(new JLabel("Start Time:"));
    Calendar startCal = new GregorianCalendar();
    startCal.set(Calendar.HOUR_OF_DAY, 9);
    startCal.set(Calendar.MINUTE, 0);
    startCal.set(Calendar.SECOND, 0);
    startCal.set(Calendar.MILLISECOND, 0);


    SpinnerDateModel startTimeModel = new SpinnerDateModel(
        startCal.getTime(), null, null, Calendar.HOUR_OF_DAY);
    JSpinner startTimeSpinner = new JSpinner(startTimeModel);
    JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
    startTimeSpinner.setEditor(startTimeEditor);
    panel.add(startTimeSpinner);

    // End time
    panel.add(new JLabel("End Time:"));
    Calendar endCal = new GregorianCalendar();
    endCal.set(Calendar.HOUR_OF_DAY, 10);
    endCal.set(Calendar.MINUTE, 0);
    endCal.set(Calendar.SECOND, 0);
    endCal.set(Calendar.MILLISECOND, 0);
    SpinnerDateModel endTimeModel = new SpinnerDateModel(
        endCal.getTime(), null, null, Calendar.HOUR_OF_DAY);
    JSpinner endTimeSpinner = new JSpinner(endTimeModel);
    JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
    endTimeSpinner.setEditor(endTimeEditor);
    panel.add(endTimeSpinner);

    // Description
    panel.add(new JLabel("Description:"));
    JTextArea descriptionArea = new JTextArea(3, 30);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    JScrollPane descScroll = new JScrollPane(descriptionArea);
    panel.add(descScroll);

    // Location
    panel.add(new JLabel("Location:"));
    JComboBox<String> locationCombo = new JComboBox<>(
        new String[]{"UNKNOWN", "PHYSICAL", "ONLINE"});
    panel.add(locationCombo);

    // Status
    panel.add(new JLabel("Status:"));
    JComboBox<String> statusCombo = new JComboBox<>(
        new String[]{"UNKNOWN", "PUBLIC", "PRIVATE"});
    panel.add(statusCombo);

    // Recurring event options
    panel.add(new JLabel("Repeat:"));
    JCheckBox recurringCheck = new JCheckBox("Repeat");
    panel.add(recurringCheck);

    // Day of week checkboxes
    JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    daysPanel.add(new JLabel("Repeat on:"));
    JCheckBox monCheck = new JCheckBox("Mon");
    JCheckBox tueCheck = new JCheckBox("Tue");
    JCheckBox wedCheck = new JCheckBox("Wed");
    JCheckBox thuCheck = new JCheckBox("Thu");
    JCheckBox friCheck = new JCheckBox("Fri");
    JCheckBox satCheck = new JCheckBox("Sat");
    JCheckBox sunCheck = new JCheckBox("Sun");
    daysPanel.add(monCheck);
    daysPanel.add(tueCheck);
    daysPanel.add(wedCheck);
    daysPanel.add(thuCheck);
    daysPanel.add(friCheck);
    daysPanel.add(satCheck);
    daysPanel.add(sunCheck);
    daysPanel.setEnabled(false);
    panel.add(daysPanel);

    // End date for recurring events
    panel.add(new JLabel("End Date (for recurring events):"));
    SpinnerDateModel endDateModel = new SpinnerDateModel(
        Date.from(selectedDate.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
        null, null, Calendar.DAY_OF_MONTH);
    JSpinner untilDateSpinner = new JSpinner(endDateModel);
    JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(untilDateSpinner, "yyyy-MM-dd");
    untilDateSpinner.setEditor(endDateEditor);
    untilDateSpinner.setEnabled(false);
    panel.add(untilDateSpinner);

    // Enable/disable time fields based on all day
    allDayCheck.addActionListener(e -> {
      boolean enabled = !allDayCheck.isSelected();
      startTimeSpinner.setEnabled(enabled);
      endTimeSpinner.setEnabled(enabled);
    });

    // Enable/disable recurring options
    recurringCheck.addActionListener(e -> {
      boolean enabled = recurringCheck.isSelected();
      daysPanel.setEnabled(enabled);
      for (Component comp : daysPanel.getComponents()) {
          comp.setEnabled(enabled);
      }
      untilDateSpinner.setEnabled(enabled);
    });

    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton createButton = new JButton("Create");
    createButton.addActionListener(e -> {
      try {
        String subjectText = subjectField.getText().trim();
        if (subjectText.isEmpty()) {
          JOptionPane.showMessageDialog(dialog, "Subject cannot be empty",
              "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        // Get date from spinner
        Date dateValue = (Date) dateSpinner.getValue();
        LocalDate date = dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        boolean allDay = allDayCheck.isSelected();
        LocalTime startTime;
        LocalTime endTime;

        if (allDay) {
          startTime = LocalTime.of(8, 0);
          endTime = LocalTime.of(17, 0);
        } else {
          Date startTimeValue = (Date) startTimeSpinner.getValue();
          Date endTimeValue = (Date) endTimeSpinner.getValue();
          Calendar cal = new GregorianCalendar();
          cal.setTime(startTimeValue);
          startTime = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY),
              cal.get(Calendar.MINUTE));
          cal.setTime(endTimeValue);
          endTime = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY),
              cal.get(Calendar.MINUTE));
        }

        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        if (endDateTime.isBefore(startDateTime)) {
          JOptionPane.showMessageDialog(dialog,
              "End time cannot be before start time",
              "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        String description = descriptionArea.getText().trim();
        if (description.isEmpty()) {
          description = "No description given";
        }

        String location = (String) locationCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();

        boolean isRepeating = recurringCheck.isSelected();

        String repeatDays = null;
        LocalDate repeatEndDate = null;

        if (isRepeating) {
          // Build days string from checkboxes
          StringBuilder daysBuilder = new StringBuilder();
          if (monCheck.isSelected()) {
            daysBuilder.append("M");
          }
          if (tueCheck.isSelected()) {
            daysBuilder.append("T");
          }
          if (wedCheck.isSelected()) {
            daysBuilder.append("W");
          }
          if (thuCheck.isSelected()) {
            daysBuilder.append("R");
          }
          if (friCheck.isSelected()) {
            daysBuilder.append("F");
          }
          if (satCheck.isSelected()) {
            daysBuilder.append("S");
          }
          if (sunCheck.isSelected()) {
            daysBuilder.append("U");
          }

          repeatDays = daysBuilder.toString();
          if (repeatDays.isEmpty()) {
            JOptionPane.showMessageDialog(dialog,
                "Please select at least one day for recurring events", "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
          }

          // Get end date from spinner
          Date endDateValue = (Date) untilDateSpinner.getValue();
          repeatEndDate = endDateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }


        emitCreateEvent(subjectText, startDateTime, endDateTime, description,
            location, status, isRepeating, repeatDays, repeatEndDate);

        dialog.dispose();

      } catch (Exception ex) {
        JOptionPane.showMessageDialog(dialog, "Failed to create event: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
    buttonPanel.add(createButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dialog.dispose());
    buttonPanel.add(cancelButton);

    JScrollPane scrollPane = new JScrollPane(panel);
    dialog.add(scrollPane, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }

  private void emitCreateEvent(String subject, LocalDateTime startDateTime,
                               LocalDateTime endDateTime, String description,
                               String location, String status, boolean isRepeating,
                               String repeatDays, LocalDate repeatEndDate) {
    for (ViewListener listener : listeners) {
      listener.handleCreateEvent(subject, startDateTime, endDateTime, description,
          location, status, isRepeating, repeatDays, repeatEndDate);
    }
  }
}
