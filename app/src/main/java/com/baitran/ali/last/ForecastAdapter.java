package com.baitran.ali.last;


        import android.content.Context;
        import android.database.Cursor;
        import android.support.v4.widget.CursorAdapter;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.baitran.ali.last.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;

    @Override
    public int getItemViewType(int position) {
        return (position== 0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext, high, isMetric) + "/" + Utility.formatTemperature(mContext,low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int idx_max_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        int idx_min_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        int idx_date = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
        int idx_short_desc = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);

        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        if (viewType == VIEW_TYPE_TODAY){
            layoutId=R.layout.list_item_forecast_today;
        }
        else if (viewType == VIEW_TYPE_FUTURE_DAY){
            layoutId = R.layout.list_item_forecast;
        }
       View view= LayoutInflater.from(context).inflate(layoutId, parent, false);
       ViewHolder viewHolder = new ViewHolder(view);
       view.setTag(viewHolder);
       return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder=(ViewHolder)view.getTag();
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        int viewType = getItemViewType(cursor.getPosition());
        if (viewType == VIEW_TYPE_TODAY){
            viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));

        }
        else if (viewType == VIEW_TYPE_FUTURE_DAY){
            viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
        }

        long dateString = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);

        viewHolder.dateView.setText(Utility.formatDate(dateString));

        String desc =cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        //TextView descTextView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
        viewHolder.descView.setText(desc);
        boolean isMetric =Utility.isMetric(context);
        float high =cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        //TextView highTv =(TextView)view.findViewById(R.id.list_item_high_textview);
        viewHolder.highView.setText(Utility.formatTemperature(context, high,isMetric));

        float low =cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
        //TextView lowTv =(TextView)view.findViewById(R.id.list_item_low_textview);
        viewHolder.lowView.setText(Utility.formatTemperature(context, low,isMetric));

       // TextView tv = (TextView)view;
        //tv.setText(convertCursorRowToUXFormat(cursor));
    }
    public static class ViewHolder{
        public final ImageView iconView;
        public final TextView  dateView;
        public final TextView descView;
        public final TextView highView;
        public final TextView lowView;

        public ViewHolder(View view){
            iconView = (ImageView)view.findViewById(R.id.list_item_icon);
            dateView = (TextView)view.findViewById(R.id.list_item_date_textview);
            descView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
            highView = (TextView)view.findViewById(R.id.list_item_high_textview);
            lowView = (TextView)view.findViewById(R.id.list_item_low_textview);

        }





    }
}