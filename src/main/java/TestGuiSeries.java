import calendar.controller.handlers.CreateEventHandler;
import calendar.controller.commanddata.CreateCommandData;
import calendar.model.CalendarImpl;
import calendar.model.interfaces.CalendarEditable;
import calendar.model.interfaces.CalendarContainer;
import calendar.model.CalendarContainerImpl;
import calendar.view.CalendarGuiViewImpl;
import calendar.controller.guicontroller.CalendarGuiController;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import calendar.view.ViewEvent;
import java.util.Map;

public class TestGuiSeries {
  public static void main(String[] args) {
    CalendarEditable cal = new CalendarImpl();
    CreateEventHandler handler = new CreateEventHandler(cal);

    LocalDateTime start = LocalDateTime.of(2025, Month.NOVEMBER, 3, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, Month.NOVEMBER, 3, 10, 0);
    String days = "MWF";
    String repeatType = "until";
    String repeatValue = "2025-11-17";
    CreateCommandData data = new CreateCommandData("SeriesGuiTest", start, end, days, repeatType, repeatValue, true);
    System.out.println(handler.handle(data));

    // create controller and view, then push month events
    CalendarContainer container = new CalendarContainerImpl();
    // add an advanced calendar that wraps our cal? But using container requires AdvancedCalendarImpl; skip and just query cal directly
    CalendarGuiViewImpl view = new CalendarGuiViewImpl();
    Map<java.time.LocalDate, List<ViewEvent>> map = new java.util.HashMap<>();

    // convert cal.getEvents for the month to ViewEvent map using controller logic
    LocalDateTime monthStart = start.withDayOfMonth(1).withHour(0).withMinute(0);
    LocalDateTime monthEnd = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59);
    List<calendar.model.interfaces.EventReadOnly> events = cal.getEvents(monthStart, monthEnd);
    for (calendar.model.interfaces.EventReadOnly e : events) {
      java.time.LocalDate d = e.getStartDateTime().toLocalDate();
      map.computeIfAbsent(d, k -> new java.util.ArrayList<>()).add(new calendar.view.ViewEventImpl.Builder()
          .setSubject(e.getSubject())
          .setStartDateTime(e.getStartDateTime())
          .setEndDateTime(e.getEndDateTime())
          .setIsSeries(e.getEventType() == calendar.model.datatypes.TypeOfEvent.SERIES)
          .build());
    }

    view.setMonthEvents(start.toLocalDate().withDayOfMonth(1), map);

    System.out.println("Done setting month events to view");
  }
}

