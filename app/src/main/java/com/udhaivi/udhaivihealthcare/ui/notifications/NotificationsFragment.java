package com.udhaivi.udhaivihealthcare.ui.notifications;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.TabPagerAdapter;
import com.udhaivi.udhaivihealthcare.databinding.FragmentNotificationsBinding;
import com.udhaivi.udhaivihealthcare.frags.HistoryFragment;
import com.udhaivi.udhaivihealthcare.frags.ReportFragment;
import com.udhaivi.udhaivihealthcare.menu.Payment;
import com.udhaivi.udhaivihealthcare.menu.PhotoUpload;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    View v;
    View mIndicator;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabPagerAdapter adapter;
    int indicatorWidth;
    ImageView addhistory;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tabLayout = root.findViewById(R.id.tab);
        mIndicator = root.findViewById(R.id.indicator);
        viewPager = root.findViewById(R.id.viewPager);
        addhistory = root.findViewById(R.id.addhistory);

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

        addhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomsheetdialog();
            }
        });

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

    private CardView cd1;

    public void bottomsheetdialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.TransparentDialog);
            View parentView = getLayoutInflater().inflate(R.layout.add_new_files, null);
        bottomSheetDialog.setContentView(parentView);

        cd1 = parentView.findViewById(R.id.cd1);
        cd1.setBackgroundResource(R.drawable.card_view_bg);

        bottomSheetDialog.show();

//        CardView video = parentView.findViewById(R.id.video);
//        video.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ((ProfileActivity)getContext()).videofrag(Attemptname, pid);
//
//                bottomSheetDialog.dismiss();
//            }
//        });
//        CardView pdf = parentView.findViewById(R.id.pdf);
//        pdf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ((ProfileActivity)getContext()).pdffrag(Attemptname, pid);
//
//                bottomSheetDialog.dismiss();
//            }
//        });

//        CardView folder = parentView.findViewById(R.id.folder);
//        folder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ((ProfileActivity)getContext()).folderfrag(Attemptname, pid);
//
//                bottomSheetDialog.dismiss();
//            }
//        });

        CardView stdtxt = parentView.findViewById(R.id.photo);
        stdtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
                startActivity(new Intent(getActivity(), PhotoUpload.class));
            }
        });


        CardView payment = parentView.findViewById(R.id.payment);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
                startActivity(new Intent(getActivity(), Payment.class));
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}