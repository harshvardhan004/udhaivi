package com.udhaivi.udhaivihealthcare.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.TabPagerAdapter;
import com.udhaivi.udhaivihealthcare.databinding.FragmentNotificationsBinding;
import com.udhaivi.udhaivihealthcare.frags.HistoryFragment;
import com.udhaivi.udhaivihealthcare.frags.ReportFragment;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    View v;
    View mIndicator;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabPagerAdapter adapter;
    int indicatorWidth;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tabLayout = root.findViewById(R.id.tab);
        mIndicator = root.findViewById(R.id.indicator);
        viewPager = root.findViewById(R.id.viewPager);

//        final ReportFragment reportFragment = new ReportFragment();
        HistoryFragment historyFragment = new HistoryFragment();

//        tabLayout.addTab(tabLayout.newTab().setText("Report"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
//        tabLayout.addTab(tabLayout.newTab().setText("Favorites"));

        adapter = new TabPagerAdapter(getChildFragmentManager());
//        adapter.addFragment(reportFragment, "Report");
        adapter.addFragment(historyFragment, "History");

        viewPager.setOffscreenPageLimit(1);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                indicatorWidth = tabLayout.getWidth() / tabLayout.getTabCount();

                //Assign new width
                FrameLayout.LayoutParams indicatorParams = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();
                indicatorParams.width = indicatorWidth;
                mIndicator.setLayoutParams(indicatorParams);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mIndicator.getLayoutParams();

                //Multiply positionOffset with indicatorWidth to get translation
                float translationOffset =  (v+i) * indicatorWidth ;
                params.leftMargin = (int) translationOffset;
                mIndicator.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}