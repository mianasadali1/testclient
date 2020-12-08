package com.mianasad.clienttutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.mianasad.clienttutorial.SpinWheel.LuckyWheelView;
import com.mianasad.clienttutorial.SpinWheel.model.LuckyItem;
import com.mianasad.clienttutorial.databinding.ActivitySpinWheelBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SpinWheelActivity extends AppCompatActivity {

    ActivitySpinWheelBinding binding;
    ProgressDialog dialog;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinWheelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Coins...");
        dialog.setCancelable(false);

        preferences = getSharedPreferences("User", MODE_PRIVATE);

        getSupportActionBar().setTitle("Spin Wheel");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<LuckyItem> data = new ArrayList<>();

        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.topText = "5";
        luckyItem1.secondaryText = "COINS";
        luckyItem1.textColor = Color.parseColor("#212121");
        luckyItem1.color = Color.parseColor("#eceff1");
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.topText = "10";
        luckyItem2.secondaryText = "COINS";
        luckyItem2.color = Color.parseColor("#00cf00");
        luckyItem2.textColor = Color.parseColor("#ffffff");
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.topText = "15";
        luckyItem3.secondaryText = "COINS";
        luckyItem3.textColor = Color.parseColor("#212121");
        luckyItem3.color = Color.parseColor("#eceff1");
        data.add(luckyItem3);

        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.topText = "20";
        luckyItem4.secondaryText = "COINS";
        luckyItem4.color = Color.parseColor("#7f00d9");
        luckyItem4.textColor = Color.parseColor("#ffffff");
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.topText = "25";
        luckyItem5.secondaryText = "COINS";
        luckyItem5.textColor = Color.parseColor("#212121");
        luckyItem5.color = Color.parseColor("#eceff1");
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.topText = "30";
        luckyItem6.secondaryText = "COINS";
        luckyItem6.color = Color.parseColor("#dc0000");
        luckyItem6.textColor = Color.parseColor("#ffffff");
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.topText = "35";
        luckyItem7.secondaryText = "COINS";
        luckyItem7.textColor = Color.parseColor("#212121");
        luckyItem7.color = Color.parseColor("#eceff1");
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.topText = "0";
        luckyItem8.secondaryText = "COINS";
        luckyItem8.color = Color.parseColor("#008bff");
        luckyItem8.textColor = Color.parseColor("#ffffff");
        data.add(luckyItem8);


        binding.wheelview.setData(data);
        binding.wheelview.setRound(5);


        binding.spinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int randomNumber = r.nextInt(8);

                binding.wheelview.startLuckyWheelWithTargetIndex(randomNumber);
            }
        });

        binding.wheelview.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                int coins = 0;

                switch (index) {
                    case 0:
                        coins = 5;
                        break;
                    case 1:
                        coins = 10;
                        break;
                    case 2:
                        coins = 15;
                        break;
                    case 3:
                        coins = 20;
                        break;
                    case 4:
                        coins = 25;
                        break;
                    case 5:
                        coins = 30;
                        break;
                    case 6:
                        coins = 35;
                        break;
                    case 7:
                        coins = 0;
                        break;
                }

                updateCoins(coins);
            }
        });
    }

    void updateCoins(int coins) {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(SpinWheelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, "http://mianasad.com/client/updatecoins.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("error")) {
                        Toast.makeText(SpinWheelActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SpinWheelActivity.this, "Coins updated.", Toast.LENGTH_SHORT).show();
                        finish();
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}