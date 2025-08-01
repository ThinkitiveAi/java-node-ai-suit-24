package com.thinkitve.aidemo.util;

import org.springframework.stereotype.Component;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.util.ArrayList;
import java.util.List;

@Component
public class TimezoneUtil {

    public ZonedDateTime convertToUTC(LocalDate date, LocalTime time, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        LocalDateTime localDateTime = LocalDateTime.of(date, time);
        return localDateTime.atZone(zoneId).withZoneSameInstant(ZoneOffset.UTC);
    }

    public ZonedDateTime convertFromUTC(ZonedDateTime utcTime, String targetTimezone) {
        ZoneId targetZoneId = ZoneId.of(targetTimezone);
        return utcTime.withZoneSameInstant(targetZoneId);
    }

    public LocalDateTime convertToLocalDateTime(ZonedDateTime zonedDateTime, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        return zonedDateTime.withZoneSameInstant(zoneId).toLocalDateTime();
    }

    public List<LocalDate> generateRecurringDates(LocalDate startDate, LocalDate endDate, 
                                                 String recurrencePattern, int maxOccurrences) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;
        int count = 0;

        while (!currentDate.isAfter(endDate) && count < maxOccurrences) {
            dates.add(currentDate);
            count++;

            switch (recurrencePattern.toUpperCase()) {
                case "DAILY":
                    currentDate = currentDate.plusDays(1);
                    break;
                case "WEEKLY":
                    currentDate = currentDate.plusWeeks(1);
                    break;
                case "MONTHLY":
                    currentDate = currentDate.plusMonths(1);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid recurrence pattern: " + recurrencePattern);
            }
        }

        return dates;
    }

    public boolean isValidTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    public boolean isDSTTransition(LocalDate date, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        ZoneRules rules = zoneId.getRules();
        LocalDateTime dateTime = date.atStartOfDay();
        
        return rules.isDaylightSavings(dateTime.toInstant(ZoneOffset.UTC)) != 
               rules.isDaylightSavings(dateTime.plusDays(1).toInstant(ZoneOffset.UTC));
    }

    public String formatTimeForTimezone(LocalTime time, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        LocalDateTime dateTime = LocalDate.now().atTime(time);
        ZonedDateTime zonedDateTime = dateTime.atZone(zoneId);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return zonedDateTime.format(formatter);
    }

    public Duration calculateDuration(LocalTime startTime, LocalTime endTime) {
        LocalDateTime startDateTime = LocalDate.now().atTime(startTime);
        LocalDateTime endDateTime = LocalDate.now().atTime(endTime);
        
        if (endTime.isBefore(startTime)) {
            endDateTime = endDateTime.plusDays(1);
        }
        
        return Duration.between(startDateTime, endDateTime);
    }

    public boolean isTimeRangeValid(LocalTime startTime, LocalTime endTime) {
        return !startTime.equals(endTime) && !startTime.isAfter(endTime);
    }
} 