package com.example.javog.sesion.Actividades;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.javog.sesion.Datos.User;
import com.example.javog.sesion.R;
import com.example.javog.sesion.crypto.MessageCrypto;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.example.javog.sesion.Actividades.LoginActivity.LOGIN_DESCRIPTION;
import static com.example.javog.sesion.Actividades.LoginActivity.LOGIN_IMAGE;
import static com.example.javog.sesion.Actividades.LoginActivity.LOGIN_NAME;
import static com.example.javog.sesion.Actividades.LoginActivity.LOGIN_PHONE;

public class ConfigurarPerfil extends AppCompatActivity {
    private EditText etNombre, etCorreo, etCel, etDes, etPass, etPass2;
    private MobileServiceClient mClient;
    private MobileServiceTable<User> tabla;
    private SharedPreferences settings;
    boolean status = false;
    private ImageView ivFoto;
    private TextView tvUrl;

    private String uploadKey = "javo_el_pollo_loko";

    private RequestQueue requestQueue;

    private String imageName;

    private File imagePath;

    private Bitmap upImage;

    private boolean imageChanged = false;

    private static final int SELECT_PHOTO = 100;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    //TODO: 1.- Obtenemos permisos de storage
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //TODO: 2.- Obtenemos permisos de cámara
    private static final int REQUEST_CAMERA = 200;
    private static final String FILE_PROVIDER = "com.example.javog.sesion.fileprovider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar_perfil);

        settings = getApplicationContext().getSharedPreferences(LoginActivity.SHARED_PREFS_SESSION, MODE_PRIVATE);

        etNombre = (EditText) findViewById(R.id.editNombreConfigurar);
        etNombre.setText(settings.getString(LOGIN_NAME, null));
        etCorreo = (EditText) findViewById(R.id.editCorreoConfigurar);
        etCorreo.setText(settings.getString(LoginActivity.LOGIN_EMAIL, null));
        etPass   = (EditText) findViewById(R.id.editContraConfigurar);
        etPass.setText(settings.getString(LoginActivity.LOGIN_PASSWORD, null));
        etCel    = (EditText)findViewById(R.id.editTelefonoConfigurar);
        etCel.setText(settings.getString(LOGIN_PHONE, null));
        etDes    = (EditText)findViewById(R.id.editDescripcionConfigurar);
        etDes.setText(settings.getString(LOGIN_DESCRIPTION, null));
        imageName = settings.getString(LOGIN_IMAGE, "");

        requestQueue = Volley.newRequestQueue(this);

        Button btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        Button btnGaleria = (Button) findViewById(R.id.btnGaleria);
        Button btnModificar = (Button) findViewById(R.id.btnModify);
        Button btnCancelar  = (Button) findViewById(R.id.btnCancelarChange);

        tvUrl = (TextView) findViewById(R.id.tvUrl);
        ivFoto = (ImageView) findViewById(R.id.ivFoto);

        initAzureClient();

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TestConection(ConfigurarPerfil.this)) {
                    String nombre = etNombre.getText().toString();
                    String correo = etCorreo.getText().toString();
                    String password = etPass.getText().toString();
                    String cel = etCel.getText().toString();
                    String desc = etDes.getText().toString();

                    if (nombre.equals("") || correo.equals("") || password.equals("") || cel.equals("") || desc.equals("")) {
                        Toast.makeText(ConfigurarPerfil.this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show();
                    } else {
                        int c = Integer.parseInt(cel);
                        String passCrypto = new MessageCrypto().GenerateHash(password, MessageCrypto.HASH_SHA256);
                        actualizarItem(correo, passCrypto, nombre, desc, c, password);
                    }
                } else {
                    Toast.makeText(ConfigurarPerfil.this, "No hay conexion a Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigurarPerfil.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 4.- Mandamos a validar permisos
                validarPermisosStorage();
            }
        });

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 10.- Mandamos a validar permisos
                validarPermisosCamara();
            }
        });
    }

    private void initAzureClient(){
        try{
            mClient = new MobileServiceClient("https://aadsdewf.azurewebsites.net", this);
            Log.d("initAzureClient",  "cliente de Azure inicializado");
        }catch (MalformedURLException mue){
            Log.d("initClientAzure", mue.getMessage());
        }
        tabla = mClient.getTable(User.class);
    }

    public void actualizarItem(final String email, final String passCript, final String name, final String description, final int phone, final String password){
        final User item = new User(email, passCript, name, description, phone, imageName);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    SharedPreferences config = getApplicationContext().getSharedPreferences(LoginActivity.SHARED_PREFS_SESSION, MODE_PRIVATE);
                    String id = config.getString(LoginActivity.LOGIN_ID, null);

                    item.setId(id);
                    tabla.update(item).get();
                    status = true;
                    SharedPreferences.Editor editor = config.edit();
                    editor.putString(LoginActivity.LOGIN_EMAIL, email);
                    editor.putString(LoginActivity.LOGIN_PASSWORD, password);
                    editor.putString(LoginActivity.LOGIN_NAME, name);
                    editor.putString(LoginActivity.LOGIN_DESCRIPTION, description);
                    editor.putString(LoginActivity.LOGIN_PHONE, String.valueOf(phone));
                    editor.putString(LoginActivity.LOGIN_IMAGE, imageName);
                    editor.commit();
                } catch (InterruptedException e) {
                    status = false;
                    e.printStackTrace();
                    Log.d("george", e.getMessage());
                } catch (ExecutionException e) {
                    status = false;
                    e.printStackTrace();
                    Log.d("george", e.getMessage());
                } catch (Exception e) {
                    status = false;
                    e.printStackTrace();
                    Log.d("george", e.getMessage());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //adapter.notifyItemRemoved(i); adapter.notifyItemRangeChanged(i, items.size());
                    }
                });
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //ImprimirStatus(status);
                if(imageChanged){
                    UploadImage(convert(upImage), imageName, uploadKey);
                } else {
                    ImprimirStatus(status);
                }
            }
        }.execute();
    }

    private void ImprimirStatus(boolean correcto){
        if (correcto){
            Toast.makeText(ConfigurarPerfil.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ConfigurarPerfil.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(ConfigurarPerfil.this, "No se pudieron actualizar los datos", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean TestConection(Context context) {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    public void validarPermisosStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Debemos mostrar un mensaje?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Mostramos una explicación de que no aceptó dar permiso para acceder a la librería
            } else {
                // Pedimos permiso
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            }
        }else{
            iniciarIntentSeleccionarFotos();
        }
    }

    //TODO: 6.- Llama al intent para seleccionar fotos
    private void iniciarIntentSeleccionarFotos() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    //TODO: 7.- Revisamos si se le dio permiso al usuario o no
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                // Si la petición se cancela se regresa un arreglo vacío
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido
                    iniciarIntentSeleccionarFotos();
                } else {
                    // Permiso negado
                }
                return;
            // TODO 14.- Validamos el permiso para el acceso a la cámara
            case REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Se concedió acceso
                    iniciarIntentTomarFoto();
                } else {
                    // permiso negado
                }
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            //TODO: 8.- Si obtuvimos una imagen entonces la procesamos
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri imagenSeleccionada = imageReturnedIntent.getData();
                    try{
                        InputStream imagenStream = getContentResolver().openInputStream(imagenSeleccionada);
                        Bitmap imagen = BitmapFactory.decodeStream(imagenStream);
                        ivFoto.setImageBitmap(imagen);
                        tvUrl.setText(imagenSeleccionada.toString());

                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        imageName = "JPEG_" + timeStamp + "_";
                        imageChanged = true;
                        upImage = imagen;

                    }catch (FileNotFoundException fnte){
                        Toast.makeText(this, fnte.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                else {
                    tvUrl.setText("");
                }
                // TODO 15.- Si obtuvimos la imagen y la guardamos la mostramos
            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK){
                    //File image = new File(imagePath.getPath());
                    Toast.makeText(this, imagePath.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    //BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    //Bitmap bitmap = BitmapFactory.decodeFile(imagePath.getAbsolutePath(),bmOptions);
                    Bitmap bitmap = decodeSmallBitmap(imagePath, 640, 640);
                    //bitmap = Bitmap.createBitmap(bitmap);
                    ivFoto.setImageBitmap(bitmap);

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    imageName = "JPEG_" + timeStamp + "_";
                    imageChanged = true;
                    upImage = bitmap;
                    //Picasso.with(this).load(tvUrl.getText().toString()).into(ivFoto);
                }
                return;
        }
    }

    //TODO: 11.- Validamos permisos de cámara
    public void validarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
        }else{
            iniciarIntentTomarFoto();
        }
    }

    //TODO: 12.- Iniciamos intent para tomar foto
    private  void iniciarIntentTomarFoto(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Validamos que hay una actividad de cámara
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Creamos un nuevo objeto para almacenar la foto
            File photoFile = null;
            Uri photoURI;
            try {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){
                    photoFile = createImageFile();
                    if (photoFile != null) {
                        photoURI = Uri.fromFile(photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, REQUEST_CAMERA);
                    }
                } else {
                    photoFile = createImageFile2();
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(this, FILE_PROVIDER, photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, REQUEST_CAMERA);
                    }
                }
            }catch (NullPointerException npe){
                Toast.makeText(this,"Error en file provider",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO: 13.- Función para crear archivo y URL
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreImagen = "JPEG_" + timeStamp + "_";
        File imagesFolder = new File( Environment.getExternalStorageDirectory(), "OpWin");
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
        }
        File image = new File(imagesFolder, nombreImagen+".jpg");
        imagePath = image;
        String urlName = "file://" + image.getAbsolutePath();
        tvUrl.setText(urlName);
        return image;
    }

    private File createImageFile2() throws IOException {
        // Creamos el archivo
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreImagen = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                nombreImagen,  /* prefijp */
                ".jpg",         /* sufijo */
                storageDir      /* directorio */
        );
        imagePath = image;


        // Obtenemos la URL
        String urlName = "file://" + image.getAbsolutePath();
        tvUrl.setText(urlName);
        return image;
    }

    // Image Tools

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSmallBitmap(File file, int reqWidth, int reqHeight){
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);

    }

    /*protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        bUpload = (Button) findViewById(R.id.button);

        requestQueue = Volley.newRequestQueue(this);

        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = null;
                try {
                    InputStream ims = getAssets().open("doge.jpg");

                    bitmap = BitmapFactory.decodeStream(ims);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                String data = convert(bitmap);
                UploadImage(data, editText.getText().toString(), editText2.getText().toString());
            }
        });
    }*/

    private void UploadImage(final String data, final String imagename, final String key){
        String url = "http://trabajosweb.azurewebsites.net/postImg.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        ImprimirStatus(status);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        error.printStackTrace();
                        ImprimirStatus(status);
                        Toast.makeText(ConfigurarPerfil.this, "La Imagen no se pudo guardar.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("image", data);
                params.put("imagename", imagename);
                params.put("key", key);

                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    private String convert(Bitmap bitmap)
    {
        Toast.makeText(this, bitmap.getWidth() + ", " + bitmap.getHeight(), Toast.LENGTH_SHORT).show();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}
