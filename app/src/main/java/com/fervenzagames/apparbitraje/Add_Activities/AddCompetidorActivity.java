package com.fervenzagames.apparbitraje.Add_Activities;

import android.app.DatePickerDialog;

import android.provider.CalendarContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import java.text.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.R;
import com.fervenzagames.apparbitraje.Utils.DatePickerFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddCompetidorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Toolbar mToolbar;

    // FECHA NACIMIENTO
    private Button mElegirFechBtn;
    private TextView mFechaTextView;

    // Edad y Categoría de Edad
    private TextView mEdad;
    private TextView mCatEdad;

    private int anhoNac; // En estas variables globales almacenaré la fecha que se obtiene con el datepicker para poder calcular la edad del competidor (y la categoría de edad).
    private int mesNac;
    private int diaNac;

    private boolean fechaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_competidor);

        mToolbar = (Toolbar) findViewById(R.id.add_competidor_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Añadir Competidor");

        mElegirFechBtn = (Button) findViewById(R.id.add_comp_fechaNacBtn);
        mFechaTextView = (TextView) findViewById(R.id.add_comp_fechaNacText);

        mEdad = (TextView) findViewById(R.id.add_comp_edadText);
        mCatEdad = (TextView) findViewById(R.id.add_comp_catEdadText);

        fechaSeleccionada = false;
        anhoNac = 0;
        mesNac = 0;
        diaNac = 0;

        Toast.makeText(AddCompetidorActivity.this, " Año -- > " + anhoNac, Toast.LENGTH_SHORT).show();

        mElegirFechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                // fechaSeleccionada = true;
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        /*Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);*/
        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        anhoNac = year;
        mesNac = month;
        mesNac++; // Los meses tienen base 0, es decir, van de 0 a 11.
        diaNac = dayOfMonth;
        String currentDateString = DateUtils.formatDateTime(this, cal.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
        //String currentDateString = DateFormat.getDateInstance().format(cal.getTime()); // El formato de esta fecha dependerá del idioma de cada terminal.
        mFechaTextView.setText(currentDateString);
        int edad = calcularEdad(anhoNac, mesNac, diaNac);
        String edadStr = Integer.toString(edad);
        mEdad.setText(edadStr);
        asignarCategoriaEdad(edad, null);
    }


    // una vez que se obtiene la fecha de nacimiento del competidor (mediante el DatePicker) se calculará la edad y se asginará la categoría de edad.
    // Cada uno de esos datos se mostrará en el textView correspondiente en pantalla.
    public int calcularEdad(int anho, int mes, int dia){

        int edad = 0;

        // Obtener la fecha actual a partir del calendario
        Calendar cal = Calendar.getInstance();
        int anhoActual = cal.get(Calendar.YEAR);
        int mesActual = cal.get(Calendar.MONTH);
        mesActual++; // Los meses tienen base 0, es decir, van de 0 a 11.
        int diaActual = cal.get(Calendar.DAY_OF_MONTH);
/*        Toast.makeText(AddCompetidorActivity.this, "Fecha Actual --> " + diaActual + "/" + mesActual + "/" + anhoActual, Toast.LENGTH_SHORT).show();
        Toast.makeText(AddCompetidorActivity.this, "Fecha Nacimiento --> " + dia + "/" + mes + "/" + anho, Toast.LENGTH_SHORT).show();*/
        //String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime());

        // Calcular la EDAD

        edad = anhoActual - anho;

        // Toast.makeText(AddCompetidorActivity.this, "EDAD --> " + edad , Toast.LENGTH_SHORT).show();

        if((mesActual < mes) || ((mesActual == mes) && (diaActual <= dia))){
            edad = edad - 1;
        }

        if(edad < 0){
            Toast.makeText(AddCompetidorActivity.this, "La fecha seleccionada es posterior a la fecha actual. Corrija la fecha de nacimiento.", Toast.LENGTH_LONG).show();
            edad = 0;
        } else if (edad > 80) {
            Toast.makeText(AddCompetidorActivity.this, "Según la normativa no puede haber ningún competidor cuya edad supere los 80 años. Compruebe la fecha de nacimiento.", Toast.LENGTH_LONG).show();
        }

        return edad;
    }

    // Una vez calculada la edad a partir de la fecha de Nacimiento se asignará al competidor la categoría de EDAD que le corresponde en el año actual.
    // Dependiendo de la modalidad de competición existen edades mínimas. Por ejemplo, en Sanda, la categoría de edad mínima es Junior, es decir, de 15 a 17 años.
    public void asignarCategoriaEdad(int edad, String modalidad){
        String catEdad = "";
        if((edad <= 8) && (edad >= 4)){
            catEdad = "01_PreInfantil";
        } else if ((edad >= 9) && (edad <= 11)){
            catEdad = "02_Infantil";
        } else if ((edad >= 12) && (edad <= 14)){
            catEdad = "03_Cadete";
        } else if ((edad >= 15) && (edad <= 17)){
            catEdad = "04_Junior";
        } else if ((edad >= 18) && (edad <= 40)){
            catEdad = "05_Absoluto";
        } else if ((edad >= 41) && (edad <= 50)){
            catEdad = "06_Sénior A (+40)";
        } else if ((edad >= 51) && (edad <= 60)){
            catEdad = "07_Sénior B (+50)";
        } else if ((edad >= 61) && (edad <= 70)){
            catEdad = "08_Sénior C (+60)";
        } else if ((edad >= 71) && (edad <= 80)){
            catEdad = "09_Sénior D (+70)";
        }

        // Asignar la categoría de Edad al TextView correspondiente.
        mCatEdad.setText(catEdad);
    }
}
