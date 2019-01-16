package com.fervenzagames.apparbitraje.User_Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Android Layout
    private CircleImageView mImagen;
    private TextView mNombre;
    private TextView mCargo;
    private TextView mNivel;
    private Button mImagenBtn;
    private Button mDatosBtn;

    //Progress Dialog
    private ProgressDialog mProgressDialog;

    private final static int GALLERY_PICK = 1;

    //Storage Firebase
    private StorageReference mImageStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(detectarTipoDispostivo() == 0){ // MÓVIL
            setContentView(R.layout.phone_user_settings);

            mImagen = findViewById(R.id.phone_settings_image);
            mNombre = findViewById(R.id.phone_settings_nombre);
            mCargo = findViewById(R.id.phone_settings_cargo);
            mNivel = findViewById(R.id.phone_settings_nivel);
            mImagenBtn = findViewById(R.id.phone_settings_change_image);
            mDatosBtn = findViewById(R.id.phone_settings_change_data);

        } else if(detectarTipoDispostivo() == 1){ // TABLET
            setContentView(R.layout.activity_settings);

            mImagen = findViewById(R.id.settings_image);
            mNombre = findViewById(R.id.settings_nombre);
            mCargo = findViewById(R.id.settings_cargo);
            mNivel = findViewById(R.id.settings_nivel);
            mImagenBtn = findViewById(R.id.settings_change_image);
            mDatosBtn = findViewById(R.id.settings_change_data);
        }

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(current_uid);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String nombre = dataSnapshot.child("nombre").getValue().toString();
                String nivel = dataSnapshot.child("nivel").getValue().toString();
                String cargo = dataSnapshot.child("cargo").getValue().toString();
                String imagen = dataSnapshot.child("imagen").getValue().toString();
                String imagen_thumb = dataSnapshot.child("imagen_thumb").getValue().toString();

                mNombre.setText(nombre);
                mNivel.setText(nivel);
                mCargo.setText(cargo);

                //Imagen
                Picasso.get().load(imagen).into(mImagen);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Al pulsar el botón para cambiar IMAGEN
        mImagenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Seleccionar Imagen"), GALLERY_PICK);
            }
        });
        // Al pulsar el botón para cambiar DATOS
        mDatosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vamos a obtener los datos de nivel y cargo para poder mostrar los valores actuales en el formulario de modificación
                String nivel_val = mNivel.getText().toString();
                String cargo_val = mCargo.getText().toString();

                Intent datosIntent = new Intent(SettingsActivity.this, CargoNivelActivity.class);
                datosIntent.putExtra("nivel_val", nivel_val);
                datosIntent.putExtra("cargo_val", cargo_val);
                startActivity(datosIntent);
            }
        });
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

                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Subiendo Imagen...");
                mProgressDialog.setMessage("Espere mientras se sube la imagen...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                final Uri resultUri = result.getUri();
                // Para que el nombre del archivo de la foto de perfil coincida con el current_user uid debemos obtenerlo a partir de FirebaseAuth.
                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                final String uid = mCurrentUser.getUid();
                final StorageReference filepath = mImageStorage.child("profileImages").child(uid + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            // Toast.makeText(SettingsActivity.this, "Subiendo Imagen...", Toast.LENGTH_SHORT).show();
                            mImageStorage.child("profileImages").child(uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String download_Url = uri.toString();

                                    mUserDatabase.child("imagen").setValue(download_Url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                mProgressDialog.dismiss();
                                                Toast.makeText(SettingsActivity.this, "Éxito al subir la imagen.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(SettingsActivity.this, "ERROR al subir la imagen.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });


                        } else {
                            Toast.makeText(SettingsActivity.this, "¡¡Error al subir la Imagen!!", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    // Este método se encarga de detectar el tamaño de la pantalla para saber si el dispostivo es un móvil o una tablet.
    // MÓVIL <7 pulgadas TABLET 7 o más.
    // MÓVIL  --> 0
    // TABLET --> 1
    public int detectarTipoDispostivo(){

        int tipo = 0;
        int screenWidth = 0;
        int screenHeight = 0;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        double x = Math.pow(screenWidth / dm.xdpi, 2);
        double y = Math.pow(screenHeight / dm.xdpi, 2);

        double screenInches = Math.sqrt(x + y);
        screenInches = (double) Math.round(screenInches * 10) / 10;

        //Toast.makeText(this, "Tamaño en Pulgadas de la Pantalla --> " + screenInches, Toast.LENGTH_LONG).show();

        if(screenInches < 7.0f)
        {
            tipo = 0; // MÓVIL
        } else if (screenInches >= 7.0f){
            tipo = 1; // TABLET
        }

        return tipo;
    }
}
