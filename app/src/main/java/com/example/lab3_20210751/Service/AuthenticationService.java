package com.example.lab3_20210751.Service;

import com.example.lab3_20210751.Beans.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthenticationService {
    @FormUrlEncoded
    @POST("/auth/login")
    Call<User> autenticarUsuario(@Field("username") String username, @Field("password") String password);

}
