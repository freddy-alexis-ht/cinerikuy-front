package com.cinerikuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cinerikuy.remote.customer.ICustomer;
import com.cinerikuy.remote.customer.exceptions.ApiExceptionResponse;
import com.cinerikuy.remote.customer.model.CustomerResponse;
import com.cinerikuy.remote.customer.model.CustomerSignInRequest;
import com.cinerikuy.utilty.Constans;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {
    private EditText usernameText, passwordText, firstNameText, lastNameText, dniText,cellphoneText;
    private Button btnSignIn;
    private TextInputLayout layoutPassword;
    private ICustomer customerService;
    private boolean isValid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameText = findViewById(R.id.user);
        passwordText = findViewById(R.id.password);
        firstNameText = findViewById(R.id.name);
        lastNameText = findViewById(R.id.lastName);
        dniText = findViewById(R.id.dni);
        cellphoneText = findViewById(R.id.phone);
        layoutPassword = findViewById(R.id.layoutPassword);
        btnSignIn = findViewById(R.id.btnRegister);



        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValid = true;
                String username = getTextAndValidate(usernameText, Constans.MESSAGE_OBLIGATORIO, Constans.MESSAGE_USERNAME_INVALID, Constans.REGEX_USERNAME, 2);
                String password = getTextAndValidate(passwordText, Constans.MESSAGE_OBLIGATORIO, Constans.MESSAGE_PASSWORD_INVALID, Constans.REGEX_PASSWORD, 4);
                String firstName = getTextAndValidate(firstNameText, Constans.MESSAGE_OBLIGATORIO, Constans.MESSAGE_NAMES_INVALID, Constans.REGEX_NAMES);
                String lastName = getTextAndValidate(lastNameText, Constans.MESSAGE_OBLIGATORIO, Constans.MESSAGE_NAMES_INVALID, Constans.REGEX_NAMES);
                String dni = getTextAndValidate(dniText, Constans.MESSAGE_OBLIGATORIO, Constans.MESSAGE_DNI_INVALID, Constans.REGEX_DNI,8);
                String cellPhone = getTextAndValidate(cellphoneText, Constans.MESSAGE_OBLIGATORIO, Constans.MESSAGE_CELLPHONE_INVALID, Constans.REGEX_CELLPHONE, 9);
                passwordText.addTextChangedListener(textWatcher);

                if (!isValid) {
                    Toast.makeText(Register.this, Constans.MESSAGE_ALL_OBLIGATORIO, Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(Register.this, "Llamando al metodo guardar", Toast.LENGTH_SHORT).show();
                    CustomerSignInRequest customerSignIn = CustomerSignInRequest.builder()
                                                            .username(username)
                                                            .password(password)
                                                            .firstName(firstName)
                                                            .lastName(lastName)
                                                            .dni(dni)
                                                            .cellphone(cellPhone)
                                                            .build();
                    sigInUser(customerSignIn);
                }

            }

        });
    }

    private void sigInUser(CustomerSignInRequest request) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_CUSTOMER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        customerService = retrofit.create(ICustomer.class);
        Call<CustomerResponse> call = customerService.sigInd(request);
        call.enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                if(!response.isSuccessful()) {
                    Gson gson = new Gson();
                    try {
                        ApiExceptionResponse errorResponse = gson.fromJson(response.errorBody().string(), ApiExceptionResponse.class);
                        if (errorResponse != null) {
                            String detail = errorResponse.getDetail();
                            Toast.makeText(Register.this, detail, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                CustomerResponse rs = response.body();
                assert rs != null;
                Toast.makeText(Register.this, rs.getUsername(), Toast.LENGTH_SHORT).show();
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

    private String getTextAndValidate(EditText editText, String mandatoryMessage, String invalidMessage,
                                            String regex, int minLength) {
        String text = editText.getText().toString();
        if (StringUtils.isBlank(text)) {
            editText.setError(mandatoryMessage);
            isValid = false;
            if (editText.getId() == R.id.password)
                layoutPassword.setEndIconVisible(false);
        } else if (text.length() < minLength || !text.matches(regex)) {
            editText.setError(invalidMessage);
            isValid = false;
            if (editText.getId() == R.id.password)
                layoutPassword.setEndIconVisible(false);
        }
        return text;

    }

    private String getTextAndValidate(EditText editText, String mandatoryMessage, String invalidMessage,
                                      String regex) {
        return getTextAndValidate(editText, mandatoryMessage, invalidMessage,regex,0);
    }

}