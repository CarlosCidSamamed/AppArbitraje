package com.fervenzagames.apparbitraje.Add_Activities;

import android.app.DatePickerDialog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.R;
import com.fervenzagames.apparbitraje.Utils.DatePickerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddCompetidorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;

    // Nombre, Apellidos, DNI / Pasaporte
    private TextInputLayout mNombre;
    private TextInputLayout mAp1;
    private TextInputLayout mAp2;
    private TextInputLayout mDNI;

    // Federación, Escuela / Club, País
    private TextInputLayout mFed;
    private TextInputLayout mEsc;
    private TextInputLayout mPais;

    // FECHA NACIMIENTO
    private Button mElegirFechBtn;
    private TextView mFechaTextView;

    // Edad y Categoría de Edad
    private TextView mEdad;
    private TextView mCatEdad;
    // Peso y Categoría de Peso
    private TextInputLayout mPeso;
    private TextView mCatPeso;
    private Button mAsignarCatPesoBtn;
    // Altura y Envergadura
    private TextInputLayout mAltura;
    private TextInputLayout mEnv;

    // Imagen
    private CircleImageView mFoto;
    private Button mCambiarFotoBtn;
    private final static int GALLERY_PICK = 1;
    private StorageReference mImagenStorage; // Referencia a Firebase Storage para poder almacenar las fotos de los competidores.

    private DatabaseReference mCompetidorDB;

    private Button mGuardarBtn;


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

        mNombre = (TextInputLayout) findViewById(R.id.add_comp_nombreInput);
        mAp1 = (TextInputLayout) findViewById(R.id.add_comp_ap1Input);
        mAp2 = (TextInputLayout) findViewById(R.id.add_comp_ap2Input);
        mDNI = (TextInputLayout) findViewById(R.id.add_comp_dniInput);

        mFed = (TextInputLayout) findViewById(R.id.add_comp_federacionInput);
        mEsc = (TextInputLayout) findViewById(R.id.add_comp_escuelaClubInput);
        mPais = (TextInputLayout) findViewById(R.id.add_comp_paisInput);

        mElegirFechBtn = (Button) findViewById(R.id.add_comp_fechaNacBtn);
        mFechaTextView = (TextView) findViewById(R.id.add_comp_fechaNacText);

        mEdad = (TextView) findViewById(R.id.add_comp_edadText);
        mCatEdad = (TextView) findViewById(R.id.add_comp_catEdadText);
        mAsignarCatPesoBtn = (Button) findViewById(R.id.add_comp_asignarCatPesoBtn);

        mPeso = (TextInputLayout) findViewById(R.id.add_comp_pesoInput);
        mCatPeso = (TextView) findViewById(R.id.add_comp_catPesoText);

        mAltura = (TextInputLayout) findViewById(R.id.add_comp_alturaInput);
        mEnv = (TextInputLayout) findViewById(R.id.add_comp_envergaduraInput);

        mFoto = (CircleImageView) findViewById(R.id.add_comp_foto);
        mCambiarFotoBtn = (Button) findViewById(R.id.add_comp_elegirFotoBtn);
        mImagenStorage = FirebaseStorage.getInstance().getReference();
        mCompetidorDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Competidores");

        mGuardarBtn = (Button) findViewById(R.id.add_comp_guardarDatos_btn);

        fechaSeleccionada = false;
        anhoNac = 0;
        mesNac = 0;
        diaNac = 0;

        mElegirFechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                // fechaSeleccionada = true;
            }
        });

        mAsignarCatPesoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String pesoStr = mPeso.getEditText().getText().toString();
                    if(!TextUtils.isEmpty(pesoStr)){ // Si se ha introducido un peso antes de pulsar el botón...
                        Float peso = Float.parseFloat(pesoStr); // Convertir el String en un Float...
                        asignarCategoriaPeso(peso); // Invocar al método.
                    } else {
                        Toast.makeText(AddCompetidorActivity.this, "Introduzca un peso antes de pulsar el botón para asignar la Categoría de PESO.", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        if(!TextUtils.isEmpty(mDNI.getEditText().getText().toString())){
            mCambiarFotoBtn.setVisibility(View.INVISIBLE);
        } else {
            mCambiarFotoBtn.setVisibility(View.VISIBLE);
        }
        mCambiarFotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignarFoto();
            }
        });

        mGuardarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprobar que el formulario se haya rellenado por completo
                boolean todosDatosOK = true;
                // Nombre, Apellidos y DNI
                todosDatosOK &= comprobarDatosPersonales(); // true y lo que devuelva comprobarDatosPersonales.
                todosDatosOK &= comprobarDatosFisicos();
                todosDatosOK &= comprobarDatosFed();
                if(todosDatosOK){
                    guardarDatosCompetidor();
                }
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

    // A partir del peso que se pasa como parámetro a este método se asigna la categoría de Peso al competidor.
    public void asignarCategoriaPeso(float peso){
        String catPeso = "";
        if(peso < 48){
            catPeso = "Menos de 48 kg";
        } else if((peso >= 48) && (peso <52)){
            catPeso = "Menos de 52 kg";
        } else if((peso >= 52) && (peso < 56)){
            catPeso = "Menos de 56 kg";
        } else if((peso >= 56) && (peso < 60)){
            catPeso = "Menos de 60 kg";
        } else if((peso >= 60) && (peso < 65)){
            catPeso = "Menos de 65 kg";
        } else if((peso >= 65) && (peso < 70)){
            catPeso = "Menos de 70 kg";
        } else if((peso >= 70) && (peso < 75)){
            catPeso = "Menos de 75 kg";
        } else if((peso >= 75) && (peso < 80)){
            catPeso = "Menos de 80 kg";
        } else if((peso >= 80) && (peso < 85)){
            catPeso = "Menos de 85 kg";
        } else if((peso >= 85) && (peso < 90)){
            catPeso = "Menos de 90 kg";
        } else if(peso >= 90){
            catPeso = "90 kg o más";
        }
        // Asignar la categoria de PESO al TextView correspondiente.
        mCatPeso.setText(catPeso);
    }

    // IMAGEN DEL COMPETIDOR
    public void asignarFoto(){
        // Al pulsar el botón para elegir la foto se lanza un Intent de la Activity de la galería del dispositivo.
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryIntent, "Seleccionar Imagen"), GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            // Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(AddCompetidorActivity.this);
                mProgressDialog.setTitle("Subiendo Imagen...");
                mProgressDialog.setMessage("Espere mientras se sube la imagen...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                final Uri resultUri = result.getUri();
                // Nombre de la foto será igual al DNI / Pasaporte del Competidor. Tanto el DNI como el Pasaporte son únicos y servirán para comprobar si ya existe esa imagen.
                final String dni = mDNI.getEditText().getText().toString();
                if(!TextUtils.isEmpty(dni)){
                   final String nombreArchivo = dni;
                } else {
                    Toast.makeText(this, "Es necesario que introduzca el DNI o el Pasaporte para poder añadir una FOTO al formulario.", Toast.LENGTH_SHORT).show();
                }

                final StorageReference filepath = mImagenStorage.child("fotosCompetidores").child(dni + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            // Toast.makeText(SettingsActivity.this, "Subiendo Imagen...", Toast.LENGTH_SHORT).show();
                            mImagenStorage.child("fotosCompetidores").child(dni + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String download_Url = uri.toString();

                                    mCompetidorDB.child("foto").setValue(download_Url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                mProgressDialog.dismiss();
                                                Toast.makeText(AddCompetidorActivity.this, "Éxito al subir la imagen.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(AddCompetidorActivity.this, "ERROR al subir la imagen.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });


                        } else {
                            Toast.makeText(AddCompetidorActivity.this, "¡¡Error al subir la Imagen!!", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    // Método para mostrar la imagen del competidor una vez que se haya subido al Firebase Storage.
    public void mostrarImagen(){

    }

    // Guardar los datos del formulario en la DB
    public void guardarDatosCompetidor(){
        Toast.makeText(this, "Guardando Datos del Competidor en la BD.", Toast.LENGTH_SHORT).show();
    }
    
    // Métodos para comprobar todos los datos del formulario excepto la FOTO
    public boolean comprobarDatosPersonales(){
        boolean datosOK = false;
        // Nombre, Apellidos y DNI
        try {
            String nombre = mNombre.getEditText().getText().toString();
            String ap1 = mAp1.getEditText().getText().toString();
            String ap2 = mAp2.getEditText().getText().toString();
            if(!TextUtils.isEmpty(nombre) && (!TextUtils.isEmpty(ap1)) && (!TextUtils.isEmpty(ap2))){
                datosOK = true;
            } else {
                Toast.makeText(this, "Introduzca el nombre, ambos apellidos y el DNI antes de pulsar el botón de Guardar Datos.", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return datosOK;
    }

    public boolean comprobarDatosFisicos(){
        // Fecha de Nacimiento, Edad, Categoría de Edad, Peso, Categoría de Peso, Altura y Envergadura
        boolean datosOK = false;

        try {
            String fechaNac = mFechaTextView.getText().toString();
            String edad = mEdad.getText().toString();
            String catEdad = mCatEdad.getText().toString();
            String peso = mPeso.getEditText().getText().toString();
            String catPeso = mCatPeso.getText().toString();
            String altura = mAltura.getEditText().getText().toString();
            String env = mEnv.getEditText().getText().toString();

            if((!TextUtils.isEmpty(fechaNac)) &&
                    (!TextUtils.isEmpty(edad)) &&
                    (!TextUtils.isEmpty(catEdad)) &&
                    (!TextUtils.isEmpty(peso)) &&
                    (!TextUtils.isEmpty(catPeso)) &&
                    (!TextUtils.isEmpty(altura)) &&
                    (!TextUtils.isEmpty(env))){
                datosOK = true;
            } else {
                Toast.makeText(this, "Introduzca los datos de la parte izquierda del formulario (datos físicos) antes de pulsar el botón de Guardar Datos.", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return datosOK;
    }

    public boolean comprobarDatosFed(){
        boolean datosOK = false;

        try {
            String fed = mFed.getEditText().getText().toString();
            String esc = mEsc.getEditText().getText().toString();
            String pais = mPais.getEditText().getText().toString();
            if(!TextUtils.isEmpty(fed) && (!TextUtils.isEmpty(esc)) && (!TextUtils.isEmpty(pais))){
                datosOK = true;
            } else {
                Toast.makeText(this, "Introduzca el nombre de la federación, la escuela o club del competidor y el país antes de pulsar el botón de Guardar Datos.", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return datosOK;
    }
}
