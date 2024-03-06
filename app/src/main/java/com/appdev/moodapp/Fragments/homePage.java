package com.appdev.moodapp.Fragments;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appdev.moodapp.AddDataActivity;
import com.appdev.moodapp.R;
import com.appdev.moodapp.Utils.DailyData;
import com.appdev.moodapp.Utils.ImageAdapter;
import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.CalendarDayLayoutBinding;
import com.appdev.moodapp.databinding.CalenderHeaderBinding;
import com.appdev.moodapp.databinding.FragmentHomePageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class homePage extends BaseFragment implements BaseFragment.HasToolbar {
    public FragmentHomePageBinding binding;

    private List<DailyData> dataList;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    String textSizeName, cMonth, cYear;


    public homePage() {
        super(R.layout.fragment_home_page);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomePageBinding.inflate(inflater, container, false);
        if (Utils.isDarkModeActivated(requireActivity())) {
            Utils.status_bar_dark(requireActivity(), R.color.black);
        } else {
            Utils.status_bar(requireActivity(), R.color.lig_bkg);
        }
        SharedPreferences preferences = requireContext().getSharedPreferences("text_size_prefs", Context.MODE_PRIVATE);
        textSizeName = preferences.getString("text_size", "");

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int totalPoints = sharedPreferences.getInt("totalPoints", 0);

        binding.point.setText(String.valueOf(totalPoints));

        firebaseAuth = FirebaseAuth.getInstance();
        binding.pg.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(requireContext(), R.color.common), PorterDuff.Mode.SRC_IN);
        binding.pg.setVisibility(View.VISIBLE);
        binding.exFiveCalendar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<DayOfWeek> daysOfWeek = daysOfWeek();
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(200);
        YearMonth endMonth = currentMonth.plusMonths(200);
        configureBinders(daysOfWeek);
        binding.exFiveCalendar.setup(startMonth, endMonth, daysOfWeek.get(0));
        binding.exFiveCalendar.scrollToMonth(currentMonth);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        binding.exFiveCalendar.setMonthScrollListener(calendarMonth -> {

            binding.listitem.setVisibility(View.INVISIBLE);
            YearMonth yearMonth = calendarMonth.getYearMonth();
            binding.exFiveMonthYearText.setText(Utils.displayText(yearMonth, false));

            cMonth = String.valueOf(yearMonth.getMonth());
            cYear = String.valueOf(yearMonth.getYear());

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
                            Calendar cal = Calendar.getInstance();
                            int curMonth = cal.get(Calendar.MONTH) + 1;
                            int curYear = cal.get(Calendar.YEAR);

                            dataList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                DailyData dailyData = snapshot.getValue(DailyData.class);
                                if (dailyData != null && isToday(dailyData.getDate()) && cMonth.equalsIgnoreCase(getMonthName(curMonth)) && cYear.equalsIgnoreCase(String.valueOf(curYear))) {
                                    binding.listitem.setVisibility(View.VISIBLE);
                                    binding.notAvailable.setVisibility(View.GONE);
                                    SharedPreferences preferences = binding.getRoot().getContext().getSharedPreferences("text_size_prefs", Context.MODE_PRIVATE);
                                    String textSizeName = preferences.getString("text_size", "");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    Date date;
                                    try {
                                        date = sdf.parse(dailyData.getDate());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        return;
                                    }

                                    // Extract month and day
                                    Calendar calendar = Calendar.getInstance();
                                    assert date != null;
                                    calendar.setTime(date);
                                    int monthNumber = calendar.get(Calendar.MONTH);
                                    int dayNumber = calendar.get(Calendar.DAY_OF_MONTH);

                                    // Get first three letters of the month name
                                    DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
                                    String[] months = dfs.getShortMonths();
                                    String monthName = months[monthNumber];

                                    // Get the first three letters of the month name
                                    String firstThreeLettersOfMonth = monthName.substring(0, 3);

                                    binding.calendarDay.setText(String.valueOf(dayNumber));
                                    binding.calendarMonth.setText(firstThreeLettersOfMonth);
                                    binding.textualData.setText(dailyData.getTextualData());

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
                                    int emojiResId;
                                    switch (dailyData.getEmoji()) {
                                        case "Happy":
                                            emojiResId = R.drawable.face1;
                                            break;
                                        case "Smile":
                                            emojiResId = R.drawable.face2;
                                            break;
                                        case "Sad":
                                            emojiResId = R.drawable.face4;
                                            break;
                                        case "Cry":
                                            emojiResId = R.drawable.face5;
                                            break;
                                        default:
                                            emojiResId = R.drawable.face3;
                                            break;
                                    }
                                    binding.imageBtn.setImageResource(emojiResId);

                                    if (dailyData.getImageUris() != null) {
                                        List<String> imageUrls = dailyData.getImageUris(); // Assuming getImageUris() returns a List<String>
                                        ImageAdapter adapter = new ImageAdapter(imageUrls, binding.getRoot().getContext());
                                        binding.RcImages.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false));
                                        binding.RcImages.setAdapter(adapter);
                                    }

                                } else {
                                    binding.listitem.setVisibility(View.GONE);
                                    binding.notAvailable.setVisibility(View.VISIBLE);
                                }
                            }
                            binding.pg.setVisibility(View.GONE);
                        } else {
                            // Hide the loadingLayout if there's no data
                            binding.pg.setVisibility(View.GONE);
                            binding.notAvailable.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors
                    }
                });

            }


            return null;
        });


        binding.exFiveNextMonthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YearMonth newCurrentMonth;
                dataList.clear();
                CalendarMonth VisibleMonth = binding.exFiveCalendar.findFirstVisibleMonth();
                if (VisibleMonth != null) {
                    newCurrentMonth = VisibleMonth.getYearMonth().plusMonths(1);

                    String newYearStr = String.valueOf(newCurrentMonth.getYear());
                    String newMonthStr = newCurrentMonth.format(DateTimeFormatter.ofPattern("MM"));
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(currentUser.getUid()).child(newYearStr).child(newMonthStr).child("dailyData");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                dataList.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    DailyData dailyData = snapshot.getValue(DailyData.class);
                                    if (dailyData != null) {
                                        dataList.add(dailyData);
                                    }
                                }
                                binding.pg.setVisibility(View.GONE);
                            } else {
                                // Hide the loadingLayout if there's no data
                                binding.pg.setVisibility(View.GONE);
                                binding.notAvailable.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors
                        }
                    });
                }

                CalendarMonth firstVisibleMonth = binding.exFiveCalendar.findFirstVisibleMonth();
                if (firstVisibleMonth != null) {
                    binding.exFiveCalendar.smoothScrollToMonth(firstVisibleMonth.getYearMonth().plusMonths(1));
                }
            }
        });

        binding.exFivePreviousMonthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YearMonth newCurrentMonth;
                dataList.clear();
                CalendarMonth VisibleMonth = binding.exFiveCalendar.findFirstVisibleMonth();
                if (VisibleMonth != null) {
                    newCurrentMonth = VisibleMonth.getYearMonth().minusMonths(1);

                    String newYearStr = String.valueOf(newCurrentMonth.getYear());
                    String newMonthStr = newCurrentMonth.format(DateTimeFormatter.ofPattern("MM"));
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(currentUser.getUid()).child(newYearStr).child(newMonthStr).child("dailyData");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                dataList.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    DailyData dailyData = snapshot.getValue(DailyData.class);
                                    if (dailyData != null) {
                                        dataList.add(dailyData);
                                    }
                                }
                                binding.pg.setVisibility(View.GONE);
                            } else {
                                // Hide the loadingLayout if there's no data
                                binding.pg.setVisibility(View.GONE);
                                binding.notAvailable.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors
                        }
                    });
                }


                CalendarMonth firstVisibleMonth = binding.exFiveCalendar.findFirstVisibleMonth();
                if (firstVisibleMonth != null) {
                    binding.exFiveCalendar.smoothScrollToMonth(firstVisibleMonth.getYearMonth().minusMonths(1));
                }
            }
        });


    }


    public static boolean isToday(String date) {
        LocalDate currentDate = LocalDate.now();
        return date.equals(currentDate.toString());
    }


    private void configureBinders(List<DayOfWeek> daysOfWeek) {
        binding.exFiveCalendar.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view, binding);
            }

            @Override
            public void bind(@NotNull DayViewContainer container, @NotNull CalendarDay data) {
                container.day = data;
                Context context = container.calendarDayLayoutBinding.getRoot().getContext();
                TextView textView = container.calendarDayLayoutBinding.calendarDayText;
                ImageView imageView = container.calendarDayLayoutBinding.imageBtn;
                textView.setText(String.valueOf(data.getDate().getDayOfMonth()));

                checkDataForDate(data, imageView);
                switch (textSizeName) {
                    case "medium":
                        textView.setTextAppearance(R.style.TextSizeMediumTextual);
                        break;
                    case "large":
                        textView.setTextAppearance(R.style.TextSizeLargeTextual);
                        break;
                    default:
                        textView.setTextAppearance(R.style.TextSizeDefaultTextual);
                        break;
                }

                if (data.getPosition() == DayPosition.MonthDate) {
                } else {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.example_5_text_grey));
                    container.calendarDayLayoutBinding.getRoot().setBackground(null);
                }
            }
        });
        binding.exFiveCalendar.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NotNull
            @Override
            public MonthViewContainer create(@NotNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NotNull MonthViewContainer container, @NotNull CalendarMonth data) {
                if (container.legendLayout.getTag() == null) {
                    container.legendLayout.setTag(data.getYearMonth());
                    for (int i = 0; i < container.legendLayout.getChildCount(); i++) {
                        TextView tv = (TextView) container.legendLayout.getChildAt(i);
                        tv.setText(daysOfWeek.get(i).getDisplayName(TextStyle.SHORT, Locale.getDefault()).toUpperCase(Locale.getDefault()));
                        switch (textSizeName) {
                            case "medium":
                                tv.setTextAppearance(R.style.WeekRowMedium);
                                break;
                            case "large":
                                tv.setTextAppearance(R.style.WeekRowLarge);
                                break;
                            default:
                                tv.setTextAppearance(R.style.WeekRow);
                                break;
                        }

                        Typeface customTypeface = ResourcesCompat.getFont(container.getView().getContext(), R.font.monserratmedium);
                        tv.setTypeface(customTypeface);
                    }
                }
            }
        });
    }

    private void checkDataForDate(CalendarDay data, ImageView imageView) {
        // Construct the path to fetch data for the current date from Firebase
        String yearStr = String.valueOf(data.getDate().getYear());
        String monthStr = String.format(Locale.getDefault(), "%02d", data.getDate().getMonthValue());
        String dayStr = String.format(Locale.getDefault(), "%02d", data.getDate().getDayOfMonth());
        DatabaseReference dateRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child(yearStr)
                .child(monthStr)
                .child("dailyData").child(dayStr);

        // Check if data exists for the current date
        dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // If data exists, set the image view to the selected emoji
                if (dataSnapshot.exists()) {
                    String emoji = dataSnapshot.child("emoji").getValue(String.class);
                    if (emoji != null) {
                        int emojiResId;
                        switch (emoji) {
                            case "Happy":
                                emojiResId = R.drawable.face1;
                                break;
                            case "Smile":
                                emojiResId = R.drawable.face2;
                                break;
                            case "Sad":
                                emojiResId = R.drawable.face4;
                                break;
                            case "Cry":
                                emojiResId = R.drawable.face5;
                                break;
                            default:
                                emojiResId = R.drawable.face3;
                                break;
                        }

                        if (emojiResId != 0) {
                            imageView.setImageResource(emojiResId);
                        }
                    } else {
                        imageView.setImageResource(R.drawable.face3);
                    }
                } else {
                    imageView.setImageResource(R.drawable.face3);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(binding.getRoot().getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private class DayViewContainer extends ViewContainer {
        CalendarDay day;
        CalendarDayLayoutBinding calendarDayLayoutBinding;
        Boolean isClicked = false;

        DayViewContainer(@NotNull View view, FragmentHomePageBinding binding) {
            super(view);
            calendarDayLayoutBinding = CalendarDayLayoutBinding.bind(view);
            view.setOnClickListener(v -> {
                isClicked = true;
                homePage.this.binding.listitem.setVisibility(View.GONE);
                homePage.this.binding.notAvailable.setVisibility(View.GONE);
                String dateString = day.getDate().toString();
                String[] dateParts = dateString.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int dayOfMonth = Integer.parseInt(dateParts[2]);

                Intent intent = new Intent(calendarDayLayoutBinding.getRoot().getContext(), AddDataActivity.class);

                intent.putExtra("day", dayOfMonth);
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                String monthStr = String.format(Locale.getDefault(), "%02d", month);
                String dayStr = String.format(Locale.getDefault(), "%02d", dayOfMonth);


                if (homePage.this.firebaseAuth.getCurrentUser() != null) {

                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(homePage.this.firebaseAuth.getCurrentUser().getUid()).
                            child(String.valueOf(year)).child(monthStr).child("dailyData").
                            child(dayStr).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {
                                        DailyData dailyData = dataSnapshot.getValue(DailyData.class);
                                        if (dailyData != null) {
                                            homePage.this.binding.listitem.setVisibility(View.VISIBLE);
                                            homePage.this.binding.notAvailable.setVisibility(View.GONE);

                                            SharedPreferences preferences = homePage.this.binding.getRoot().getContext().getSharedPreferences("text_size_prefs", Context.MODE_PRIVATE);
                                            String textSizeName = preferences.getString("text_size", "");
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                            Date date;
                                            try {
                                                date = sdf.parse(dailyData.getDate());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                                return;
                                            }

                                            // Extract month and day
                                            Calendar calendar = Calendar.getInstance();
                                            assert date != null;
                                            calendar.setTime(date);
                                            int monthNumber = calendar.get(Calendar.MONTH);
                                            int dayNumber = calendar.get(Calendar.DAY_OF_MONTH);

                                            // Get first three letters of the month name
                                            DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
                                            String[] months = dfs.getShortMonths();
                                            String monthName = months[monthNumber];

                                            // Get the first three letters of the month name
                                            String firstThreeLettersOfMonth = monthName.substring(0, 3);

                                            homePage.this.binding.calendarDay.setText(String.valueOf(dayNumber));
                                            homePage.this.binding.calendarMonth.setText(firstThreeLettersOfMonth);
                                            homePage.this.binding.textualData.setText(dailyData.getTextualData());

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
                                            int emojiResId;
                                            switch (dailyData.getEmoji()) {
                                                case "Happy":
                                                    emojiResId = R.drawable.face1;
                                                    break;
                                                case "Smile":
                                                    emojiResId = R.drawable.face2;
                                                    break;
                                                case "Sad":
                                                    emojiResId = R.drawable.face4;
                                                    break;
                                                case "Cry":
                                                    emojiResId = R.drawable.face5;
                                                    break;
                                                default:
                                                    emojiResId = R.drawable.face3;
                                                    break;
                                            }
                                            homePage.this.binding.imageBtn.setImageResource(emojiResId);

                                            if (dailyData.getImageUris() != null) {
                                                List<String> imageUrls = dailyData.getImageUris(); // Assuming getImageUris() returns a List<String>
                                                ImageAdapter adapter = new ImageAdapter(imageUrls, homePage.this.binding.getRoot().getContext());
                                                homePage.this.binding.RcImages.setLayoutManager(new LinearLayoutManager(homePage.this.binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false));
                                                homePage.this.binding.RcImages.setAdapter(adapter);
                                            }

                                        } else {
                                            homePage.this.binding.listitem.setVisibility(View.GONE);
                                        }
                                        homePage.this.binding.pg.setVisibility(View.GONE);
                                    } else {

                                        homePage.this.binding.pg.setVisibility(View.GONE);
                                        homePage.this.binding.notAvailable.setVisibility(View.VISIBLE);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    homePage.this.binding.notAvailable.setVisibility(View.GONE);

                                }
                            });

                }
//                if (isToday(String.valueOf(day.getDate()))) {
//                    if (day.getPosition() == DayPosition.MonthDate) {
////                        homePage.this.binding.listitem.setVisibility(View.VISIBLE);
//                        homePage.this.binding.notAvailable.setVisibility(View.GONE);
//                        isClicked = false;
////                        calendarDayLayoutBinding.getRoot().getContext().startActivity(intent);
//                    }
//                }
            });
        }
    }


    private static class MonthViewContainer extends ViewContainer {
        LinearLayout legendLayout;

        MonthViewContainer(@NotNull View view) {
            super(view);
            legendLayout = CalenderHeaderBinding.bind(view).legendLayout.getRoot();
        }
    }

    public void forDefault() {
        binding.calendarDay.setTextAppearance(R.style.RadioSize);
        binding.calendarMonth.setTextAppearance(R.style.RadioSize);
        binding.textualData.setTextAppearance(R.style.RadioSize);
    }

    public void forMedium() {
        binding.calendarDay.setTextAppearance(R.style.RadioSizeMedium);
        binding.calendarMonth.setTextAppearance(R.style.RadioSizeMedium);
        binding.textualData.setTextAppearance(R.style.RadioSizeMedium);
    }

    public void forLarge() {
        binding.calendarDay.setTextAppearance(R.style.RadioSizeLarge);
        binding.calendarMonth.setTextAppearance(R.style.RadioSizeLarge);
        binding.textualData.setTextAppearance(R.style.RadioSizeLarge);
    }

    @Override
    public Toolbar getToolbar() {
        return null;
    }

    private String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    @Override
    protected Integer getTitleRes() {
        return null;
    }


}