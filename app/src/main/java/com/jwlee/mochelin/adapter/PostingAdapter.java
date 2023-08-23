package com.jwlee.mochelin.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jwlee.mochelin.R;
import com.jwlee.mochelin.api.NetworkClient;
import com.jwlee.mochelin.api.PostingApi;
import com.jwlee.mochelin.config.Config;
import com.jwlee.mochelin.model.Posting;
import com.jwlee.mochelin.model.ResultRes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostingAdapter extends RecyclerView.Adapter<PostingAdapter.ViewHolder>{

    Context context;
    ArrayList<Posting> postingArrayList;

    SimpleDateFormat sf;

    SimpleDateFormat df;

    public PostingAdapter(Context context, ArrayList<Posting> postingArrayList) {
        this.context = context;
        this.postingArrayList = postingArrayList;

        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  //원본 타임
        df = new SimpleDateFormat("yyyy년-MM월-dd일 HH;mm");  //데스트네이션 타임  한글 처리 년 월 일 추가
        sf.setTimeZone(TimeZone.getTimeZone("UTC"));  //원본 타임은 UTC 글로벌 표준시이다.
        df.setTimeZone(TimeZone.getDefault());  // 내 폰의 로컬타임으로 바꾸자.

    }


    @NonNull
    @Override
    public PostingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.posting_row, parent, false);
        return new PostingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posting posting = postingArrayList.get(position);



        //이미지 처리는 네트워크 Glide

        Glide.with(context)
                .load(posting.imgUrl)
                .into(holder.imgPhoto);

        holder.txtContent.setText(posting.content);  //public 이라서 getContent() 필요없음. private은 필요
        holder.txtEmail.setText(posting.email);

        // 여러분 폰의 로컬타임으로 변환해야 한다.

        try {
            Date date = sf.parse(posting.createdAt); // 문자열을 UTC 형식으로 변환
            String localTime = df.format(date);
            holder.txtCreatedAt.setText(localTime);
        } catch (ParseException e) {
            Log.i("PostingApp", e.toString());
        }

//        holder.txtCreatedAt.setText(posting.createdAt);  원래 문장 삭제함!!

        //좋아요 처리. (0,1 )

        if (posting.isLike == 1) {
            holder.imgLike.setImageResource(R.drawable.ic_thumb_up_2);
        } else {
            holder.imgLike.setImageResource(R.drawable.ic_thumb_up_1);
        }

    }

    @Override
    public int getItemCount() {
        return postingArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView txtContent;
        TextView txtEmail;
        TextView txtCreatedAt;
        ImageView imgLike;   //Thumb up 이미지
        ImageView imgPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            txtContent = itemView.findViewById(R.id.txtContent);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            imgLike = itemView.findViewById(R.id.imgLike);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);


            imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    Posting posting = postingArrayList.get(index);

                    Retrofit retrofit = NetworkClient.getRetrofitClient(context);    // if문 안에 있는 코드를 밖으로 빼내어서 사용하자 4줄
                    PostingApi api = retrofit.create(PostingApi.class);
                    SharedPreferences sp = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
                    String token = sp.getString(Config.ACCESS_TOKEN, "");

                    if(posting.isLike == 1) {
                        //1 이면 취소

                        Call<ResultRes> call = api.deletePostLike(posting.id,"Bearer "+ token);
                        call.enqueue(new Callback<ResultRes>() {
                            @Override
                            public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                                if(response.isSuccessful()) {
                                    posting.isLike = 0;
                                    notifyDataSetChanged();

                                } else {

                                }
                            }

                            @Override
                            public void onFailure(Call<ResultRes> call, Throwable t) {

                            }
                        });


                    }else {

                        Call<ResultRes> call = api.setPostLike(posting.id, "Bearer " + token);
                        call.enqueue(new Callback<ResultRes>() {
                            @Override
                            public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                                if (response.isSuccessful()) {
                                    posting.isLike = 1;
                                    notifyDataSetChanged();

                                }else {

                                }
                            }

                            @Override
                            public void onFailure(Call<ResultRes> call, Throwable t) {

                            }
                        });

                    }
                }
            });




        }
    }
}
