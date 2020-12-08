package com.mianasad.clienttutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anupkumarpanwar.scratchview.ScratchView;
import com.mianasad.clienttutorial.databinding.ActivityScratchBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ScratchActivity extends AppCompatActivity {

    ActivityScratchBinding binding;
    SharedPreferences preferences;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScratchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Coins...");
        dialog.setCancelable(false);

        preferences = getSharedPreferences("User", MODE_PRIVATE);

        Random random = new Random();
        int generatedCoins = random.nextInt(100);

        binding.coins.setText(String.format("%d %s", generatedCoins, "Coins"));

        binding.scratchView.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {
                updateCoins(generatedCoins);
            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                if(percent >= 30) {
                    scratchView.reveal();
                }
            }
        });
    }

    void updateCoins(int coins) {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(ScratchActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, "http://mianasad.com/client/updatecoins.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("error")) {
                        Toast.makeText(ScratchActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ScratchActivity.this, "Coins updated.", Toast.LENGTH_SHORT).show();
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