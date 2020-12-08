package com.mianasad.clienttutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mianasad.clienttutorial.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    ProgressDialog dialog;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("User", MODE_PRIVATE);

        if(preferences.getBoolean("loggedIn", false)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } 

        editor = preferences.edit();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging into your account...");

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = binding.emailBox.getText().toString();
                String pass = binding.passBox.getText().toString();


                if(email.isEmpty()) {
                    binding.emailBox.setError("Email address cannot be empty.");
                    return;
                }

                if(pass.isEmpty()) {
                    binding.passBox.setError("Password cannot be empty.");
                    return;
                }

                dialog.show();

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                StringRequest request = new StringRequest(Request.Method.POST,
                        "http://mianasad.com/client/login.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        try {
                            JSONObject object = new JSONObject(response);

                            if(object.getBoolean("error")) {
                                Toast.makeText(LoginActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject data = object.getJSONObject("data");
                                editor.putString("userid", String.valueOf(data.getInt("id")));
                                editor.putBoolean("loggedIn", true);
                                editor.commit();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("pass", pass);
                        data.put("email", email);
                        return data;
                    }
                };

                queue.add(request);
            }
        });

    }
}