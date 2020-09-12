package com.jantonioc.xallyapp.FragmentsComanda;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jantonioc.ln.Categoria;
import com.jantonioc.ln.Orden;
import com.jantonioc.ln.ResultadoWS;
import com.jantonioc.xallyapp.Adaptadores.CategoriaAdapter;
import com.jantonioc.xallyapp.FragmentsOrdenes.Categorias;
import com.jantonioc.xallyapp.FragmentsOrdenes.DetalleMenu;
import com.jantonioc.xallyapp.FragmentsOrdenes.Ordenes;
import com.jantonioc.xallyapp.MainActivity;
import com.jantonioc.xallyapp.R;
import com.jantonioc.xallyapp.Retrofit.NetworkClient;
import com.jantonioc.xallyapp.Retrofit.UploadAPI;
import com.jantonioc.xallyapp.VolleySingleton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.jantonioc.xallyapp.Constans.URLBASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgregarComanda extends Fragment {

    private View rootView;
    private ImageView imagencomanda;
    private ProgressBar progressBar;

    //Permisos
    private static final int REQUEST_CAMERA_AND_WRITE_EXTERNAL = 10;
    private static final int REQUEST_FROM_CAMERA = 1;

    //Carpetas para las comandas
    private final String CARPETA_RAIZ = "XalliAPP/";
    private final String CARPETA_IMAGENES = "Comandas";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + CARPETA_IMAGENES;

    //ruta del archivo
    private String path="";
    boolean permisos = false;

    //archivo bitma para la imagen
    private Bitmap bitmap = null;

    //
    boolean nuevaimagen=false;


    public AgregarComanda() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_new_photo_comanda, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.add_comanda:
                tomarfoto();
                return true;

            case R.id.save_phto:
                uploadImage();
                return  true;

                default:
                   return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {

        MenuItem guardar = menu.findItem(R.id.save_phto);

        if(path.isEmpty())
        {
            guardar.setEnabled(false);
            guardar.getIcon().setAlpha(130);
        }
        else
        {
            guardar.setEnabled(true);
            guardar.getIcon().setAlpha(255);
        }

        MenuItem tomar = menu.findItem(R.id.add_comanda);

        if(permisos)
        {
            tomar.setEnabled(true);
            tomar.getIcon().setAlpha(255);
        }
        else
        {
            tomar.setEnabled(false);
            tomar.getIcon().setAlpha(130);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Cambiando el toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Guardar Comanda");

        //ocualtando el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        rootView = inflater.inflate(R.layout.fragment_agregar_comanda, container, false);

        imagencomanda = rootView.findViewById(R.id.imagencomanda);

        progressBar = rootView.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        consultarFoto();

        validarPermisos();

        return rootView;
    }

    private void consultarFoto() {

        String uri = URLBASE+"ComandaWS/consultarFoto/"+MainActivity.comanda.getIdorden();
        StringRequest request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    //respuesta de parte del servidor
                    String ruta = jsonObject.getString("Mensaje");
                    Boolean resultado = jsonObject.getBoolean("Resultado");

                    if (resultado) {
                        nuevaimagen = true;
                        //memory policy y network pilicy para que no este siempre mostrando la misma foto
                        Picasso.with(getContext()).load(ruta).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside().into(imagencomanda, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(rootView.getContext(), "Error al obtener la imagen", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else
                    {
                        progressBar.setVisibility(View.GONE);
                    }

                } catch (JSONException ex) {

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(rootView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }

    private void validarPermisos() {

        //viejos dispositivos
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permisos = true;
        } else {
            //nuevos dispositivos
            if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                permisos = true;
            } else {
                //pedir el permiso de la camara
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_AND_WRITE_EXTERNAL);
            }
        }
        requireActivity().invalidateOptionsMenu();
    }

    private void tomarfoto() {

        String nombreImagen = "";
        File fileImagen = new File(getContext().getExternalFilesDir(null), RUTA_IMAGEN);
        boolean isCreada = fileImagen.exists();


        if (isCreada == false) {
            isCreada = fileImagen.mkdirs();
        }

        if (isCreada == true) {
            nombreImagen = MainActivity.comanda.getNombrecompleto() + MainActivity.comanda.getIdorden() + ".png";
        }

        path = getContext().getExternalFilesDir(null) + File.separator + RUTA_IMAGEN + File.separator + nombreImagen;

        File imagen = new File(path);

        Intent intent = null;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = getContext().getPackageName() + ".provider";
            Uri imageUri = FileProvider.getUriForFile(getContext(), authorities, imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }

        startActivityForResult(intent, REQUEST_FROM_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQUEST_FROM_CAMERA:
                    MediaScannerConnection.scanFile(rootView.getContext(), new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });

                    bitmap = BitmapFactory.decodeFile(path);
                    imagencomanda.setImageBitmap(bitmap);
                    requireActivity().invalidateOptionsMenu();
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CAMERA_AND_WRITE_EXTERNAL:
                if (permissions.length == 2 && grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    permisos = true;
                } else {
                    permisos = false;
                }
                requireActivity().invalidateOptionsMenu();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void uploadImage()
    {
        if(nuevaimagen)
        {
            //Mostrar un dialog
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Actualizar foto");
            builder.setMessage("¿Desea Actulizar la foto para esta orden?");

            builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    enviarimg();
                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            builder.create();
            builder.show();
        }
        else
        {
            enviarimg();
        }
    }

    private void enviarimg()
    {
        //ocultar la img
        imagencomanda.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);

        File file = new File(path);

        Retrofit retrofit = NetworkClient.getRetrofit();

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);

        MultipartBody.Part photo = MultipartBody.Part.createFormData("photo",file.getName(),requestBody);

        RequestBody ordenid = RequestBody.create(MediaType.parse("text/plain"),String.valueOf(MainActivity.comanda.getIdorden()));

        UploadAPI uploadAPI = retrofit.create(UploadAPI.class);

        uploadAPI.uploadImage(photo,ordenid).enqueue(new Callback<ResultadoWS>() {
            @Override
            public void onResponse(Call<ResultadoWS> call, retrofit2.Response<ResultadoWS> response) {

                if (response.isSuccessful()) {

                    String msj = response.body().getMensaje();
                    boolean resultado = response.body().isResultado();

                    if (resultado) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(rootView.getContext(), msj, Toast.LENGTH_SHORT).show();
                        //Abrir el fragmento del detalle de los platillos
                        Fragment fragment = new ClientesComanda();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.commit();
                    } else {
                        imagencomanda.setVisibility(View.VISIBLE);
                        Toast.makeText(rootView.getContext(), msj, Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResultadoWS> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                imagencomanda.setVisibility(View.VISIBLE);
                Toast.makeText(rootView.getContext(),t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

//        uploadAPI.uploadImage(photo).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//
//                if (response.isSuccessful()) {
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(rootView.getContext(), "Almacenado con Exito", Toast.LENGTH_SHORT).show();
//                    Fragment fragment = new ClientesComanda();
//                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.content, fragment);
//                    transaction.commit();
//                } else
//                {
//                    Toast.makeText(rootView.getContext(),"ah ocurrido un error inesperado intente de nuevo", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                    imagencomanda.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                imagencomanda.setVisibility(View.VISIBLE);
//                Toast.makeText(rootView.getContext(),t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

//        Call<ResultadoWS> call = uploadAPI.uploadImage(photo);
//
//        call.enqueue(new Callback<ResultadoWS>() {
//            @Override
//            public void onResponse(Call<ResultadoWS> call, retrofit2.Response<ResultadoWS> response) {
//
//                if(response.isSuccessful())
//                {
//                    String msj = response.body().getMensaje();
//                    boolean resultado = response.body().isResultado();
//
//                    if(resultado)
//                    {
//                        //progressBar.setVisibility(View.GONE);
//                        Toast.makeText(rootView.getContext(), msj, Toast.LENGTH_SHORT).show();
//                        //Abrir el fragmento del detalle de los platillos
//                        Fragment fragment = new ClientesComanda();
//                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.content, fragment);
//                        transaction.commit();
//                    }
//                    else
//                    {
//                        imagencomanda.setVisibility(View.VISIBLE);
//                        Toast.makeText(rootView.getContext(), msj, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            @Override
//            public void onFailure(Call<ResultadoWS> call, Throwable t) {
//                imagencomanda.setVisibility(View.VISIBLE);
//                Toast.makeText(rootView.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

}
