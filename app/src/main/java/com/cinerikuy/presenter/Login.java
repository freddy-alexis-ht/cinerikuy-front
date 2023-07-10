package com.cinerikuy.presenter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cinerikuy.R;
import com.cinerikuy.remote.customer.ICustomer;
import com.cinerikuy.remote.customer.exceptions.ApiExceptionResponse;
import com.cinerikuy.remote.customer.model.CustomerLoginRequest;
import com.cinerikuy.remote.customer.model.CustomerResponse;
import com.cinerikuy.utilty.Constans;
import com.cinerikuy.utilty.Utils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {
    private TextView createAccount;
    private Button btnLogin;
    private EditText usernameText, passwordText;
    private TextInputLayout layoutPassword;
    private ICustomer customerService;
    private ProgressDialog progressDialog;
    boolean isLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createAccount = findViewById(R.id.enlaceCreate);
        btnLogin = findViewById(R.id.btnLogin);
        usernameText = findViewById(R.id.user);
        passwordText = findViewById(R.id.password);
        layoutPassword = findViewById(R.id.layPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                passwordText.addTextChangedListener(textWatcher);
                if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                    usernameText.setError("Campo obligatorio");
                    passwordText.setError("Campo obligatorio");
                    layoutPassword.setEndIconVisible(false);
                } else {
                    CustomerLoginRequest loginRequest = CustomerLoginRequest.builder()
                            .username(username)
                            .password(password)
                            .build();
                    login(loginRequest);
                }
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }
    public void login(CustomerLoginRequest request) {
        Utils.logRequest(request);
        showProgressDialog("Iniciando Sesion...");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_CUSTOMER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        customerService = retrofit.create(ICustomer.class);
        Call<CustomerResponse> call = customerService.login(request);

        call.enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                if(!response.isSuccessful()) {
                    Gson gson = new Gson();
                    try {
                        assert response.errorBody() != null;
                        ApiExceptionResponse errorResponse = gson.fromJson(response.errorBody().string(), ApiExceptionResponse.class);
                        if (errorResponse != null) {
                            String detail = errorResponse.getDetail();
                            Utils.logResponse(errorResponse);
                            delayAndStartNavigationActivity(detail, isLogin);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;

                } else {
                    CustomerResponse rs = response.body();
                    assert rs != null;
                    isLogin = true;
                    Utils.logResponse(rs);
                    delayAndStartNavigationActivity(rs.getUsername(), isLogin);
                }
            }
            @Override
            public void onFailure(Call<CustomerResponse> call, Throwable t) {
                Log.e("Throw Error:", t.getMessage());
                progressDialog.dismiss();
                Toast.makeText(Login.this, "Presenta problemas de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveSharedPreferences(String user) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", user);
        editor.apply();
    }
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean isNotEmpty = !TextUtils.isEmpty(charSequence);
            layoutPassword.setEndIconVisible(isNotEmpty);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void delayAndStartNavigationActivity(String userName, boolean isLogin) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                if (isLogin) {
                    Intent intent = new Intent(Login.this, NavigationActivity.class);
                    intent.putExtra("username",userName);
                    saveSharedPreferences(userName);
                    startActivity(intent);
                    Toast.makeText(Login.this, "Bienvenido " + userName, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Login.this, userName, Toast.LENGTH_SHORT).show();
                }

            }
        },3000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}