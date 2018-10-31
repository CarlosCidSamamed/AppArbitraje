package com.fervenzagames.apparbitraje.Add_Activities;

import android.app.DatePickerDialog;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.R;
import com.fervenzagames.apparbitraje.Utils.DatePickerFragment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddCompetidorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Toolbar mToolbar;

    // FECHA NACIMIENTO
    private Button mElegirFechBtn;
    private TextView mFechaTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_competidor);

        mToolbar = (Toolbar) findViewById(R.id.add_competidor_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Añadir Competidor");


        mElegirFechBtn = (Button) findViewById(R.id.add_comp_fechaNacBtn);
        mFechaTextView = (TextView) findViewById(R.id.add_comp_fechaNacText);

        mElegirFechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
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
        String currentDateString = DateUtils.formatDateTime(this, cal.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
        //String currentDateString = DateFormat.getDateInstance().format(cal.getTime()); // El formato de esta fecha dependerá del idioma de cada terminal.
        mFechaTextView.setText(currentDateString);
    }
}
