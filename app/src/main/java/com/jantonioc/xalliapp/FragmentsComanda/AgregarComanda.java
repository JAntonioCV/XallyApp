package com.jantonioc.xalliapp.FragmentsComanda;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.ResultadoWS;
import com.jantonioc.xalliapp.Constans;
import com.jantonioc.xalliapp.MainActivity;
import com.jantonioc.xalliapp.R;
import com.jantonioc.xalliapp.Retrofit.NetworkClient;
import com.jantonioc.xalliapp.Retrofit.IWebServicesAPI;
import com.jantonioc.xalliapp.VolleySingleton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.jantonioc.xalliapp.Constans.URLBASE;

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

    //para innabilitar o habilitar botones
    boolean nuevaimagen=false;
    boolean imagen = false;
    boolean ruta = true;

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

        //al seleccionar una de las opciones del toolbar
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

        //para habilitt o innabilitar los botones del toolbar
            MenuItem guardar = menu.findItem(R.id.save_phto);

            if(!path.isEmpty() && ruta)
            {
                guardar.setEnabled(true);
                guardar.getIcon().setAlpha(255);
            }
            else
            {
                guardar.setEnabled(false);
                guardar.getIcon().setAlpha(130);
            }

            MenuItem tomar = menu.findItem(R.id.add_comanda);

            if(permisos && imagen)
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

        //vista
        rootView = inflater.inflate(R.layout.fragment_agregar_comanda, container, false);

        //imagen
        imagencomanda = rootView.findViewById(R.id.imagencomanda);

        progressBar = rootView.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        //consulta si tiene foto asociado a esa orden
        consultarFoto();

        //consulta si tiene los permisos
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
                                //si se procesa la descarga de la imagen
                                imagen = true;
                                requireActivity().invalidateOptionsMenu();
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                //si falla
                                imagen = true;
                                progressBar.setVisibility(View.GONE);
                                requireActivity().invalidateOptionsMenu();
                                Toast.makeText(rootView.getContext(), "Error al obtener la imagen", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else
                    {
                        //Muestro la imagen aunque no cargue
                        imagen = true;
                        requireActivity().invalidateOptionsMenu();
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
                Toast.makeText(rootView.getContext(),Constans.errorVolley(error), Toast.LENGTH_SHORT).show();

            }
        })
        {
            //metodo para la autenficacion basica en el servidor
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Constans.getToken();
            }
        };

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

    //abrir la cama para tomar fotos
    private void tomarfoto() {

        //nombre de la imagen, y al archivo le pasamos la ruta
        String nombreImagen = "";
        File fileImagen = new File(getContext().getExternalFilesDir(null), RUTA_IMAGEN);
        boolean isCreada = fileImagen.exists();


        //si no esta creada
        if (isCreada == false) {
            isCreada = fileImagen.mkdirs();
        }

        //si esta creada
        if (isCreada == true) {
            //le ponemos el nombre del cliente y el id de la orden
            nombreImagen = MainActivity.comanda.getNombrecompleto() + MainActivity.comanda.getIdorden() + ".png";
        }

        //sacamos el path
        path = getContext().getExternalFilesDir(null) + File.separator + RUTA_IMAGEN + File.separator + nombreImagen;

        //creamos archivo con la direccion
        File imagen = new File(path);

        // abrimos el inten de la camara para capturar la imagen
        Intent intent = null;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //el provehedor que nos permite obtener archivos de imagenes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = getContext().getPackageName() + ".provider";
            Uri imageUri = FileProvider.getUriForFile(getContext(), authorities, imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }

        //esperamos el resutl
        startActivityForResult(intent, REQUEST_FROM_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //si el resultado es ok obtenemos la path de la imagen se escanea
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQUEST_FROM_CAMERA:
                    MediaScannerConnection.scanFile(rootView.getContext(), new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });

                    //convertimos a bitmar y lo pasamos a la imageview
                    bitmap = BitmapFactory.decodeFile(path);
                    imagencomanda.setImageBitmap(bitmap);
                    requireActivity().invalidateOptionsMenu();
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //piediendo los permisos nesesarios
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

    //metodo para subir la iamgen
    private void uploadImage()
    {
        imagen = false;
        ruta = false;
        requireActivity().invalidateOptionsMenu();

        if(nuevaimagen)
        {
            //Mostrar un dialog si realmente quiere actualizar o no
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
                    imagen = true;
                    ruta = true;
                    requireActivity().invalidateOptionsMenu();
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

    //enviar imagen
    private void enviarimg()
    {
        //ocultar la img
        imagencomanda.setVisibility(View.GONE);

        //poner viisble el progress
        progressBar.setVisibility(View.VISIBLE);

        //obtener un nuevo archivo con la direcion
        File file = new File(path);

        //obtener la instancia de retrofit
        Retrofit retrofit = NetworkClient.getRetrofit();

        //cabecera del tipo de dato
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);

        //la foto para que sea enviada como un formulario
        MultipartBody.Part photo = MultipartBody.Part.createFormData("photo",file.getName(),requestBody);

        //el id de la orden
        RequestBody ordenid = RequestBody.create(MediaType.parse("text/plain"),String.valueOf(MainActivity.comanda.getIdorden()));

        //Creacioin de la interfaz de retrofit para traer los servicios
        IWebServicesAPI iwebServicesAPI = retrofit.create(IWebServicesAPI.class);

        //pasamos la foto y el id
        iwebServicesAPI.uploadComanda(photo,ordenid).enqueue(new Callback<ResultadoWS>() {
            @Override
            public void onResponse(Call<ResultadoWS> call, retrofit2.Response<ResultadoWS> response) {

                //si es completada sin error
                if (response.isSuccessful()) {

                    //llamamos el mensaje y el resultado
                    String msj = response.body().getMensaje();
                    boolean resultado = response.body().isResultado();

                    //si es true
                    if (resultado) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(rootView.getContext(), msj, Toast.LENGTH_SHORT).show();
                        //Abrir el fragmento del detalle de los platillos
                        Fragment fragment = new ClientesComanda();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.commit();
                    } else {
                        //si es falso
                        imagencomanda.setVisibility(View.VISIBLE);
                        Toast.makeText(rootView.getContext(), msj, Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //si falla
            @Override
            public void onFailure(Call<ResultadoWS> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                imagencomanda.setVisibility(View.VISIBLE);
                Toast.makeText(rootView.getContext(),t.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });
    }

}
