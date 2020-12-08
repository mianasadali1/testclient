package com.mianasad.clienttutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mianasad.clienttutorial.databinding.ActivityLeaderboardsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LeaderboardsActivity extends AppCompatActivity {

    ActivityLeaderboardsBinding binding;
    ArrayList<User> users;
    UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaderboardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        users = new ArrayList<>();
        adapter = new UsersAdapter(this, users);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.usersList.setLayoutManager(layoutManager);
        binding.usersList.setAdapter(adapter);

        loadLeaderboard();
    }

    void loadLeaderboard() {
        RequestQueue queue = Volley.newRequestQueue(LeaderboardsActivity.this);

        StringRequest request = new StringRequest(Request.Method.GET, "http://mianasad.com/client/leaderboard.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for(int i =0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String name = obj.getString("name");
                        String email = obj.getString("email");
                        int coins = obj.getInt("coins");

                        User user = new User(name, email, coins);
                        users.add(user);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }
}