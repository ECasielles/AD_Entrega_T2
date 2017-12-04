package com.mercacortex.ad_entrega_t2.ui.calendario;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mercacortex.ad_entrega_t2.R;
import com.squareup.timessquare.CalendarPickerView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Date;
import java.util.List;

/**
 * 3. Crear una aplicación que calcule los días lectivos en el calendario escolar del curso actual.
 * La aplicación pedirá dos fechas (dentro del curso actual) y obtendrá los días lectivos entre esas dos fechas dadas.
 * Se almacenará en un fichero en la tarjeta de memoria las fechas de todos los días lectivos
 * (en formato dd-MM-yyyy, cada día en una línea). También se mostrará en pantalla si el día de hoy es lectivo.
 * Se deberá comprobar que la tarjeta de memoria está presente y que se puede escribir en ella.
 *
 * Ayuda:
 *
 * Para pedir una fecha se puede usar la clase DatePicker.
 * Para manejar fechas se pueden usar las clases Calendar y SimpleDateFormat.
 * Un ejemplo de uso de fechas: Java Date and Calendar examples.
 * Material DateTime Picker
 * Joda-Time y en GitHub
 * ThreeTenABP y Javadoc
 * Android Date/Time tool comparison
 */
//Usando: https://github.com/square/android-times-square
public class CalendarioActivity extends AppCompatActivity {

    Button btnRestart;
    TextView txvInfo;
    Date maxDate = new Date(118,5,22);
    Date minDate = new Date(117,8,15);
    CalendarRepository repository;
    CalendarPickerView calendarPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        btnRestart = (Button) findViewById(R.id.btnRestart);
        txvInfo = (TextView) findViewById(R.id.txvInfo);
        repository = CalendarRepository.getInstance(this);

        calendarPickerView = (CalendarPickerView) findViewById(R.id.calendar_view);
        calendarPickerView.init(minDate, maxDate).inMode(CalendarPickerView.SelectionMode.RANGE);
        calendarPickerView.highlightDates(repository);
        calendarPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                List<Date> dates = calendarPickerView.getSelectedDates();
                if(dates.size() > 0) {
                    int classDays = 0;
                    for (Date tempDate : dates)
                        if (!isHoliday(tempDate))
                            classDays++;
                    txvInfo.setText("Días lectivos: " + classDays);
                }
            }
            @Override
            public void onDateUnselected(Date date) {
            }
        });

        init();
    }

    boolean isHoliday(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.getDayOfWeek() == DateTimeConstants.SUNDAY ||
                dateTime.getDayOfWeek() == DateTimeConstants.SATURDAY ||
                repository.contains(date);
    }

    void init() {
        Date today = new Date();
        calendarPickerView.selectDate(today);
        if(isHoliday(today))
            txvInfo.setText("¡Hoy no es lectivo!");
        else
            txvInfo.setText("Hoy tienes clase...");
    }

    public void onClick(View view) {
        init();
    }


}
