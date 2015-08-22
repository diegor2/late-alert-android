package net.startapi.latealert;

import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.maps.model.Duration;
import com.google.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

/**
 * Metodos utilitarios para Google Calendar API
 *
 * @author TaTi Lattanzi
 */
public class CalendarUtils {

    private static final String TAG = CalendarUtils.class.getSimpleName();

    /**
     * Compara o tempo do percurso com o horario de cada evento da lista.
     *
     * @param items   os eventos
     * @param origins LatIng de origem (GPS)
     */
    public void compareEvent(List<Event> items, LatLng origins) {
        if (null == items || null == origins) return;

        for (Event event : items) {
            DateTime start = event.getStart().getDateTime() != null ? event.getStart().getDateTime() : event.getStart().getDateTime();
            if (start == null) return;

            Calendar cEvent = getCalendarEvent(start);
            //Verifica se o evento ocorre no mesmo dia, mes e ano

            String location = event.getLocation();
            if (location == null) {
                location = "<unknown>";
            }

            Log.d(TAG, "Event " + event.getDescription() + " @ " + location
                                    + " on " + start.toStringRfc3339());

            //if (compareCalendar(cEvent)) {

            DistanceMatrixServices distanceServices = new DistanceMatrixServices();
            Duration timeToArrive = distanceServices.getDurationMatrix(origins, location);
            Long secondsToArrive = (null != timeToArrive) ? timeToArrive.inSeconds : 0;

            //Converte Millis to Seconds
            Long secondsToStartEvent = (System.currentTimeMillis() - cEvent.getTimeInMillis()) / 1000;
            Log.d(TAG, "secondsToStartEvent " + secondsToStartEvent);

            String humanReadable = (null!=timeToArrive) ? timeToArrive.humanReadable : "<null>";
            String message =  AlertApp.getCredential().getSelectedAccountName()
                        + " está atrasado.  Previsão de chegada " + humanReadable
                        + " Evento " + event.getDescription() + " @ " + location
                        + " começa: " + start.toStringRfc3339();

                Log.d(TAG, message);

                if ((secondsToArrive > secondsToStartEvent) && (null != event)) {
                    String  recipients  = getEmailTo(event);

                    String mailApi = "https://fast-basin-1765.herokuapp.com/mailx.php?emails="
                            + recipients + "&message=" + message.replace(" ", "+");
                    Log.e(TAG, "sendmail " + mailApi);

                    try{
                        URL url = new URL(mailApi);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        try {
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        } catch (Exception e){
                            Log.e(TAG, "sendmail", e);
                        } finally {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e){
                        Log.e(TAG, "sendmail", e);
                    }

                }

            //}
        }
    }

    /**
     * Retorna a data e horario do evento em um objeto Calendar.
     *
     * @param startDate
     * @return <code>Calendar</code> do evento
     */
    public Calendar getCalendarEvent(DateTime startDate) {
        Calendar cEvento = Calendar.getInstance();

        String[] dateArray = startDate.toString().replaceAll("T", "-").replaceAll(":", "-").replaceAll("\\.", "-").split("-");
        if (dateArray.length > 0) {
            int ano = dateArray[0] != null && !dateArray[0].isEmpty() ? Integer.parseInt(dateArray[0]) : 0;
            int mes = dateArray[1] != null && !dateArray[1].isEmpty() ? Integer.parseInt(dateArray[1]) : 0;
            int dia = dateArray[2] != null && !dateArray[2].isEmpty() ? Integer.parseInt(dateArray[2]) : 0;
            int hora = 0;
            int min = 0;
            int seg = 0;

            //Data sem hora
            if (dateArray.length > 3) {
                hora = dateArray[3] != null && !dateArray[3].isEmpty() ? Integer.parseInt(dateArray[3]) : 0;
                min = dateArray[4] != null && !dateArray[4].isEmpty() ? Integer.parseInt(dateArray[4]) : 0;
                seg = dateArray[5] != null && !dateArray[5].isEmpty() ? Integer.parseInt(dateArray[5]) : 0;
            }

            cEvento.set(ano, mes, dia, hora, min, seg);
        }

        return cEvento;
    }

    /**
     * Compara com o dia, mes e ano atual.
     *
     * @param cEvent o calendario do evento
     * @return true, se for igual
     */
    public boolean compareCalendar(Calendar cEvent) {
        Calendar c = Calendar.getInstance();
        if (c.get(c.DAY_OF_MONTH) == cEvent.get(cEvent.DAY_OF_MONTH)
                && c.get(c.MONTH) == cEvent.get(cEvent.MONTH)
                && c.get(c.YEAR) == cEvent.get(cEvent.YEAR)) {
            return true;
        }

        return false;
    }

    /**
     * Retorna um array de Strings com os destinatarios do e-mail.
     *
     * @param event
     * @return destinarios do e-mail
     */
    public String getEmailTo(Event event) {
        String emailTo = "";;

        for (EventAttendee attendee : event.getAttendees()) {
            emailTo += attendee.getEmail() + ",";
        }

        return emailTo;
    }

}
