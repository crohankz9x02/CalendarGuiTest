package calendar.controller.guicontroller;

import calendar.controller.CalendarController;
import calendar.controller.commanddata.CreateCalendarCommandData;
import calendar.controller.commanddata.CreateCommandData;
import calendar.controller.commanddata.DeleteEventCommandData;
import calendar.controller.commanddata.DeleteMultipleEventsCommandData;
import calendar.controller.commanddata.DeleteSeriesCommandData;
import calendar.controller.commanddata.EditCalendarCommandData;
import calendar.controller.commanddata.EditEventCommandData;
import calendar.controller.commanddata.EditMultipleEventsCommandData;
import calendar.controller.commanddata.EditSeriesCommandData;
import calendar.controller.commanddata.ExportCommandData;
import calendar.controller.handlers.CreateCalendarHandler;
import calendar.controller.handlers.CreateEventHandler;
import calendar.controller.handlers.DeleteEventHandler;
import calendar.controller.handlers.DeleteMultipleEventsHandler;
import calendar.controller.handlers.DeleteSeriesHandler;
import calendar.controller.handlers.EditCalendarHandler;
import calendar.controller.handlers.EditEventHandler;
import calendar.controller.handlers.EditMultipleEventsHandler;
import calendar.controller.handlers.EditSeriesHandler;
import calendar.controller.handlers.ExportEventHandler;
import calendar.model.interfaces.AdvancedCalendar;
import calendar.model.interfaces.CalendarContainer;
import calendar.model.interfaces.EventReadOnly;
import calendar.view.CalendarGuiView;
import calendar.view.ViewEvent;
import calendar.view.ViewEventImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Calendar GUI application.
 * Implements ViewListener to handle all user interactions from the view.
 * All business logic is handled here, not in the view.
 */
public class CalendarGuiController implements CalendarController, ViewListener {

  private final CalendarContainer container;
  private final CalendarGuiView view;
  private LocalDate currentMonth;
  private LocalDate selectedDate;


  /**
   * Constructor for CalendarGuiController.
   *
   * @param container the calendar container (model)
   * @param view      the GUI view
   */
  public CalendarGuiController(CalendarContainer container, CalendarGuiView view) {
    this.container = container;
    this.view = view;
    this.currentMonth = LocalDate.now();
    this.selectedDate = LocalDate.now();
    view.addViewListener(this);
  }

  @Override
  public void run() {
//    setupViewForDialogs();
    view.display();
    handleRefresh();
  }

  @Override
  public void handleSwitchCalendar(String calendarName) {
    try {
      container.setActiveCalendar(calendarName);
      handleRefresh();
    } catch (Exception e) {
      view.renderError("Error: Failed to switch calendar: " + e.getMessage());
    }
  }

  @Override
  public void handleCreateCalendar(String name, String timezone) {
    try {
      ZoneId zoneId = ZoneId.of(timezone);
      CreateCalendarHandler handler = new CreateCalendarHandler(container);
      CreateCalendarCommandData data = new CreateCalendarCommandData(name, zoneId);
      handler.handle(data);
      // make the newly created calendar the active one so the view selects it immediately
      try {
        container.setActiveCalendar(name);
      } catch (Exception ignore) {
        // if setting active fails for any reason, ignore and continue to refresh
      }
      handleRefresh();
    } catch (Exception e) {
      // provide a more helpful message even when e.getMessage() is null
      String msg = e.getMessage();
      if (msg == null || msg.isEmpty()) {
        msg = e.toString();
      }
      view.renderError("Error: Failed to create calendar: " + msg);
      e.printStackTrace();
    }
  }

  @Override
  public void handleEditCalendar(String calendarName, String property, String newValue) {
    try {
      EditCalendarHandler handler = new EditCalendarHandler(container);
      EditCalendarCommandData data = new EditCalendarCommandData(calendarName, property, newValue);
      handler.handle(data);
      handleRefresh();
    } catch (Exception e) {
      view.renderError("Error: Failed to edit calendar: " + e.getMessage());
    }
  }

  @Override
  public void handlePreviousMonth() {
    currentMonth = currentMonth.minusMonths(1);
    handleRefresh();
  }

  @Override
  public void handleNextMonth() {
    currentMonth = currentMonth.plusMonths(1);
    handleRefresh();
  }

  @Override
  public void handleDateSelected(LocalDate date) {
    selectedDate = date;
    handleRefresh();
  }

  @Override
  public void handleCreateEvent(String subject, LocalDateTime startDateTime,
                                LocalDateTime endDateTime, String description, String location,
                                String status, boolean isRepeating, String repeatDays,
                                LocalDate repeatEndDate) {
    try {
      String result = createEvent(subject, startDateTime, endDateTime, description,
          location, status, isRepeating, repeatDays, repeatEndDate);
      if (isError(result)) {
        view.renderError(result);
      } else {
        handleRefresh();
      }
    } catch (Exception e) {
      view.renderError("Error: " + e.getMessage());
    }
  }


  @Override
  public void handleEditEvent(String property, String subject, LocalDateTime startDateTime,
                              LocalDateTime endDateTime, String newValue, String scope) {
    try {
      String result = editEvent(property, subject, startDateTime, endDateTime, newValue, scope);
      if (isError(result)) {
        view.renderError(result);
      } else {
        handleRefresh();
      }
    } catch (Exception e) {
      view.renderError("Error: " + e.getMessage());
    }
  }

  private String editEvent(String property, String subject, LocalDateTime startDateTime,
                           LocalDateTime endDateTime, String newValue, String scope) {
    try {
      AdvancedCalendar activeCalendar = container.getActiveCalendar();
      if (activeCalendar == null) {
        return "Error: No active calendar";
      }

      String result;
      if ("single".equals(scope)) {
        EditEventHandler handler = new EditEventHandler(activeCalendar.getCalendar());
        EditEventCommandData data = new EditEventCommandData(
            property, subject, startDateTime, endDateTime, newValue);
        result = handler.handle(data);
      } else if ("from".equals(scope)) {
        EditMultipleEventsHandler handler = new EditMultipleEventsHandler(
            activeCalendar.getCalendar());
        EditMultipleEventsCommandData data = new EditMultipleEventsCommandData(
            property, subject, startDateTime, newValue);
        result = handler.handle(data);
      } else {
        EditSeriesHandler handler = new EditSeriesHandler(activeCalendar.getCalendar());
        EditSeriesCommandData data = new EditSeriesCommandData(
            property, subject, startDateTime, newValue);
        result = handler.handle(data);
      }
      return result;
    } catch (Exception e) {
      return "Error: Failed to edit event: " + e.getMessage();
    }
  }

  @Override
  public void handleDeleteEvent(String subject, LocalDateTime startDateTime,
                                LocalDateTime endDateTime, String scope) {
    try {
      String result = deleteEvent(subject, startDateTime, endDateTime, scope);
      if (isError(result)) {
        view.renderError(result);
      } else {
        handleRefresh();
      }
    } catch (Exception e) {
      view.renderError("Error: " + e.getMessage());
    }
  }

  private String deleteEvent(String subject, LocalDateTime startDateTime,
                             LocalDateTime endDateTime, String scope) {
    try {
      AdvancedCalendar activeCalendar = container.getActiveCalendar();
      if (activeCalendar == null) {
        return "Error: No active calendar";
      }

      String result;
      if ("single".equals(scope)) {
        DeleteEventHandler handler = new DeleteEventHandler(activeCalendar.getCalendar());
        DeleteEventCommandData data = new DeleteEventCommandData(
            subject, startDateTime, endDateTime);
        result = handler.handle(data);
      } else if ("from".equals(scope)) {
        DeleteMultipleEventsHandler handler = new DeleteMultipleEventsHandler(
            activeCalendar.getCalendar());
        DeleteMultipleEventsCommandData data = new DeleteMultipleEventsCommandData(
            subject, startDateTime);
        result = handler.handle(data);
      } else {
        DeleteSeriesHandler handler = new DeleteSeriesHandler(activeCalendar.getCalendar());
        DeleteSeriesCommandData data = new DeleteSeriesCommandData(subject, startDateTime);
        result = handler.handle(data);
      }
      return result;
    } catch (Exception e) {
      return "Error: Failed to delete event: " + e.getMessage();
    }
  }

  @Override
  public void handleExportCalendar(String fileName) {
    String result = exportCalendar(fileName);
    if (isError(result)) {
      view.renderError(result);
    }
    view.render(result);
  }

  private String exportCalendar(String fileName) {
    try {
      AdvancedCalendar activeCalendar = container.getActiveCalendar();
      if (activeCalendar == null) {
        return "Error: No active calendar";
      }
      ExportEventHandler handler = new ExportEventHandler(activeCalendar.getCalendar());
      ExportCommandData data = new ExportCommandData(fileName);
      return handler.handle(data);
    } catch (Exception e) {
      return "Error: Failed to export: " + e.getMessage();
    }
  }

  @Override
  public void handleRefresh() {
    currentMonth = view.getCurrentMonth();
    selectedDate = view.getSelectedDate();

    List<String> calendarNames = getCalendarNames();
    String activeCalendarName = getActiveCalendarName();
    String timezone = getActiveCalendarTimezone();

    // Update view with data
    view.setCalendars(calendarNames, activeCalendarName);

    Map<LocalDate, List<ViewEvent>> monthEvents = getEventsForMonth(currentMonth);
    view.setMonthEvents(currentMonth, monthEvents);

    List<ViewEvent> dayEvents = handleGetEventsForDay(selectedDate);
    view.setDayEvents(selectedDate, dayEvents);

    view.getTopPanel().updateTimezone(timezone);
    view.getTopPanel().updateMonthYear(currentMonth);
  }

  @Override
  public List<ViewEvent> handleGetEventsForDay(LocalDate selectedDate) {
    List<ViewEvent> result = new ArrayList<>();
    AdvancedCalendar activeCalendar = container.getActiveCalendar();
    if (activeCalendar == null) {
      return result;
    }
    LocalDateTime dayStart = selectedDate.atStartOfDay();
    LocalDateTime dayEnd = selectedDate.atTime(23, 59, 59);
    List<EventReadOnly> events = activeCalendar.getCalendar().getEvents(dayStart, dayEnd);

    for (EventReadOnly event : events) {
      result.add(convertToEventInfo(event));
    }
    return result;
  }

  public Map<LocalDate, List<ViewEvent>> getEventsForMonth(LocalDate currentMonth) {
    Map<LocalDate, List<ViewEvent>> result = new HashMap<>();
    AdvancedCalendar activeCalendar = container.getActiveCalendar();
    if (activeCalendar == null) {
      return result;
    }

    LocalDate firstDay = currentMonth.withDayOfMonth(1);
    LocalDate lastDay = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth());
    LocalDateTime start = firstDay.atStartOfDay();
    LocalDateTime end = lastDay.atTime(23, 59, 59);

    List<EventReadOnly> events = activeCalendar.getCalendar().getEvents(start, end);
    for (EventReadOnly event : events) {
      LocalDate eventDate = event.getStartDateTime().toLocalDate();
      result.computeIfAbsent(eventDate, k -> new ArrayList<>())
          .add(convertToEventInfo(event));
    }

    return result;
  }

  private ViewEvent convertToEventInfo(EventReadOnly event) {
    return new ViewEventImpl
        .Builder()
        .setSubject(event.getSubject())
        .setStartDateTime(event.getStartDateTime())
        .setEndDateTime(event.getEndDateTime())
        .setDescription(event.getDescription())
        .setLocation(event.getLocation().name())
        .setStatus(event.getEventStatus().name())
        .setAllDay(event.isAllDay())
        .setIsSeries(event.getEventType() == calendar.model.datatypes.TypeOfEvent.SERIES)
        .build();
  }

  private String getActiveCalendarTimezone() {
    AdvancedCalendar activeCalendar = container.getActiveCalendar();
    return activeCalendar.getZoneId().getId();
  }

  @Override
  public String getActiveCalendarName() {
    AdvancedCalendar activeCalendar = container.getActiveCalendar();
    return activeCalendar.getName();
  }

  private List<String> getCalendarNames() {
    List<String> names = new ArrayList<>(container.getCalendars().keySet());
    java.util.Collections.sort(names);
    return names;
  }

  private String createEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                             String description, String location, String status,
                             boolean isRepeating, String repeatDays, LocalDate repeatEndDate) {
    try {
      AdvancedCalendar activeCalendar = container.getActiveCalendar();

      CreateEventHandler handler = new CreateEventHandler(activeCalendar.getCalendar());
      CreateCommandData data;

      if (isRepeating) {
        data = new CreateCommandData(subject, startDateTime,
            endDateTime, repeatDays, "until",
            repeatEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), true);
      } else {
        data = new CreateCommandData(subject, startDateTime, endDateTime, true);
      }

      String result = handler.handle(data);

      List<EventReadOnly> createdEvents = activeCalendar
          .getCalendar()
          .getEvents(startDateTime, endDateTime.plusDays(30));

      for (EventReadOnly event : createdEvents) {
        if (event.getSubject().equals(subject)
            && event.getStartDateTime().equals(startDateTime)) {
          List<EventReadOnly> eventsToEdit = new ArrayList<>();
          eventsToEdit.add(event);
          if (!description.equals("No description given")) {
            activeCalendar.getCalendar().editEvent(eventsToEdit, "description", description);
          }
          if (!location.equals("UNKNOWN")) {
            activeCalendar.getCalendar().editEvent(eventsToEdit, "location", location);
          }
          if (!status.equals("UNKNOWN")) {
            activeCalendar.getCalendar().editEvent(eventsToEdit, "status", status);
          }
          break;
        }
      }
      return result;
    } catch (Exception e) {
      return "Error: Failed to create event: " + e.getMessage();
    }
  }

  private static boolean isError(String result) {
    return result != null
        && (result.startsWith("Error")
        || result.contains("Failed")
        || result.contains("Error"));
  }
}
