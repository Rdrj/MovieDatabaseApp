package com.example.android.popularmovies.utilities;

import android.content.Context;

import com.example.android.popularmovies.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.example.android.popularmovies.utilities.Constant.ZERO;

public class FormatUtils {

    /**
     * This method will convert an integer number to a String in a certain format, commas in Numbers.
     * Returns a String with a comma after every third digit from right to left.
     * (e.g. 1000 -> 1,000)
     */
    public static String formatNumber(int number) {
        DecimalFormat decimalFormat = new DecimalFormat(Constant.PATTERN_FORMAT_NUMBER);
        return decimalFormat.format(number);
    }

    /**
     * This method will convert an integer number to a String in a certain format.
     * Returns a String with a comma after every third digit from right to left and add $ at the very front.
     * (e.g. 100000000 -> $100,000,000)
     */
    public static String formatCurrency(long number) {
        DecimalFormat decimalFormat = new DecimalFormat(Constant.PATTERN_FORMAT_CURRENCY);
        return decimalFormat.format(number);
    }

    /**
     * This method will used to format a date.
     * (e.g. 2018-06-23 -> Jun 23, 2018)
     */
    public static String formatDate(String releaseDate) {
        // Create a SimpleDateFormat instance
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                Constant.PATTERN_FORMAT_DATE_INPUT, Locale.getDefault());
        // Parse the release date into a Date object
        Date date = null;
        try {
            date = dateFormat.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Initialize a SimpleDateFormat instance
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(
                Constant.PATTERN_FORMAT_DATE_OUTPUT, Locale.getDefault());
        // Convert the date "yyyy-MM-dd" to the date "MMM dd, yyy"
        return outputDateFormat.format(date);
    }

    /**
     *  This method will convert minutes to hours and minutes.
     *  (e.g. 112 min -> 1h 52m, 120 min -> 2h)
     */
    public static String formatTime(Context context, int runtime) {
        long hours = TimeUnit.MINUTES.toHours(runtime);
        long minutes = runtime - TimeUnit.HOURS.toMinutes(hours);
        if (minutes == ZERO) {
            return String.format(Locale.getDefault(), context.getString(R.string.format_runtime_hours), hours);
        }
        return String.format(Locale.getDefault(), context.getString(R.string.format_runtime), hours, minutes);
    }
}
