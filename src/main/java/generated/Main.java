package generated;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Main {
    public static void main(String[] args) {


        String invalidDate = "2005-08-09T18:31:42";
        String valid1 = "2001-07-04T12:08:56.235Z";
        String valid2 = "2001-07-04T12:08:56.235+01:00";

        System.out.println(isValidISODateTime(invalidDate));
        System.out.println(isValidISODateTime(valid1));
        System.out.println(isValidISODateTime(valid2));

//        OffsetDateTime invalidOffset = OffsetDateTime.parse(invalidDate, DateTimeFormatter.ISO_DATE_TIME);
        OffsetDateTime offsetDateTime1 = OffsetDateTime.parse(valid1);
        OffsetDateTime offsetDateTime2 = OffsetDateTime.parse(valid2);


        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dt = LocalDateTime.parse(invalidDate, formatter);

        Instant invalidInstance = dt.toInstant(ZoneOffset.UTC);
        Instant instant1 = offsetDateTime1.toInstant();
        Instant instant2 = offsetDateTime2.toInstant();


        System.out.println("instant1  " + instant1);
        System.out.println("offset1   " + offsetDateTime1);
        System.out.println("instant2  " + instant2);
        System.out.println("offset2   " + offsetDateTime2);
        System.out.println("localdatetime  " + dt);
        System.out.println("invalidInstant   " + invalidInstance);


        System.out.println("=================================");

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        OffsetDateTime now = OffsetDateTime
                .parse(OffsetDateTime.now(ZoneId.of("Z"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        System.out.println("offset now: " + now);

    }

    public static boolean isValidISODateTime(String date) {
        try {
            DateTimeFormatter.ISO_DATE_TIME.parse(date);
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }
}

