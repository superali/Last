package com.baitran.ali.last;

/**
 * Created by ali on 08/12/15.
 */


        import android.app.AlarmManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.LoaderManager;
        import android.support.v4.content.CursorLoader;
        import android.support.v4.content.Loader;
        import android.support.v4.widget.SimpleCursorAdapter;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ListView;
        import android.widget.TextView;

        import  com.baitran.ali.last.WeatherContract;
        import com.baitran.ali.last.sync.MySyncAdapter;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> ,
                            SharedPreferences.OnSharedPreferenceChangeListener {
    private String mlocation;
    private int mPosition;
    private static final String SELECTED_KEY = "selected_item";
    private ListView mListView;
    private boolean mUseTodayLayout;
    private static final int FORECAST_LOADER = 0;
    private ForecastAdapter mForecastAdapter;
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have _id columns)
            // On the one hand, that's annoying. On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG

    };

    // These indices are tied to FORECAST_COLUMNS. If FORECAST_COLUMNS changes, these must change
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_CONDITION_ID = 5;
    public static final int COL_LOCATION_SETTING = 6;
    public static final int COL_LOCATION_LAT = 7;
    public static final int COL_LOCATION_LONG = 8;


    public interface CallBack {
        public void onItemSelected(String date);
    }

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //if (id == R.id.action_refresh) {
        // updateWeather();
        //  return true;
        // }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setUseTodayLayout(boolean UseTodayLayout) {
        mUseTodayLayout = UseTodayLayout;
        if (mForecastAdapter != null) {
            mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The CursorAdapter will take data from our cursor and populate the ListView.
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mForecastAdapter);
        View empty_view = rootView.findViewById(R.id.empty_view);
        mListView.setEmptyView(empty_view);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //SimpleCursorAdapter adapter = (SimpleCursorAdapter)adapterView.getAdapter();
                Cursor cursor = mForecastAdapter.getCursor();
                if (null != cursor && cursor.moveToPosition(i)) {
                    boolean isMetric = Utility.isMetric(getActivity());
                    ((CallBack) getActivity()).onItemSelected("" + cursor.getLong(COL_WEATHER_DATE));

                    mPosition = i;
                    /*Intent intent = new Intent(getActivity(),DetailActivity.class).putExtra(
                            DetailFragment.DATE_KEY,""+cursor.getLong(COL_WEATHER_DATE)
                    );
                    startActivity(intent);*/

                }
            }


        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateWeather() {
        /*Intent alarmIntent = new Intent(getActivity(),FirstService.AlarmReceiver.class);
        alarmIntent.putExtra(FirstService.LOCATION_QUERY_EXTRA,mlocation);
        PendingIntent pi =  PendingIntent.getBroadcast(getActivity(),0,alarmIntent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE );
        am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+5000,pi);
        weatherTask = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
        Intent intent = new Intent(getActivity(), FirstService.class);
        intent.putExtra(FirstService.LOCATION_QUERY_EXTRA,
                Utility.getPreferredLocation(getActivity()));

        getActivity().startService(intent);*/
        MySyncAdapter.initializeSync(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        // updateWeather();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
        if (mlocation != null && !Utility.getPreferredLocation(getActivity()).equals(mlocation)) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mlocation = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mlocation, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mForecastAdapter.swapCursor(cursor);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mForecastAdapter.swapCursor(null);
    }

    private void openPreferredLocationInMap() {
        if (null != mForecastAdapter) {
            Cursor cursor = mForecastAdapter.getCursor();
            if (null != cursor) {
                cursor.moveToPosition(0);
                String poslat = cursor.getString(COL_LOCATION_LAT);
                String poslong = cursor.getString(COL_LOCATION_LONG);
                Uri geoLocation = Uri.parse("geo:" + poslat + "," + poslong);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d("Cant execute", "Couldn't call " + poslat + " " + poslong + ", no receiving apps installed!");
                }
            }
        }
    }

    private void updateEmptyView() {
        if (mForecastAdapter.getCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.empty_view);

            if (null != tv) {
                int message = R.string.empty_string;
                @MySyncAdapter.LocationStatus int location = Utility.getLocationStatus(getActivity());
                switch (location) {
                    case MySyncAdapter.LOCATION_STATUS_SERVER_DOWN:
                        message = R.string.empty_string_server_down;
                        break;
                    case MySyncAdapter.LOCATION_STATUS_SERVER_INVALID:
                        message = R.string.empty_string_server_error;
                        break;
                    case MySyncAdapter.LOCATION_STATUS_INVALID:
                        message = R.string.empty_string_location_invalid;
                        break;
                    default:
                        if (Utility.isNetworkAvailable(getActivity())) {
                            Log.d("empty_string_no_network","empty_string_no_network");
                             message = R.string.empty_string_no_network;
                        }
                }
                tv.setText(message);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_location_status_key))) {
            updateEmptyView();
        }


        /*if (key.equals(getString(R.string.pref_units_key))) {
            Context c=getActivity();

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
            SharedPreferences.Editor spe = sp.edit();
            spe.putInt(c.getString(R.string.pref_units_key),locationStatus);
            spe.apply();            tempUnitsPref.setSummary(sharedPreferences.getString(key, getString(R.string.pref_units_metric)));
        }*/


    }
}