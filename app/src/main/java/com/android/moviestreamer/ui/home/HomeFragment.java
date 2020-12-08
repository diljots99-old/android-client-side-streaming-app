package com.android.moviestreamer.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.android.moviestreamer.R;
import com.android.moviestreamer.ui.livetv.LiveTVFragment;
import com.android.moviestreamer.ui.movies.MoviesFragment;
import com.android.moviestreamer.ui.series.SeriesFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class    HomeFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    MoviesFragment moviesFragment;
    SeriesFragment seriesFragment;
    LiveTVFragment liveTVFragment;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = root.findViewById(R.id.view_pager_home_fragment);
        tabLayout = root.findViewById(R.id.tabLayout_home_fragment);

        moviesFragment = new MoviesFragment();
        seriesFragment = new SeriesFragment();
        liveTVFragment = new LiveTVFragment();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);
        viewPagerAdapter.addFragment(moviesFragment,"Movies");
        viewPagerAdapter.addFragment(seriesFragment,"Series");
        viewPagerAdapter.addFragment(liveTVFragment,"Live TV");

        viewPager.setAdapter(viewPagerAdapter);

        return root;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}