package net.teslaa.flymate.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.airbnb.lottie.LottieAnimationView;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import net.teslaa.flymate.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements WeatherSourceCallback,
    EasyPermissions.PermissionCallbacks {

  public static final String TAG = MainActivity.class.getSimpleName();
  private static final int READ_PHONE_STATE = 100;
  private final WeatherSource mWeatherSource = new OpenWeather(this);

  private CurrentWeatherIcons mForecast;
  @BindView(R.id.locationLabel)
  TextView mLocationLabel;
  @BindView(R.id.timeLabel)
  TextView mTimeLabel;
  @BindView(R.id.temperatureLabel)
  TextView mTemperatureLabel;
  @BindView(R.id.humidityValue)
  TextView mHumidityValue;
  @BindView(R.id.precipValue)
  TextView mPrecipValue;
  @BindView(R.id.summaryLabel)
  TextView mSummaryLabel;
  @BindView(R.id.iconImageView)
  ImageView mIconImageView;
  @BindView(R.id.refreshImageView)
  ImageView mRefreshImageView;
  private LottieAnimationView mProgressBar;
  private double myLatitude;
  private double myLongitude;
  private String mLocationName = "Default Locale";
  private LottieAnimationView lottieBackground;

  @SuppressLint("MissingPermission")
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    methodRequiredPermissions();
    setUpViewIds();
    setUpLottie();
    setUpProgressBar();
  }

  private void setUpProgressBar() {
    mProgressBar.setAnimation("worm.json");
    mProgressBar.playAnimation();
    mProgressBar.setSpeed(5.0f);
    mProgressBar.loop(true);
    mProgressBar.setVisibility(View.INVISIBLE);
  }

  private void setUpLottie() {
    lottieBackground.setAnimation("gradient_animated_background.json");
    lottieBackground.playAnimation();
    lottieBackground.setSpeed(0.5f);
    lottieBackground.loop(true);
  }

  private void setUpViewIds() {
    // TODO: There is a bug in butterknife when i binded these 2 views, this needs to be fixed and removed
    mProgressBar = findViewById(R.id.progressBar);
    lottieBackground = findViewById(R.id.background_lottie);
  }

  @Override
  protected void onResume() {
    super.onResume();
    //start location service
    SmartLocation
        .with(this)
        .location()
        .oneFix()
        .start(new OnLocationUpdatedListener() {
          @Override
          public void onLocationUpdated(Location location) {
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
            mLocationName = getLocationName(myLatitude, myLongitude);
            if (mTemperatureLabel.getText().toString()
                .equals(getString(R.string.temperature_loading))) {
              runOnUiThread(new Runnable() {
                public void run() {
                  refreshForecast(mRefreshImageView);
                }
              });
            }
          }
        });
  }

  @Override
  protected void onPause() {
    super.onPause();
    SmartLocation.with(this).location().stop();
  }

  public void toggleRefresh() {
    if (mProgressBar.getVisibility() == View.INVISIBLE) {
      mProgressBar.setVisibility(View.VISIBLE);
      mRefreshImageView.setVisibility(View.INVISIBLE);
    } else {
      mProgressBar.setVisibility(View.INVISIBLE);
      mRefreshImageView.setVisibility(View.VISIBLE);
    }
  }

  public void updateDisplay() {
    Current current = mForecast.getCurrent();
    mTemperatureLabel.setText(current.getTemperature() + "");
    mTimeLabel.setText("Sunrise At " + current.getFormatedTime());
    mHumidityValue.setText(current.getHumidity() + "%");
    mPrecipValue.setText(current.getPrecipChance() + "Km/h");
    mSummaryLabel.setText(current.getSummary());
    Drawable drawable = getResources().getDrawable((int) current.getIconId());
    mIconImageView.setImageDrawable(drawable);
  }

  public void alertUserAboutError() {
    AlertDialogFragment dialog = new AlertDialogFragment();
    dialog.show(getFragmentManager(), "error_dialog");
  }

  @OnClick(R.id.refreshImageView)
  public void refreshForecast(View v) {
    mLocationLabel.setText(mLocationName);

    if (isNetworkAvailable()) {
      toggleRefresh();
      mWeatherSource.getForecast(myLatitude, myLongitude);
    } else {
      Toast.makeText(this, getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onSuccess(CurrentWeatherIcons forecast) {
    mForecast = forecast;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        toggleRefresh();
        updateDisplay();
      }
    });
  }

  @Override
  public void onFailure(Exception e) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        toggleRefresh();
        alertUserAboutError();
      }
    });
  }

  /**
   * Get the name of the city at the given map coordinates.
   */
  public String getLocationName(double latitude, double longitude) {

    String cityName = "Not Found";
    if (Geocoder.isPresent()) {
      Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
      try {
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if (addresses.size() > 0) {
          Address address = addresses.get(0);
          cityName = address.getLocality(); // + ", " + address.getAdminArea();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      cityName = "Not Available";
    }
    return cityName;
  }

  public boolean isNetworkAvailable() {
    ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = null;
    if (manager != null) {
      Log.d("getActiveNetworkInfo()", "is not null");
      networkInfo = manager.getActiveNetworkInfo();
    } else {
      Log.d("getActiveNetworkInfo()", "is null");
    }
    boolean isAvailable = false;
    if (networkInfo != null && networkInfo.isConnected()) {
      isAvailable = true;
    }
    return isAvailable;
  }

  @Override
  public void onPermissionsGranted(int requestCode, List<String> perms) {

  }

  @Override
  public void onPermissionsDenied(int requestCode, List<String> perms) {

  }

  /**
   * This is an override of onRequestPermissionResult to take YES/NO of the dialog
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    // Forward results to EasyPermissions
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

  /**
   * On this method there is defined which permissions this application needs, so the request
   * execution start from here;
   */
  @AfterPermissionGranted(READ_PHONE_STATE)
  private void methodRequiredPermissions() {
    String[] perms = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE};
    if (EasyPermissions.hasPermissions(this, perms)) {

    } else {
      EasyPermissions
          .requestPermissions(this, "These access is mandatory", READ_PHONE_STATE, perms);
    }
  }
}
