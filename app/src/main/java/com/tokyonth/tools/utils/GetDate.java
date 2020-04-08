package com.tokyonth.tools.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetDate {

    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

}
