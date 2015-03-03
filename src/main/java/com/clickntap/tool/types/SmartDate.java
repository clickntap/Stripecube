package com.clickntap.tool.types;

//import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class SmartDate {

    private DateTime t;

    public SmartDate() {
        t = new DateTime();
    }

    public SmartDate(long millis) {
        t = new DateTime(millis);
    }

    public static void main(String args[]) {
        // SmartDate sd = null;
        // sd = new SmartDate();
        // System.out.println(sd);
        // sd = new SmartDate(1381708800000L);
        // System.out.println(sd);
        System.out.println(new Datetime(1381233708911L));
    }

    public String toString() {
        return t.toDateTime(DateTimeZone.UTC).toString();
    }

}
