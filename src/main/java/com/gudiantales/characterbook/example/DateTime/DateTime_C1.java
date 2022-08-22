package com.gudiantales.characterbook.example.DateTime;

import org.apache.tomcat.jni.Local;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class DateTime_C1 {

    public static void main(String[] args) {
        Instant instant = Instant.now();
        System.out.println("DateTime_C1.main Instant ::"+instant); //기준시 UTC GMT
        System.out.println(" === " + instant.atZone(ZoneId.of("UTC")));

        ZoneId zone = ZoneId.systemDefault();
        System.out.println("zone = " + zone);

        ZonedDateTime zonedDateTime = instant.atZone(zone);
        System.out.println("zonedDateTime = " + zonedDateTime);

        /*
        DateTime_C1.main Instant ::2022-08-22T00:47:30.313890900Z
        === 2022-08-22T00:47:30.313890900Z[UTC]
        zone = Asia/Seoul
        zonedDateTime = 2022-08-22T09:47:30.313890900+09:00[Asia/Seoul]
         */

        LocalDateTime now = LocalDateTime.now();
        System.out.println("now = " + now);
        //LocalDateTime.of()

        ZonedDateTime nowInKorea = ZonedDateTime.now(zone);
        System.out.println("nowInKorea = " + nowInKorea);

        LocalDate localDateTo = LocalDate.now();
        LocalDate localDateFrom = LocalDate.of(2022, Month.JULY, 16);

        Period between = Period.between(localDateTo, localDateFrom);
        System.out.println("between = " + between.getDays());

        Period until = localDateTo.until(localDateFrom);
        System.out.println("until = " + until.get(ChronoUnit.DAYS));

        Instant one = Instant.now();
        Instant two = one.plus(10, ChronoUnit.SECONDS);
        Duration between1 = Duration.between(one, two);
        System.out.println("between1 = " + between1.getSeconds());


    }
}
