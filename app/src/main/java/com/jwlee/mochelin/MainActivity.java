package com.jwlee.mochelin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.jwlee.mochelin.adapter.PostingAdapter;
import com.jwlee.mochelin.api.NetworkClient;
import com.jwlee.mochelin.api.PostingApi;
import com.jwlee.mochelin.config.Config;
import com.jwlee.mochelin.model.Posting;
import com.jwlee.mochelin.model.PostingList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    Button btnAdd;
    ProgressBar progressBar;

    RecyclerView recyclerView;
    PostingAdapter adapter;

    ArrayList<Posting> postingArrayList = new ArrayList<>();

    String token;

    // 페이징 처리에 필요한 변수
    int offset = 0;
    int limit = 10;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 회원가입이나 로그인이 되어있는 유저인지 체크해야 한다.
        // 억세스토큰이 있는지를 확인하는 코드로 작성.
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        if(token.isEmpty()){
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);

            finish();
            return;
        }

        btnAdd = findViewById(R.id.btnAdd);
        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition+1 == totalCount){
                    //아이템 추가 ! 입맛에 맞게 설정하시면됩니다.

                    // 데이터를 추가로 불러온다.
                    if(count == limit){
                        addNetworkData();
                    }

                }

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        offset = 0;    // 추가한 부분
        getNetworkData();
    }

    private void addNetworkData() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);

        PostingApi api = retrofit.create(PostingApi.class);

        Call<PostingList> call = api.getFollowPost(offset, limit,  "Bearer " + token );

        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){

                    PostingList postingList = response.body();

                    // 페이징 위한 변수 처리
                    count = postingList.count;
                    offset = offset + count;

                    postingArrayList.addAll( postingList.items );

                    adapter.notifyDataSetChanged();

                }else {

                }
            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void getNetworkData() {

        progressBar.setVisibility(View.VISIBLE);

        postingArrayList.clear();

        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);

        PostingApi api = retrofit.create(PostingApi.class);

        Call<PostingList> call = api.getFollowPost(offset, limit,  "Bearer " + token );

        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){

                    PostingList postingList = response.body();

                    // 페이징을 위한 변수처리
                    count = postingList.count;
                    offset = offset + count;

                    postingArrayList.addAll( postingList.items );

                    adapter = new PostingAdapter(MainActivity.this, postingArrayList);

                    recyclerView.setAdapter(adapter);

                }else{

                }

            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}