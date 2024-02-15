package com.appdev.moodapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.appdev.moodapp.timeline;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
import java.util.List;
import java.util.Locale;


public class BoardScreen extends BaseFragment implements BaseFragment.HasToolbar {
    public FragmentBoardScreenBinding binding;
    private DatabaseReference databaseReference;
    private String selectedEmoji = "Neutral";
    private DailyDataAdapter adapter;
    private List<DailyData> dataList;

    String textSizeName;

    //    private final List<String> legendArr = new ArrayList<String>();
//private final List<String> legendArr = new ArrayList<String>() {{
//    add("12/01");
//    add("12/06");
//    add("12/11");
//    add("12/16");
//    add("12/21");
//}};

    private FirebaseAuth firebaseAuth;

    public BoardScreen() {
        super(R.layout.fragment_board_screen);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBoardScreenBinding.inflate(inflater, container, false);
        if(Utils.isDarkModeActivated(requireActivity())){
            Utils.status_bar_dark(requireActivity(), R.color.black);
        } else{
            Utils.status_bar(requireActivity(), R.color.lig_bkg);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        binding.rcProgress.setVisibility(View.VISIBLE);

        SharedPreferences preferences = requireContext().getSharedPreferences("text_size_prefs", Context.MODE_PRIVATE);
        textSizeName = preferences.getString("text_size", "");


        binding.pg.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(requireContext(), R.color.common), PorterDuff.Mode.SRC_IN);
        binding.rcProgress.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.common), PorterDuff.Mode.SRC_IN);


        LocalDate currentDate = LocalDate.now();

        // Get the current year
        int currentYear = currentDate.getYear();

        // Get the first three letters of the month name
        String monthName = currentDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        binding.titleMonth.setText(monthName + " , " + currentYear);

        YearMonth currentMonth = YearMonth.now();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            dataList = new ArrayList<>();
            String yearStr = String.valueOf(currentMonth.getYear());
            String monthStr = currentMonth.format(DateTimeFormatter.ofPattern("MM"));

            databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(currentUser.getUid()).child(yearStr).child(monthStr).child("dailyData");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                       dataList.clear();
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

                                ArrayList<Entry> entries = new ArrayList<>();
                                entries.add(new Entry(0, happyPercentage)); // Assuming the first day of the month is at index 0
                                entries.add(new Entry(1, smilePercentage));
                                entries.add(new Entry(2, neutralPercentage));
                                entries.add(new Entry(3, sadPercentage));
                                entries.add(new Entry(4, cryPercentage));

                                // Create LineDataSet and LineData
                                LineDataSet dataSet = new LineDataSet(entries, "Mood");
                                LineData lineData = new LineData(dataSet);

                                // Set LineData to LineChart
                                LineChart lineChart = binding.chart1;
                                lineChart.getDescription().setEnabled(false);
                                lineChart.setDrawGridBackground(false);
                                lineChart.setData(lineData);
                                lineChart.getAxisRight().setEnabled(false);
                                // Customize Axes if needed
                                XAxis xAxis = lineChart.getXAxis();
                                // Customize xAxis if needed
                                xAxis.setDrawLimitLinesBehindData(false);
                                XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
                                xAxis.setPosition(position);
                                xAxis.setDrawGridLines(false);



                                YAxis yAxis = lineChart.getAxisLeft();
                                yAxis.setDrawGridLines(false);
                                // Customize yAxis if needed

                                // Refresh Chart
                                lineChart.invalidate();

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
            binding.newList.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            binding.newList.setAdapter(adapter);
        }

        binding.timelinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(),timeline.class));
            }
        });


        return binding.getRoot();
    }
    public void forDefault() {
        binding.moodflow.setTextAppearance(R.style.TextSizeDefaultTextual);
        binding.barmood.setTextAppearance(R.style.TextSizeDefaultTextual);
        binding.timelinebtn.setTextAppearance(R.style.WeekRow);
        binding.timeline.setTextAppearance(R.style.WeekRow);
        binding.happyPercentage.setTextAppearance(R.style.RadioSize);
        binding.smilePercentage.setTextAppearance(R.style.RadioSize);
        binding.neutralPercentage.setTextAppearance(R.style.RadioSize);
        binding.sadPercentage.setTextAppearance(R.style.RadioSize);
        binding.cryPercentage.setTextAppearance(R.style.RadioSize);
    }

    public void forMedium() {
        binding.moodflow.setTextAppearance(R.style.TextSizeMediumTextual);
        binding.barmood.setTextAppearance(R.style.TextSizeMediumTextual);
        binding.timelinebtn.setTextAppearance(R.style.WeekRowMedium);
        binding.timeline.setTextAppearance(R.style.WeekRowMedium);
        binding.happyPercentage.setTextAppearance(R.style.RadioSizeMedium);
        binding.smilePercentage.setTextAppearance(R.style.RadioSizeMedium);
        binding.neutralPercentage.setTextAppearance(R.style.RadioSizeMedium);
        binding.sadPercentage.setTextAppearance(R.style.RadioSizeMedium);
        binding.cryPercentage.setTextAppearance(R.style.RadioSizeMedium);
    }

    public void forLarge() {
        binding.moodflow.setTextAppearance(R.style.TextSizeLargeTextual);
        binding.barmood.setTextAppearance(R.style.TextSizeLargeTextual);
        binding.timelinebtn.setTextAppearance(R.style.WeekRowLarge);
        binding.timeline.setTextAppearance(R.style.WeekRowLarge);
        binding.happyPercentage.setTextAppearance(R.style.RadioSizeLarge);
        binding.smilePercentage.setTextAppearance(R.style.RadioSizeLarge);
        binding.neutralPercentage.setTextAppearance(R.style.RadioSizeLarge);
        binding.sadPercentage.setTextAppearance(R.style.RadioSizeLarge);
        binding.cryPercentage.setTextAppearance(R.style.RadioSizeLarge);
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
        switch (textSizeName) {
            case "medium":
                forMedium();
                break;
            case "large":
                forLarge();
                break;
            default:
                forDefault();
                break;
        }

    }

    private void setProgressBarWeight(ProgressBar progressBar, int weight) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) progressBar.getLayoutParams();
        layoutParams.weight = weight;
        progressBar.setLayoutParams(layoutParams);
    }

    @Override
    public Toolbar getToolbar() {
        return null;
    }

    @Override
    protected Integer getTitleRes() {
        return null;
    }
}