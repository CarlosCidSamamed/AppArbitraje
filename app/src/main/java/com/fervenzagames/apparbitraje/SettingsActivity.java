package com.fervenzagames.apparbitraje;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
        setContentView(R.layout.activity_settings);

        mImagen = (CircleImageView) findViewById(R.id.settings_image);
        mNombre = (TextView) findViewById(R.id.settings_nombre);
        mCargo = (TextView) findViewById(R.id.settings_cargo);
        mNivel = (TextView) findViewById(R.id.settings_nivel);
        mImagenBtn = (Button) findViewById(R.id.settings_change_image);
        mDatosBtn = (Button) findViewById(R.id.settings_change_data);

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

                Uri resultUri = result.getUri();
                // Para que el nombre del archivo de la foto de perfil coincida con el current_user uid debemos obtenerlo a partir de FirebaseAuth.
                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = mCurrentUser.getUid();
                StorageReference filepath = mImageStorage.child("profileImages").child(uid + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this, "Subiendo Imagen...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, "¡¡Error al subir la Imagen!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
