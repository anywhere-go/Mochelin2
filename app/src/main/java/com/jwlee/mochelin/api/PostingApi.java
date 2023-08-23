package com.jwlee.mochelin.api;

import com.jwlee.mochelin.model.PostingList;
import com.jwlee.mochelin.model.ResultRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostingApi {// 친구들의 포스트 가져오는 API
    @GET("/post/follow")
    Call<PostingList> getFollowPost(@Query("offset") int offset,
                                    @Query("limit") int limit,
                                    @Header("Authorization") String token);

    // 좋아요 API
    @POST("/post/{postId}/like")
    Call<ResultRes> setPostLike(@Path("postId") int postId,
                                @Header("Authorization") String token);
    // 좋아요 취소 API
    @DELETE("/post/{postId}/like")
    Call<ResultRes> deletePostLike(@Path("postId") int postId,
                                   @Header("Authorization") String token);

    // 포스트 생성 API

    @Multipart
    @POST("/post")
    Call<ResultRes> addPosting(@Header("Authorization") String token,
                               @Part MultipartBody.Part photo,
                               @Part("content") RequestBody content);


}
