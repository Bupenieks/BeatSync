package com.benupenieks.beatsync.RowingActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AestheticActivity;
import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageFragment;
import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionFragment;
import com.benupenieks.beatsync.MainActivity.MainActivity;
import com.benupenieks.beatsync.MainActivity.MainContract;
import com.benupenieks.beatsync.MainActivity.MainPresenter;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class RowingActivity extends AestheticActivity implements RowingContract.View {

    private RowingContract.Presenter mPresenter;
    private LineChart mAccelerometerGraph;
    private AccelerometerGraphData mGraphData = new AccelerometerGraphData();

    private class AccelerometerGraphData {
        public LineDataSet dataSet;
        public LineData data;
        public List<Entry> entries = new ArrayList<>();

        private final int MAX_DATA_POINTS = 300;

        public void updateData(Entry entry) {
            entries.add(entry);
            int numEntries = entries.size();
            if (numEntries == 2) {
                // init graph
                dataSet = new LineDataSet(entries, "Accelerometer");
                dataSet.setColors(R.color.colorAccent);
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                List<ILineDataSet> tempHolder = new ArrayList<>();
                tempHolder.add(dataSet);
                data = new LineData(tempHolder);
                dataSet.setDrawValues(false);
                dataSet.setLineWidth(3.f);
                dataSet.setDrawCircles(false);
                mAccelerometerGraph.setData(data);
            } else if (numEntries > 2) {
                // update graph

                if (numEntries >= MAX_DATA_POINTS) {
                    dataSet.removeEntry(0);
                    entries.remove(0);
                }
                dataSet.addEntry(entry);
                data.notifyDataChanged();
                mAccelerometerGraph.notifyDataSetChanged();
            }
            mAccelerometerGraph.invalidate();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_rowing);

        Aesthetic.get()
                .colorPrimaryRes(R.color.colorPrimary)
                .colorWindowBackgroundRes(R.color.colorBackground)
                .colorAccentRes(R.color.colorAccent)
                .apply();

        mPresenter = new RowingPresenter();
        mPresenter.attachView(this);


        /*findViewById(R.id.finish_rowing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RowingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });*/


        // Graph formatting
        mAccelerometerGraph = (LineChart) findViewById(R.id.accelerometer_graph);
        mAccelerometerGraph.setDrawGridBackground(false);
        mAccelerometerGraph.setDrawBorders(false);
        mAccelerometerGraph.getAxisLeft().setDrawLabels(false);
        mAccelerometerGraph.getAxisRight().setDrawLabels(false);
        mAccelerometerGraph.getXAxis().setDrawLabels(false);
        mAccelerometerGraph.getLegend().setEnabled(false);
        mAccelerometerGraph.getAxisLeft().setDrawGridLines(false);
        mAccelerometerGraph.getAxisLeft().setEnabled(false);
        mAccelerometerGraph.getXAxis().setEnabled(false);
        mAccelerometerGraph.getAxisRight().setEnabled(false);

        Description des = mAccelerometerGraph.getDescription();
        des.setEnabled(false);

    }


    // @Override
    public void attachPresenter() {
        mPresenter = (RowingPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null) {
            mPresenter = new RowingPresenter();
        }
        mPresenter.attachView(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onPause();
    }


    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void finish(int strokeRate) {
        EventBus.getDefault().postSticky(strokeRate);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("stroke_rate", strokeRate);
        setResult(Activity.RESULT_OK, intent);
        startActivity(intent);
    }

    public void updateGraph(float x, float y) {
        mGraphData.updateData(new Entry(x, y));
    }
}