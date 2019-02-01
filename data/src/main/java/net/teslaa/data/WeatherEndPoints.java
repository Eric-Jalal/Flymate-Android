package net.teslaa.data;

import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Amir on Feb/2019 - 11.25.
 */


public interface WeatherEndPoints {

  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);

}
