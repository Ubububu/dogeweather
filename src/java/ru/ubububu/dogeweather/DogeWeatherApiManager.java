package ru.ubububu.dogeweather;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by andrey on 14.10.15.
 */
public class DogeWeatherApiManager {
    private static final String API_URL = "http://api.openweathermap.org";
    /*private static final OkHttpClient client = new OkHttpClient();
    static {
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request(); //Current Request
                Response response = chain.proceed(originalRequest); //Get response of the request
                if (BuildConfig.DEBUG) {
                    //I am logging the response body in debug mode. When I do this I consume the response (OKHttp only lets you do this once) so i have re-build a new one using the cached body
                    String bodyString = response.body().string();
                    Log.v("dogeapi", String.format("Sending request %s with headers %s ", originalRequest.url(), originalRequest.headers()));
                    Log.v("dogeapi", String.format("Got response HTTP %s %s \n\n with body %s \n\n with headers %s ", response.code(), response.message(), bodyString, response.headers()));
                    response = response.newBuilder().body(ResponseBody.create(response.body().contentType(), bodyString)).build();
                }

                return response;
            }
        });
    }*/
    private static final Retrofit REST_ADAPTER = new Retrofit.Builder()
            .baseUrl(API_URL)
            //.client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static final DogeWeatherService DOGE_SERVICE = REST_ADAPTER.create(DogeWeatherService.class);

    public static DogeWeatherService getService() {
        return DOGE_SERVICE;
    }

    public interface DogeWeatherService {
        @GET("/data/2.5/find?mode=json")
        Call<ListWeather> findByName(@Query("q") String name,
                                  @Query("APPID") String appId);
        @GET("/data/2.5/weather?units=metric&mode=json")
        Call<DefaultWeather> weatherById(@Query("id") int id,
                                   @Query("APPID") String appId);
        @GET("/data/2.5/weather?units=metric&mode=json")
        Call<DefaultWeather> weatherByLatLon(@Query("lat") double lat,
                                       @Query("lon") double lon,
                                       @Query("APPID") String appId);
    }
}
