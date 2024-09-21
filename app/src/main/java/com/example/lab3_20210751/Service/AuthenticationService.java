package com.example.lab3_20210751.Service;

import com.example.lab3_20210751.Beans.ToDo;
import com.example.lab3_20210751.Beans.TodosResponse;
import com.example.lab3_20210751.Beans.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthenticationService {
    @FormUrlEncoded
    @POST("/auth/login")
    Call<User> autenticarUsuario(@Field("username") String username, @Field("password") String password);



    @GET("/todos/user/{userId}")
    Call<TodosResponse> getTodos(@Path("userId") int userId);
}
