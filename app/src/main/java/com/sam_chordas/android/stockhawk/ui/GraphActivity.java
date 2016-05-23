package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CURSOR_LOADER_ID = 2;
    private String mSymbol;
    private LineChart mChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        Bundle extras = getIntent().getExtras();
        mSymbol= extras.getString(MyStocksActivity.Symbol_key);


        mChart = (LineChart) findViewById(R.id.chart1);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String select = "(("+QuoteColumns.ISCURRENT + " = ? ) AND ("+QuoteColumns.SYMBOL + " = ? ))";
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                select,
                new String[]{"0",mSymbol},
                null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mChart.clear();
        mChart.setDescription("A data From "+mSymbol);
        mChart.setFocusable(true);
        Legend legend = mChart.getLegend();
        legend.setTextSize(15f);

        mChart.setData(setChartData(data));
        mChart.setDescriptionColor(Color.BLUE);


        Log.d("Cursor",String.valueOf(data.getCount() ));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private LineData setChartData(Cursor data)
    {
        data.moveToFirst();
        ArrayList<String> xVals = new ArrayList<String>();
        int count = data.getCount();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float bidData = Float.valueOf(data.getString(2));
            yVals.add(new Entry(bidData, i));
            data.moveToNext();
        }

        LineDataSet set1;
        set1 = new LineDataSet(yVals, mSymbol);

         //set1.setFillAlpha(110);
         //set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.setColor(Color.BLUE);
        set1.setCircleColor(Color.BLUE);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(12f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
         return new LineData(xVals, dataSets);
    }
}
