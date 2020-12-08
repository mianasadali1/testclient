package com.mianasad.clienttutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mianasad.clienttutorial.databinding.ActivityWalletBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WalletActivity extends AppCompatActivity {

    ActivityWalletBinding binding;
    SharedPreferences preferences;
    ProgressDialog dialog;
    int coins = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Getting Coins...");
        dialog.setCancelable(false);

        preferences = getSharedPreferences("User", MODE_PRIVATE);

        getUserCoins();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Wallet");

        binding.withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdraw(coins);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getUserCoins()
    {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(WalletActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, "http://mianasad.com/client/getuser.php"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    coins = object.getJSONObject("data").getInt("coins");
                    binding.coins.setText(String.valueOf(coins));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("id", preferences.getString("userid", ""));

                return data;
            }
        };

        queue.add(request);
    }

    void withdraw(int coins) {
        dialog.setMessage("Sending Request...");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(WalletActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, "http://mianasad.com/client/withdraw.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("error")) {
                        Toast.makeText(WalletActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WalletActivity.this, "Withdraw sent.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("id", preferences.getString("userid", ""));
                data.put("coins", String.valueOf(coins));

                return data;
            }
        };

        queue.add(request);
    }
}