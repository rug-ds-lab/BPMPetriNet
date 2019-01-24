package nl.rug.ds.bpm.expression.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nick van Beest on 24 Jan. 2019
 *
 */
public class TimestampConvert {

	public static String toDate(long ldate) {
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		
		return sd.format(new Date(ldate));
	}
	
	public static long toLong(String sdate) {
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

		try {
			return sd.parse(sdate).getTime();
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
}