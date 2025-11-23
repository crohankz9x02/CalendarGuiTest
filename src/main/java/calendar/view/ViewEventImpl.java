package calendar.view;

import java.time.LocalDateTime;

/**
 * Data transfer object for event information to be displayed in the view.
 * Uses a builder to avoid large constructors.
 */
public class ViewEventImpl implements ViewEvent {
  private final String subject;
  private final LocalDateTime startDateTime;
  private final LocalDateTime endDateTime;
  private final String description;
  private final String location;
  private final String status;
  private final boolean allDay;
  private final boolean isSeries;

  private ViewEventImpl(Builder builder) {
    this.subject = builder.subject;
    this.startDateTime = builder.startDateTime;
    this.endDateTime = builder.endDateTime;
    this.description = builder.description;
    this.location = builder.location;
    this.status = builder.status;
    this.allDay = builder.allDay;
    this.isSeries = builder.isSeries;
  }

  public static class Builder {
    private String subject;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description = "";
    private String location = "";
    private String status = "";
    private boolean allDay = false;
    private boolean isSeries = false;

    public Builder setSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public Builder setStartDateTime(LocalDateTime start) {
      this.startDateTime = start;
      return this;
    }

    public Builder setEndDateTime(LocalDateTime end) {
      this.endDateTime = end;
      return this;
    }

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder setLocation(String location) {
      this.location = location;
      return this;
    }

    public Builder setStatus(String status) {
      this.status = status;
      return this;
    }

    public Builder setAllDay(boolean allDay) {
      this.allDay = allDay;
      return this;
    }

    public Builder setIsSeries(boolean isSeries) {
      this.isSeries = isSeries;
      return this;
    }

    public ViewEvent build() {
      return new ViewEventImpl(this);
    }
  }

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  @Override
  public LocalDateTime getEndDateTime() { return endDateTime; }

  @Override
  public String getDescription() { return description; }

  @Override
  public String getLocation() { return location; }

  @Override
  public String getStatus() { return status; }

  @Override
  public boolean isAllDay() { return allDay; }

  @Override
  public boolean isSeries() { return isSeries; }
}
