package com.baitran.ali.last;

        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v7.app.ActionBarActivity;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;

        import com.baitran.ali.last.sync.MySyncAdapter;

public class MainActivity extends ActionBarActivity implements ForecastFragment.CallBack{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;

    @Override
    public void onItemSelected(String date) {
        if (mTwoPane){
            Bundle args = new Bundle();
            args.putString(DetailFragment.DATE_KEY,date);
            DetailFragment fragment =new DetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container,fragment)
                    .commit();
        }else {

                    Intent intent = new Intent(this,DetailActivity.class).putExtra(
                            DetailFragment.DATE_KEY,date
                    );
                    startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container )!= null){
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
        }else {
            mTwoPane =false;
        }
        ForecastFragment f =((ForecastFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        f.setUseTodayLayout(!mTwoPane);
        MySyncAdapter.initializeSync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}
