package calendar.view;

import java.time.LocalDateTime;

public interface ViewEvent {

  String getSubject();

  LocalDateTime getStartDateTime();

  LocalDateTime getEndDateTime();

  String getDescription();

  String getLocation();

  String getStatus();

  boolean isAllDay();

  boolean isSeries();
}
