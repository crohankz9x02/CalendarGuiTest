package calendar.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DayPanel extends JPanel implements MouseListener {
  private final LocalDate date;
  private final Consumer<LocalDate> dateSelected;
  private static final int MAX_EVENTS_TO_SHOW = 4;

  public DayPanel(LocalDate date, List<ViewEvent> eventList,
                  boolean selectedDate, Color calendarColor, Consumer<LocalDate> dateSelected) {

    super(new BorderLayout());
    this.date = date;
    this.dateSelected = dateSelected;

    setPreferredSize(new Dimension(100, 100));

    if (selectedDate) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
    } else {
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    }

    setBackground(calendarColor);

    JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.CENTER);
    this.add(dayLabel, BorderLayout.NORTH);

    JPanel eventsPanel = new JPanel();
    eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
    eventsPanel.setOpaque(false);

    int eventsToShow = Math.min(eventList.size(), MAX_EVENTS_TO_SHOW);

    this.addMouseListener(this);
    dayLabel.addMouseListener(this);

    for (int i = 0; i < eventsToShow; i++) {
      ViewEvent event = eventList.get(i);
      String subject = event.getSubject();
      JLabel eventLabel = new JLabel(subject);
      eventLabel.setOpaque(true);
      eventLabel.setBackground(new Color(255, 235, 59));
      eventLabel.setForeground(Color.BLACK);
      eventLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
      eventLabel.addMouseListener(this);
      eventsPanel.add(eventLabel);
    }

    if (eventList.size() > MAX_EVENTS_TO_SHOW) {
      JLabel moreLabel = new JLabel((eventList.size() - MAX_EVENTS_TO_SHOW) + " more");
      moreLabel.setForeground(Color.GRAY);
      moreLabel.addMouseListener(this);
      eventsPanel.add(moreLabel);
    }

    add(eventsPanel, BorderLayout.CENTER);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (dateSelected != null) {
      dateSelected.accept(date);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {

  }

  @Override
  public void mouseReleased(MouseEvent e) {

  }

  @Override
  public void mouseEntered(MouseEvent e) {

  }

  @Override
  public void mouseExited(MouseEvent e) {

  }
}
