package com.baitran.ali.last;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ali on 20/12/15.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #Baity";
    private ShareActionProvider mShareActionProvider;
    private static final int DETAIL_LOADER = 0;
    public static final String DATE_KEY = "date";
    public static final String LOCATION_KEY = "location";
    private String mForecastStr;
    private String mLocation;

    public ImageView mIconView;
    public TextView mFriendlyDateView;
    public TextView mDateView;
    public TextView mDescriptionView;
    public TextView mHighTempView;
    public TextView mLowTempView;
    public TextView mHumidityView;
    public TextView mWindView;
    public TextView mPressureView;


    String[] FORECAST_COLUMNS = {
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
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_LOC_KEY,


    };

    // These indices are tied to FORECAST_COLUMNS. If FORECAST_COLUMNS changes, these must change
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES= 8;
    public static final int COL_WEATHER_CONDITION_ID= 9;
    public static final int COL_LOC_KEY = 10;






    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        return rootView;

            /*Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String fs = intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.detail_text)).setText(fs);
            }*/


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != savedInstanceState)
        {
            mLocation=savedInstanceState.getString(LOCATION_KEY);
        }
        Bundle args = getArguments();
        //Intent intent = getActivity().getIntent();
        if (args !=null &&args.containsKey(DetailFragment.DATE_KEY)){

                getLoaderManager().initLoader(DETAIL_LOADER,null,this);
    }}

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //String dateString = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
       // String  dateString= getActivity().getIntent().getStringExtra(DATE_KEY);

        String  dateString= getArguments().getString(DATE_KEY);

        mLocation = Utility.getPreferredLocation(getActivity());
        Log.v(LOG_TAG,"locaaaaaaaaaaation :"+mLocation);
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStringDate(mLocation, dateString);
        return new CursorLoader(
                getActivity(),
                weatherUri,
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        int weatherId = data.getInt(COL_WEATHER_ID);

        // Use weather art image

        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_CONDITION_ID)));

        // Read date from cursor and update views for day of week and date
        long dateString = data.getLong(COL_WEATHER_DATE);

        String weatherDescription =
                data.getString(COL_WEATHER_DESC);
        mIconView.setContentDescription(weatherDescription);


        boolean isMetric = Utility.isMetric(getActivity());
        float high =data.getFloat(COL_WEATHER_MAX_TEMP);

        float low =data.getFloat(COL_WEATHER_MIN_TEMP);


        // Read humidity from cursor and update view
        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);

        // Read wind speed & direction
        float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDirStr = data.getFloat(COL_WEATHER_DEGREES);

        // Read pressure from the cursor
        float pressure = data.getFloat(COL_WEATHER_PRESSURE);

        mForecastStr = String.format("%s - %s - %s/%s",
                dateString, weatherDescription, high, low);

        mLowTempView.setText(Utility.formatTemperature(getActivity(), low,isMetric));
        mHighTempView.setText(Utility.formatTemperature(getActivity(), high,isMetric));
        mDateView.setText(Utility.getFriendlyDayString(getActivity(), dateString));
        mDescriptionView.setText(weatherDescription);
        mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));
        mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));
        mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null!=mLocation){
            outState.putString(LOCATION_KEY,mLocation);
        }}

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();

       // Intent intent = getActivity().getIntent();
        if (args !=null &&args.containsKey(DetailFragment.DATE_KEY)&&
                null!= mLocation && !mLocation.equals(Utility.getPreferredLocation(getActivity()))){
            getLoaderManager().restartLoader(DETAIL_LOADER,null,this);
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }


}
