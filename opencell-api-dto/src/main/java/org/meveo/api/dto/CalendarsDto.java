package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class CalendarsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Calendars")
@XmlAccessorType(XmlAccessType.FIELD)
public class CalendarsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6354285812403951307L;

    /** The calendar. */
    private List<CalendarDto> calendar = new ArrayList<>();

    /**
     * Gets the calendar.
     *
     * @return the calendar
     */
    public List<CalendarDto> getCalendar() {
        if (calendar == null)
            calendar = new ArrayList<>();
        return calendar;
    }

    /**
     * Sets the calendar.
     *
     * @param calendar the new calendar
     */
    public void setCalendar(List<CalendarDto> calendar) {
        this.calendar = calendar;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CalendarsDto [calendar=" + calendar + "]";
    }

}
