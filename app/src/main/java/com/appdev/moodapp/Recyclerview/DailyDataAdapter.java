package com.appdev.moodapp.Recyclerview;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appdev.moodapp.R;
import com.appdev.moodapp.Utils.DailyData;
import com.appdev.moodapp.Utils.ImageAdapter;
import com.appdev.moodapp.databinding.SecondSampleBinding;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyDataAdapter extends RecyclerView.Adapter<DailyDataAdapter.ViewHolder> {

    private List<DailyData> dataList;

    public DailyDataAdapter(List<DailyData> dataList) {
        this.dataList = dataList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SecondSampleBinding binding = SecondSampleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyData data = dataList.get(position);
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final SecondSampleBinding binding;

        public ViewHolder(@NonNull SecondSampleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DailyData data) {
            SharedPreferences preferences = binding.getRoot().getContext().getSharedPreferences("text_size_prefs", Context.MODE_PRIVATE);
            String textSizeName = preferences.getString("text_size", "");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date;
            try {
                date = sdf.parse(data.getDate());
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
            binding.textualData.setText(data.getTextualData());

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
            switch (data.getEmoji()) {
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

            if (data.getImageUris() != null) {
                List<String> imageUrls = data.getImageUris(); // Assuming getImageUris() returns a List<String>
                ImageAdapter adapter = new ImageAdapter(imageUrls, binding.getRoot().getContext());
                binding.RcImages.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false));
                binding.RcImages.setAdapter(adapter);
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
    }

}


