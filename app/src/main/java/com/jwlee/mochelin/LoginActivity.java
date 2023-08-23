package com.jwlee.mochelin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.jwlee.mochelin.api.NetworkClient;
import com.jwlee.mochelin.api.UserApi;
import com.jwlee.mochelin.config.Config;
import com.jwlee.mochelin.model.User;
import com.jwlee.mochelin.model.UserRes;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    Button btnLogin;
    TextView txtRegister;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword1);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString().trim();

                Pattern pattern = Patterns.EMAIL_ADDRESS; // 여러 패턴 중에 하나의 패턴 꺼집어낸다.
                if (pattern.matcher(email).matches() == false) {
                    Snackbar.make(btnLogin,
                            "이메일 형식을 확인하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String password = editPassword.getText().toString().trim(); // 비번 데이터 가져오기
                if (password.length() < 6 || password.length() > 12) {
                    Snackbar.make(btnLogin,
                            "비밀번호 길이를 확인하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

//                showProgress();

                Retrofit retrofit = NetworkClient.getRetrofitClient(LoginActivity.this);
                UserApi api = retrofit.create(UserApi.class);
                User user = new User(email, password);
                Call<UserRes> call = api.login(user);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
//                        dismissProgress();
                        if (response.isSuccessful()) {
//                            UserRes res = response.body();
//                            SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sp.edit();
////                            editor.putString(Config.ACCESS_TOKEN, res.access_token);
//                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                            finish();


                        } else if (response.code() == 400){

                            Snackbar.make(btnLogin,
                                    "이미 회원가입 되어있거나, 비밀번호가 틀렸습니다.",
                                    Snackbar.LENGTH_SHORT).show();
                            return;

                        } else {

                        }

                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {

//                        dismissProgress();
                    }
                });

            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

//    Dialog dialog;
//
//    void showProgress(){
//        dialog = new Dialog(this);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setContentView(new ProgressBar(this));
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//    }
//
//    void dismissProgress(){
//        dialog.dismiss();
//    }

}