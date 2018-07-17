package com.tacuba.comicsmanager.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

//import com.tacuba.dto.ComicDto;
//import com.tacuba.dto.PreferenciasDto;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.tacuba.comicsmanager.dto.InfoComic;

/**
 * Created by mendoedg on 23/02/2015.
 */
public class Utility {
    public static ArrayList<String> nameOfEvent = new ArrayList<String>();
    public static ArrayList<String> startDates = new ArrayList<String>();
    public static ArrayList<String> endDates = new ArrayList<String>();
    public static ArrayList<String> descriptions = new ArrayList<String>();

    public static ArrayList<String> readCalendarEvent(Context context) {
        Cursor cursor = context.getContentResolver()
                .query(Uri.parse("fechaRec://com.android.calendar/events"),
                        new String[] { "calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation" }, null,
                        null, null);
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        for (int i = 0; i < CNames.length; i++) {

            nameOfEvent.add(cursor.getString(1));
            startDates.add(getDate(Long.parseLong(cursor.getString(3))));
            endDates.add(getDate(Long.parseLong(cursor.getString(4))));
            descriptions.add(cursor.getString(2));
            CNames[i] = cursor.getString(1);
            cursor.moveToNext();

        }
        return nameOfEvent;
    }

    /*
    private static void addToCalendar(Context context, ComicDto comicDto,PreferenciasDto preferenciasDto) {
        String title = comicDto.getTitulo();
        Calendar cal_start = MyDateUtils.convertirStrFecha(comicDto.getFecha());

        cal_start.set(Calendar.HOUR_OF_DAY, preferenciasDto.getHoraInicio());
        cal_start.set(Calendar.MINUTE, preferenciasDto.getMinutoInicio());

        Calendar cal_end =  MyDateUtils.convertirStrFecha(comicDto.getFecha());
        cal_end.add(Calendar.MINUTE, preferenciasDto.getMinutosAlarma());

        final ContentResolver cr = context.getContentResolver();
        Cursor cursor ;
        if (Build.VERSION.SDK_INT >= 8 )
            cursor = cr.query(Uri.parse("fechaRec://com.android.calendar/calendars"), new String[]{ "_id", "calendar_displayName" }, null, null, null);
        else
            cursor = cr.query(Uri.parse("fechaRec://calendar/calendars"), new String[]{ "_id", "calendar_displayName" }, null, null, null);
        if ( cursor.moveToFirst() ) {
            final String[] calNames = new String[cursor.getCount()];
            final int[] calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                cursor.moveToNext();
            }

            ContentValues cv = new ContentValues();
            cv.put("calendar_id", calIds[1]);
            cv.put("title", title);
            cv.put("dtstart", cal_start.getTimeInMillis() );
            cv.put("hasAlarm", 1);
            cv.put("dtend", cal_end.getTimeInMillis());
            cv.put("eventTimezone", TimeZone.getDefault().getID());
            Uri newEvent ;
            if (Build.VERSION.SDK_INT >= 8 )
                newEvent = cr.insert(Uri.parse("fechaRec://com.android.calendar/events"), cv);
            else
                newEvent = cr.insert(Uri.parse("fechaRec://calendar/events"), cv);

            if (newEvent != null) {
                long tituloRec = Long.parseLong( newEvent.getLastPathSegment() );
                ContentValues values = new ContentValues();
                values.put( "event_id", tituloRec );
                values.put( "method", 1 );
                values.put( "minutes", 1 ); // minutes

                if (Build.VERSION.SDK_INT >= 8 )
                    cr.insert( Uri.parse( "fechaRec://com.android.calendar/reminders" ), values );
                else
                    cr.insert( Uri.parse( "fechaRec://calendar/reminders" ), values );

            }
        }
        cursor.close();
    }
*/
    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static void mostrarMensaje(Activity activity, String mensaje) {
        Toast.makeText(activity,mensaje,Toast.LENGTH_LONG).show();
    }

    public static float calcularTotal(ArrayList<InfoComic> listSelected) {
        float total =0;
        if(listSelected!= null && listSelected.size() >0){
            for (InfoComic tmp :listSelected) {
                total+=tmp.getComicDto().getPrecio();
            }
        }
        return total;
    }
}
