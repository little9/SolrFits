package org.solrfits;

import org.apache.commons.lang.time.FastDateFormat;

import java.util.TimeZone;

/**
 * Created by jlittle on 7/8/16.
 */
public class DateHandler {
    private static final FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");


    public String getXMLSchemaDate(long seconds) {
        return fastDateFormat.format(Long.valueOf(seconds));
    }
}
