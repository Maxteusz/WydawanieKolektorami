package com.example.WydawanieKolektorami;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    private Button startButton;
    private TextInputEditText ip, port;
    private MaterialButton acceptedButton;
    public static String ipConnection = "192.168.1.198", portConnection = "5005";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CargoListActivity.class);
                startActivity(intent);
                //new IntentIntegrator(MainActivity.this).setCaptureActivity(ScanActivity.class).setPrompt("Zeskanuj Produkt").setBarcodeImageEnabled(true).setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES).initiateScan();

            }
        });


    }
    public void showDialog (View V)
    {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ip = dialog.findViewById(R.id.editText_ip);
        port = dialog.findViewById(R.id.editText_port);
        if(MainActivity.ipConnection != "")
            ip.setText(MainActivity.ipConnection + "");
        if(MainActivity.portConnection != "")
            port.setText(MainActivity.portConnection + "");

        acceptedButton = dialog.findViewById(R.id.button_accepted);
        acceptedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ip.getText().equals("") && !port.getText().equals("")) {
                    MainActivity.ipConnection = ip.getText().toString();
                    MainActivity.portConnection = port.getText().toString();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }




}