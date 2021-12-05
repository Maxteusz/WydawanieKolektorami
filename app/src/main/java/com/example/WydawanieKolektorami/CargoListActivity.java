package com.example.WydawanieKolektorami;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class CargoListActivity extends AppCompatActivity {

    ArrayList<Cargo> cargoList;
    MaterialButton scanButton, generateButton;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    ListCargoAdapter adapter;
    AdapterWarehouseSpinner adapterWarehouseSpinner;
    AdapterClientSpinner adapterClientSpinner;
    AutoCompleteTextView warehouseSpinner, clientSpinner;
    MaterialButton generateDocumentButton;
    Dialog generateDocumentDialog;
    AlertDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargo_list);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(CargoListActivity.this).setCaptureActivity(ScanActivity.class).setPrompt("Zeskanuj Produkt").setBarcodeImageEnabled(true).setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES).initiateScan();

            }
        });
        cargoList = new ArrayList<Cargo>();
        new IntentIntegrator(CargoListActivity.this)
                .setCaptureActivity(ScanActivity.class)
                .setPrompt("Zeskanuj Produkt").setBarcodeImageEnabled(true)
                .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                .initiateScan();
        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ListCargoAdapter(getApplicationContext(), cargoList);
        recyclerView.setAdapter(adapter);

        generateButton = findViewById(R.id.generate_button);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showGenerateDocumentDialog(CargoListActivity.this);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {

            } else {
                int index = -1;
                for (int i = 0; i < cargoList.size(); i++) {
                    if (cargoList.get(i).Ean.equals(result.getContents()))
                        index = i;
                }
                if (index >= 0) {
                    cargoList.get(index).quantity++;

                } else
                    new AddCargo(result.getContents() + "").execute();
            }
            Log.i("Zeskanowane", result.getContents() + "");
            adapter.notifyDataSetChanged();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void showDialog(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(CargoListActivity.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void showLoadingDialog(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(CargoListActivity.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        loadingDialog = builder1.create();
        loadingDialog.show();
    }

    public void generateDocument(int warehouseId, String clientName) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final String[] documet = {""};
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = "http://" + MainActivity.ipConnection + ":" + MainActivity.portConnection + "/DodajDokument/" + warehouseId + "/" + clientName + "/";
        JSONArray array = new JSONArray();
        for (int i = 0; i < cargoList.size(); i++) {

            JSONObject jsonParams = new JSONObject();
            try {
                Log.i("Dodano", "dodano");
                //Add string params
                jsonParams.put("id", cargoList.get(i).id);
                jsonParams.put("nazwa", cargoList.get(i).name);
                jsonParams.put("jednostka", cargoList.get(i).unit);
                jsonParams.put("ean", cargoList.get(i).Ean);
                jsonParams.put("ilosc", cargoList.get(i).quantity);
                Log.i("Obiekt", jsonParams + "");
                array.put(jsonParams);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final String requestBody = array.toString();

        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.i("Odczyt", response + "");
                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();
                showDialog(response);
                cargoList.clear();
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                showDialog("Wystąpił błąd przy połączeniu z serwerem");
                clientSpinner.setClickable(true);
                warehouseSpinner.setClickable(true);
                generateDocumentButton.setClickable(true);
            }
        }) {
            @Override
            protected void onFinish() {

            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.i("Błąd", "Konwersja nie udała się");
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        int socketTimeout = 30000;
        RetryPolicy _policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(_policy);

        Log.i("Błąd", req.hasHadResponseDelivered() + "");
        queue.add(req);


    }

    public ArrayList<Warehouse> getWarehousesArray() {

        ArrayList<Warehouse> warehousesArray = new ArrayList<>();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final String[] documet = {""};
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = "http://" + MainActivity.ipConnection + ":" + MainActivity.portConnection + "/PobierzMagazyny/";
        JSONArray array = new JSONArray();


        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject warehouse = (JSONObject) response
                                .get(i);
                        int id = warehouse.getInt("MagId");
                        String name = warehouse.getString("Mag_Symbol");
                        warehousesArray.add(new Warehouse(id, name));


                    } catch (JSONException e) {
                        e.printStackTrace();


                    }

                }

                Log.i("Ładowanie magazynów", "Ładwanie magazynów przebiegło pomyślnie");
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.


            }
        });

        int socketTimeout = 30000;
        RetryPolicy _policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(_policy);
        queue.add(req);

        return warehousesArray;

    }

    public ArrayList<Client> getClientsArray() {

        ArrayList<Client> clientsArray = new ArrayList<>();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final String[] documet = {""};
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = "http://" + MainActivity.ipConnection + ":" + MainActivity.portConnection + "/PobierzKontrahentow/";
        JSONArray array = new JSONArray();


        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject client = (JSONObject) response
                                .get(i);
                        int id = client.getInt("KntId");
                        String name = client.getString("Knt_Kod");
                        clientsArray.add(new Client(id, name));
                        Log.i("Dodano Kontrahenta", id + " " + name);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                Log.i("Ładowanie kontrahentów", "Ładwanie kontrahentów przebiegło pomyślnie");
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.


            }
        });

        int socketTimeout = 30000;
        RetryPolicy _policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(_policy);
        queue.add(req);

        return clientsArray;


    }

    public void showGenerateDocumentDialog(Activity activity) {
        ArrayList<Warehouse> warehouseArrayList = getWarehousesArray();
        ArrayList<Client> clientsArrayList = getClientsArray();
        final int[] warehouseId = {-1};
        final String[] clientName = {""};
        generateDocumentDialog = new Dialog(activity);
        generateDocumentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        generateDocumentDialog.setCancelable(true);
        generateDocumentDialog.setContentView(R.layout.generate_document_dialog);
        generateDocumentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        adapterWarehouseSpinner = new AdapterWarehouseSpinner(getApplicationContext(), warehouseArrayList);
        warehouseSpinner = generateDocumentDialog.findViewById(R.id.warehouse_spinner);
        //warehouseSpinner.setThreshold(1);
        warehouseSpinner.setAdapter(adapterWarehouseSpinner);
        Log.i("Dodano magazyn", warehouseArrayList.size() + "");
        adapterWarehouseSpinner.notifyDataSetChanged();
        warehouseSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //warehouseSpinner.setText(warehouseArrayList.get(parent.getItemAtPosition(position)).name);
                Warehouse war = (Warehouse) parent.getItemAtPosition(position);
                warehouseSpinner.setText(war.name);
                warehouseSpinner.setSelection(war.name.length());
                warehouseId[0] = war.id;

            }
        });

        clientSpinner = generateDocumentDialog.findViewById(R.id.client_spinner);
        adapterClientSpinner = new AdapterClientSpinner(getApplicationContext(), clientsArrayList);
        clientSpinner.setAdapter(adapterClientSpinner);
        clientSpinner.setThreshold(1);
        adapterClientSpinner.notifyDataSetChanged();
        clientSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Client war = (Client) parent.getItemAtPosition(position);
                clientSpinner.setText(war.getName());
                clientName[0] = war.getName();
                clientSpinner.setSelection(war.getName().length());
            }
        });
        clientSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterClientSpinner.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapterClientSpinner.notifyDataSetChanged();
            }
        });

        generateDocumentButton = generateDocumentDialog.findViewById(R.id.generate_button);
        generateDocumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (warehouseId[0] == -1)
                    showDialog("Wybierz magazyn");
                else if (clientName[0] == "")
                    showDialog("Wybierz kontrahenta");
                else {
                    generateDocumentDialog.dismiss();
                    showLoadingDialog("Tworzenie dokumentu....\n     ");
                    generateDocument(warehouseId[0], clientName[0]);
                }
            }
        });


        generateDocumentDialog.show();
    }

    class AddCargo extends AsyncTask<Void, Void, Void> {
        String _ean;
        int error;

        public AddCargo(String _ean) {
            this._ean = _ean;
            error = 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            URL githubEndpoint = null;
            int id = 0;
            String name = "";
            String units = "";
            String ean = _ean;
            Log.i("Zeskanowane", ean + "");
            try {
                githubEndpoint = new URL("http://" + MainActivity.ipConnection + ":" + MainActivity.portConnection + "/PobierzEan/" + _ean);
                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) githubEndpoint.openConnection();
                myConnection.setConnectTimeout(1000);

                if (myConnection.getResponseCode() == 200) {
                    // Success
                    // Further processing here
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");

                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.setLenient(true);
                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String key = jsonReader.nextName();
                        {
                            if (key.equals("id"))
                                id = jsonReader.nextInt();
                            else if (key.equals("nazwa"))
                                name = jsonReader.nextString();
                            else if (key.equals("jednostka"))
                                units = jsonReader.nextString();
                            else
                                jsonReader.skipValue();
                        }

                    }
                    cargoList.add(new Cargo(id, 1, name, units, ean));
                    Log.i("Dodany towar", id + " " + name + " " + units + " " + ean);
                    myConnection.disconnect();

                } else
                    error = 1;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                error = 2;
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            if (error == 2)
                showDialog("Brak połączenia z serwerem");
            else if (error == 1)
                showDialog("Towaru nie ma w bazie badź zeskanowana pozycja jest usługą");
        }
    }

}



