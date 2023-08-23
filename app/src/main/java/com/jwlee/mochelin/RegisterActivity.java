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

public class RegisterActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editNickName;
    EditText editName;
    EditText editPassword1;

    EditText editPassword2;
    Button btnRegister;
    TextView txtLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail = findViewById(R.id.editEmail);
        editNickName = findViewById(R.id.editNickName);
        editName = findViewById(R.id.editName);
        editPassword1 = findViewById(R.id.editPassword);
        editPassword2 = findViewById(R.id.editPassword2);

        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString().trim();
                String nickname = editNickName.getText().toString().trim();
                String name = editName.getText().toString().trim();



                Pattern pattern = Patterns.EMAIL_ADDRESS; // 여러 패턴 중에 하나의 패턴 꺼집어낸다.
                if (pattern.matcher(email).matches() == false) {
                    Snackbar.make(btnRegister,
                            "이메일 형식을 확인하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String password1 = editPassword1.getText().toString().trim();
                String password2 = editPassword2.getText().toString().trim();

                if(email.isEmpty() || nickname.isEmpty() || name.isEmpty() || password1.isEmpty() || password2.isEmpty()){
                    Snackbar.make(btnRegister,
                            "필수항목을 모두 입력하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if( email.contains("@") == false ){
                    Snackbar.make(btnRegister,
                            "이메일 형식을 확인하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (password1.length() < 4 || password1.length() > 12) {
                    Snackbar.make(btnRegister,
                            "비밀번호 길이를 확인하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(  !password1.equals(password2)  ){
                    Snackbar.make(btnRegister,
                            "비밀번호가 서로 다릅니다.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
//                showProgress();  // 프로그래스바 실행

                Retrofit retrofit = NetworkClient.getRetrofitClient(RegisterActivity.this);
                UserApi api = retrofit.create(UserApi.class);
                User user = new User(email, password1);
                Call<UserRes> call = api.register(user);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();
                        if (response.isSuccessful()) {
                            SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME,MODE_PRIVATE);
                            SharedPreferences.Editor editor =sp.edit();

                            UserRes res = response.body();
                            editor.putString(Config.ACCESS_TOKEN, res.access_token);  // public 설정해서 res.access_token 사용가능
                            editor.apply();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();


                        }else if(response.code() == 400) {
                            Snackbar.make(btnRegister,
                                    "이미 회원가입 되어 있습니다. 로그인하세요.",
                                    Snackbar.LENGTH_SHORT).show();
                            return;

                        }else if(response.code() == 401) {

                        }else if(response.code()== 404) {

                        }else if (response.code() == 500) {

                        }else {

                        }

                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        dismissProgress();

                    }
                });


            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }

    Dialog dialog;

    void showProgress(){
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    void dismissProgress(){
        dialog.dismiss();
    }
}