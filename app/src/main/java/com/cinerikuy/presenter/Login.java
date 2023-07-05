package com.cinerikuy.presenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cinerikuy.R;
import com.cinerikuy.remote.customer.ICustomer;
import com.cinerikuy.remote.customer.exceptions.ApiExceptionResponse;
import com.cinerikuy.remote.customer.model.CustomerLoginRequest;
import com.cinerikuy.remote.customer.model.CustomerResponse;
import com.cinerikuy.utilty.Constans;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.Executor;

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
    private BiometricPrompt.PromptInfo promptInfo;
    private ImageView btnFingerPrint;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private String userName;
    private SharedPreferences sharedPreferences;
    private boolean isLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createAccount = findViewById(R.id.enlaceCreate);
        btnLogin = findViewById(R.id.btnLogin);
        btnFingerPrint = findViewById(R.id.btn_fingerprint);
        usernameText = findViewById(R.id.user);
        passwordText = findViewById(R.id.password);
        layoutPassword = findViewById(R.id.layPassword);
        chechBiometric();
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

        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("isLogin", false);
        if (isLogin) {
           btnFingerPrint.setVisibility(View.VISIBLE);
           executedBiometric();
        } else {
            executedBiometricFirts();
        }

    }
    public void chechBiometric() {
        BiometricManager biometricManager = BiometricManager.from(this);
        String info = "";
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                info = "Dispositivo cuenta con autenticacion biometrica";
                enabledButton(true);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                info = "El dispositivo no cuenta con autenticación biometrica";
                enabledButton(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                info = "El dispositivo sensor se encuentra desabilitado";
                enabledButton(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                info = "Necesita registrar una huella dactilar";
                enabledButton(false);
                break;
            default:
                info = "Causa desconocida";
                enabledButton(false, true);
                break;
        }
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }
    private void enabledButton(boolean enable, boolean unroll) {
        enabledButton(enable);
        if (!unroll) return;
        Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK);
        startActivity(enrollIntent);
    }
    void enabledButton(boolean enable) {
        btnLogin.setEnabled(enable);
        btnFingerPrint.setEnabled(true);
    }
    public void executedBiometricFirts() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(Login.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(Login.this, errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                showProgressDialog("Registrando Huella Dactilar");
                delayAndStartNavigationActivity(userName);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Login.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }
        });
        btnFingerPrint.setOnClickListener(view -> {
            BiometricPrompt.PromptInfo.Builder prBuilder = dialogMetric("Verificando","Huella");
            prBuilder.setNegativeButtonText("Cancel");
            biometricPrompt.authenticate(prBuilder.build());
        });
    }

    public void executedBiometric() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(Login.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(Login.this, errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String user = sharedPreferences.getString("username","");
                String pass = sharedPreferences.getString("password","");
                CustomerLoginRequest customer = CustomerLoginRequest.builder()
                                        .username(user)
                                        .password(pass)
                                        .build();
                login(customer);

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Login.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }
        });
        btnFingerPrint.setOnClickListener(view -> {
            BiometricPrompt.PromptInfo.Builder prBuilder = dialogMetric("Iniciar sesión con huella dactilar","Coloca tu huella en el sensor para iniciar sesión");
            prBuilder.setNegativeButtonText("Cancel");
            biometricPrompt.authenticate(prBuilder.build());
        });
    }
    BiometricPrompt.PromptInfo.Builder dialogMetric(String title, String message) {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(message);
    }

    public void login(CustomerLoginRequest request) {
        showProgressDialog("Validando...");
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
                            Toast.makeText(Login.this, detail, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;

                } else {
                    CustomerResponse rs = response.body();
                    assert rs != null;
                    if(isLogin) {
                        delayAndStartNavigationActivity(rs.getUsername());
                    } else {
                        showRegisterBiometricFingerPrint(request);
                    }
                }
            }
            @Override
            public void onFailure(Call<CustomerResponse> call, Throwable t) {
                Log.e("Throw Error:", t.getMessage());
            }
        });
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

    private void delayAndStartNavigationActivity(String userName) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Intent intent = new Intent(Login.this, NavigationActivity.class);
                startActivity(intent);
                Toast.makeText(Login.this, "Bienvenido " + userName, Toast.LENGTH_SHORT).show();
                finish();
            }
        },3000);
    }

    private void showRegisterBiometricFingerPrint(CustomerLoginRequest rs) {
        userName = rs.getUsername();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                //Guardar en sharePreferences el username,password y boolean
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("username",rs.getUsername());
                editor.putString("password",rs.getPassword());
                editor.putBoolean("isLogin",true);
                editor.apply();

                BiometricPrompt.PromptInfo.Builder prBuilder = dialogMetric("Registro de huella dactilar","Coloca tu huella en el sensor para registrarla");
                prBuilder.setNegativeButtonText("Cancel");
                biometricPrompt.authenticate(prBuilder.build());
            }
        },3000);


    }
}