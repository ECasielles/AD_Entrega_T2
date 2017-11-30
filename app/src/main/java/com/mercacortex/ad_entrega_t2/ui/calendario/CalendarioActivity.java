package com.mercacortex.ad_entrega_t2.ui.calendario;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.mercacortex.ad_entrega_t2.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Date;

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
public class CalendarioActivity extends AppCompatActivity {

    DatePicker datePicker;
    Date startDate, endDate;
    Button btnCalc, btnBack;
    Date maxDate = new Date(18,6,21);
    Date minDate = new Date(17,9,15);
    CalendarRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        repository = CalendarRepository.getInstance(this);
        btnCalc = (Button) findViewById(R.id.btnCalc);
        btnBack = (Button) findViewById(R.id.btnBack);
        datePicker = (DatePicker) findViewById(R.id.dpSchool);

        DateTime today = DateTime.now();
        Date date = new Date(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth());
        isHoliday(date);

        initialize();
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBack:
                initialize();
                break;
            case R.id.btnCalc:
                if(startDate != null && endDate != null) {
                    int minIndex = 0;
                    int maxIndex = 0;
                    while()
                }

                break;
        }
    }

    void initialize(){
        startDate = null;
        endDate = null;
        datePicker.setMinDate(minDate.getTime());
        datePicker.setMaxDate(maxDate.getTime());
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date selectedDate = new Date(
                        ((DatePicker) view).getYear(),
                        ((DatePicker) view).getMonth(),
                        ((DatePicker) view).getDayOfMonth());
                isHoliday(selectedDate);
            }
        });
        datePicker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(startDate == null) {
                    startDate = new Date(
                            ((DatePicker) view).getYear(),
                            ((DatePicker) view).getMonth(),
                            ((DatePicker) view).getDayOfMonth()
                    );
                    ((DatePicker) view).setMinDate(startDate.getTime());
                } else {
                    endDate = new Date(
                            ((DatePicker) view).getYear(),
                            ((DatePicker) view).getMonth(),
                            ((DatePicker) view).getDayOfMonth()
                    );
                    ((DatePicker) view).setMaxDate(endDate.getTime());
                }
                return true;
            }
        });
    }

    void isHoliday(Date date){
        DateTime dateTime = new DateTime(date.getTime());
        if(dateTime.getDayOfWeek() != DateTimeConstants.SATURDAY &&
                dateTime.getDayOfWeek() != DateTimeConstants.SUNDAY &&
                repository.contains(date))
            Toast.makeText(this, "Hoy no es lectivo", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Hoy es lectivo", Toast.LENGTH_SHORT).show();
    }

}
