package com.mianasad.clienttutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.mianasad.clienttutorial.databinding.ActivityRegisterBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating new user...");

        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.nameBox.getText().toString();
                String email = binding.emailBox.getText().toString();
                String pass = binding.passBox.getText().toString();

                if(name.isEmpty()) {
                    binding.nameBox.setError("Name cannot be empty.");
                    return;
                }

                if(email.isEmpty()) {
                    binding.emailBox.setError("Email address cannot be empty.");
                    return;
                }

                if(pass.isEmpty()) {
                    binding.passBox.setError("Password cannot be empty.");
                    return;
                }

                dialog.show();


                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);

                StringRequest request = new StringRequest(Request.Method.POST,
                        "http://mianasad.com/client/register.php",
                        new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);

                            if(object.getBoolean("error")) {
                                Toast.makeText(RegisterActivity.this,
                                        object.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
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
                        data.put("name", name);
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