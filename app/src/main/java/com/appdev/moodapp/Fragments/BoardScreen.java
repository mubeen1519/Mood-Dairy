package com.appdev.moodapp.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.appdev.moodapp.R;
import com.appdev.moodapp.Recyclerview.DailyDataAdapter;
import com.appdev.moodapp.Utils.DailyData;
import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.FragmentBoardScreenBinding;
import com.appdev.moodapp.databinding.FragmentHomePageBinding;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import io.github.farshidroohi.ChartEntity;


public class BoardScreen extends Fragment {
    public FragmentBoardScreenBinding binding;
    private DatabaseReference databaseReference;
    private String selectedEmoji = "Neutral";
    private DailyDataAdapter adapter;
    private List<DailyData> dataList;


    //    private final List<String> legendArr = new ArrayList<String>();
//private final List<String> legendArr = new ArrayList<String>() {{
//    add("12/01");
//    add("12/06");
//    add("12/11");
//    add("12/16");
//    add("12/21");
//}};
    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBoardScreenBinding.inflate(inflater, container, false);
        Utils.status_bar(requireActivity(), R.color.lig_bkg);
        firebaseAuth = FirebaseAuth.getInstance();
        binding.rcProgress.setVisibility(View.VISIBLE);

        LocalDate currentDate = LocalDate.now();

        // Get the current year
        int currentYear = currentDate.getYear();

        // Get the first three letters of the month name
        String monthName = currentDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        binding.titleMonth.setText(monthName + "," + currentYear);

//
//        for (int day = 1; day <= 31; day++) {
//            legendArr.add(String.format(Locale.getDefault(),"12/%02d", day));
//        }
//
//        generateRandomValues();
//
//        // Convert lineValues to Entry array
//        float[] lineEntries = new float[lineValues.size()];
//        for (int i = 0; i < lineValues.size(); i++) {
//            lineEntries[i] = lineValues.get(i);
//        }
//
//        ChartEntity firstChartEntity = new ChartEntity(Color.argb(255, 25, 104, 199), firstChartGraph1());
//
//
//        // Create list and add ChartEntity
//        List<ChartEntity> list = new ArrayList<>();
//        list.add(firstChartEntity);
//
//        // Set legend and list to LineChart
//       binding.lineChart.setLegend(legendArr);
//       binding.lineChart.setList(list);

        ArrayList<Entry> linevalues = new ArrayList<>();
        linevalues.add(new Entry(0, 20f));
        linevalues.add(new Entry(1, 30f));
        linevalues.add(new Entry(2, 40f));
        linevalues.add(new Entry(3, 50f));
        linevalues.add(new Entry(4, 60f));

        LineDataSet linedataset = new LineDataSet(linevalues, "First");
        linedataset.setColor(getResources().getColor(R.color.common));
        linedataset.setCircleRadius(4f);
        linedataset.setDrawFilled(true);
        linedataset.setValueTextSize(10F);
//        linedataset.setFillColor(getResources().getColor(R.color.green));
        linedataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);

//        List<String> xAxisLabels = new ArrayList<>();
//        xAxisLabels.add("12/01");
//        xAxisLabels.add("12/07");
//        xAxisLabels.add("12/14");
//        xAxisLabels.add("12/21");
//        xAxisLabels.add("12/28");


        LineData data = new LineData(linedataset);
        binding.getTheGraph.setData(data);

        // Set custom x-axis labels
        XAxis xAxis = binding.getTheGraph.getXAxis();
//        xAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                int intValue = (int) value;
//                if (intValue >= 0 && intValue < xAxisLabels.size()) {
//                    return xAxisLabels.get(intValue);
//                } else {
//                    return "";
//                }
//            }
//        });

        binding.getTheGraph.setBackgroundColor(getResources().getColor(R.color.white));
        binding.getTheGraph.animateXY(2000, 2000, Easing.EaseInCubic);
//
//
//        ArrayList<Entry> linevalues = new ArrayList<>();
//        linevalues.add(new Entry(20f, 0.0F));
//        linevalues.add(new Entry(27f, 7.0F));
//        linevalues.add(new Entry(30f, 3.0F));
//        linevalues.add(new Entry(40f, 2.0F));
//        linevalues.add(new Entry(50f, 1.0F));
//        linevalues.add(new Entry(60f, 8.0F));
//        linevalues.add(new Entry(70f, 10.0F));
//        linevalues.add(new Entry(80f, 1.0F));
//        linevalues.add(new Entry(90f, 2.0F));
//        linevalues.add(new Entry(100f, 5.0F));
//        linevalues.add(new Entry(110f, 1.0F));
//        linevalues.add(new Entry(120f, 20.0F));
//        linevalues.add(new Entry(130f, 40.0F));
//        linevalues.add(new Entry(140f, 50.0F));
//
//        LineDataSet linedataset = new LineDataSet(linevalues, "First");
//        linedataset.setColor(getResources().getColor(R.color.common));
//        linedataset.setCircleRadius(4f);
//        linedataset.setDrawFilled(true);
//        linedataset.setValueTextSize(10F);
////        linedataset.setFillColor(getResources().getColor(R.color.green));
//        linedataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//
//        LineData data = new LineData(linedataset);
//
//        LineChart lineChart = binding.getTheGraph;
//        lineChart.setData(data);
//        lineChart.setBackgroundColor(getResources().getColor(R.color.white));
//        lineChart.animateXY(2000, 2000, Easing.EaseInCubic);

//        Description description = new Description();
//        description.setText("Mood Flow");
//        description.setPosition(150f,15f);
//        lineChart.setDescription(description);
//        lineChart.getAxisRight().setDrawLabels(false);
//
//        vValues = Arrays.asList("12/01","12/06","12/11","12/16","12/21");
//        XAxis Axis = lineChart.getXAxis();
//        Axis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        Axis.setValueFormatter(new IndexAxisValueFormatter(vValues));
//        Axis.setLabelCount(5);
//        Axis.setGranularity(1f);
//
//
//        YAxis yAxis = lineChart.getAxisLeft();
//        Axis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        Axis.setValueFormatter(new IndexAxisValueFormatter(vValues));
//        Axis.setLabelCount(5);
//        Axis.setGranularity(1f);


        return binding.getRoot();
    }

    //    private void generateRandomValues() {
//        Random random = new Random();
//        for (int i = 0; i < legendArr.size(); i++) {
//            lineValues.add(random.nextFloat() * 100000); // Adjust the range as needed
//        }
//    }
    private float[] firstChartGraph1() {
        return new float[]{70000f, 190000f, 300000f, 400000f, 24000f};
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        YearMonth currentMonth = YearMonth.now();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {


            String yearStr = String.valueOf(currentMonth.getYear());
            String monthStr = currentMonth.format(DateTimeFormatter.ofPattern("MM"));

            databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(currentUser.getUid()).child(yearStr).child(monthStr).child("dailyData");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int totalMoods = 0;
                        int happyCount = 0;
                        int smileCount = 0;
                        int neutralCount = 0;
                        int sadCount = 0;
                        int cryCount = 0;
                        int happyPercentage = 0;
                        int smilePercentage = 0;
                        int neutralPercentage = 0;
                        int sadPercentage = 0;
                        int cryPercentage = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DailyData dailyData = snapshot.getValue(DailyData.class);
                            if (dailyData != null) {
                                dataList.add(dailyData);
                                totalMoods++;
                                selectedEmoji = dailyData.getEmoji();
                                switch (dailyData.getEmoji()) {
                                    case "Happy":
                                        happyCount++;
                                        break;
                                    case "Smile":
                                        smileCount++;
                                        break;
                                    case "Neutral":
                                        neutralCount++;
                                        break;
                                    case "Sad":
                                        sadCount++;
                                        break;
                                    case "Cry":
                                        cryCount++;
                                        break;
                                    default:
                                        break;
                                }
                                // Calculate percentages
                                int totalPercentage = 100;
                                happyPercentage = (int) ((happyCount / (float) totalMoods) * totalPercentage);
                                smilePercentage = (int) ((smileCount / (float) totalMoods) * totalPercentage);
                                neutralPercentage = (int) ((neutralCount / (float) totalMoods) * totalPercentage);
                                sadPercentage = (int) ((sadCount / (float) totalMoods) * totalPercentage);
                                cryPercentage = (int) ((cryCount / (float) totalMoods) * totalPercentage);

                                setProgressBarWeight(binding.Progress1, happyPercentage);
                                setProgressBarWeight(binding.Progress2, smilePercentage);
                                setProgressBarWeight(binding.Progress3, neutralPercentage);
                                setProgressBarWeight(binding.Progress4, sadPercentage);
                                setProgressBarWeight(binding.Progress5, cryPercentage);

                                binding.Progress2.setProgress(smilePercentage);
                                binding.Progress3.setProgress(neutralPercentage);
                                binding.Progress4.setProgress(sadPercentage);
                                binding.Progress5.setProgress(cryPercentage);
                                binding.Progress1.setProgress(happyPercentage);

                                binding.happyPercentage.setText(happyPercentage + "%");
                                binding.smilePercentage.setText(smilePercentage + "%");
                                binding.neutralPercentage.setText(neutralPercentage + "%");
                                binding.sadPercentage.setText(sadPercentage + "%");
                                binding.cryPercentage.setText(cryPercentage + "%");
                            }
                        }
                        adapter.notifyDataSetChanged();
                        binding.rcProgress.setVisibility(View.GONE);
                        binding.Progress1.setVisibility(happyPercentage > 0 ? View.VISIBLE : View.GONE);
                        binding.Progress2.setVisibility(smilePercentage > 0 ? View.VISIBLE : View.GONE);
                        binding.Progress3.setVisibility(neutralPercentage > 0 ? View.VISIBLE : View.GONE);
                        binding.Progress4.setVisibility(sadPercentage > 0 ? View.VISIBLE : View.GONE);
                        binding.Progress5.setVisibility(cryPercentage > 0 ? View.VISIBLE : View.GONE);

                        binding.pg.setVisibility(View.GONE);
                        binding.progressLayout.setVisibility(View.VISIBLE);
                        binding.progressPercent.setVisibility(View.VISIBLE);
                    } else {
                        binding.pg.setVisibility(View.GONE);
                        binding.rcProgress.setVisibility(View.GONE);
                        binding.Progress3.setProgress(100);
                        binding.happyPercentage.setText(0 + "%");
                        binding.smilePercentage.setText(0 + "%");
                        binding.neutralPercentage.setText(0 + "%");
                        binding.sadPercentage.setText(0 + "%");
                        binding.cryPercentage.setText(0 + "%");
                        binding.progressPercent.setVisibility(View.VISIBLE);
                        binding.nodata.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });
            adapter = new DailyDataAdapter(dataList);
            binding.ListRc.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            binding.ListRc.setAdapter(adapter);
        }
    }

    private void setProgressBarWeight(ProgressBar progressBar, int weight) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) progressBar.getLayoutParams();
        layoutParams.weight = weight;
        progressBar.setLayoutParams(layoutParams);
    }
}