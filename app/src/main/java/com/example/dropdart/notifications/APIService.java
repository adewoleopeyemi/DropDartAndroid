package com.example.dropdart.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {"Content-Type:application/json",
                    "Authorization:key=AAAAqyxLcr4:APA91bFfk4RvR58-DGiUgRI0kBFFQQ0gIrGH3qGLAE9xnEhhZJd0_XI4ByaLoO5BA7SNRUzhazXTZ1SVXP1FOVD9sHN5WCN9ff2T6gYk1fj9HDcii0DSZVmA2IUYKdtS5VvrvoFGHUZa"

                    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
