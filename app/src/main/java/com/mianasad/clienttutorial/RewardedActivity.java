package com.mianasad.clienttutorial;

import androidx.annotation.NonNull;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.mianasad.clienttutorial.databinding.ActivityRewardedBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RewardedActivity extends AppCompatActivity {

    ActivityRewardedBinding binding;

    private RewardedAd rewardedAd;
    SharedPreferences preferences;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRewardedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Coins...");
        dialog.setCancelable(false);

        preferences = getSharedPreferences("User", MODE_PRIVATE);

        rewardedAd = new RewardedAd(this, "ca-app-pub-3940256099942544/5224354917");

        AdRequest adRequest = new AdRequest.Builder().build();
        rewardedAd.loadAd(adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                super.onRewardedAdFailedToLoad(loadAdError);
            }
        });

        binding.watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rewardedAd.isLoaded()) {
                    rewardedAd.show(RewardedActivity.this, new RewardedAdCallback() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
//                            int coins = 35;
                            updateCoins(rewardItem.getAmount());
                        }
                    });
                }
            }
        });

    }

    void updateCoins(int coins) {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(RewardedActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, "http://mianasad.com/client/updatecoins.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("error")) {
                        Toast.makeText(RewardedActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RewardedActivity.this, "Coins updated.", Toast.LENGTH_SHORT).show();
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