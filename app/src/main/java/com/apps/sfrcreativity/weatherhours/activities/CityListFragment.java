package com.apps.sfrcreativity.weatherhours.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.apps.sfrcreativity.weatherhours.R;
import com.apps.sfrcreativity.weatherhours.adapter.CityAdapter;
import com.apps.sfrcreativity.weatherhours.storage.DBHelper;

import java.util.ArrayList;

public class CityListFragment extends Fragment  {

    // TODO: Rename and change types of parameters
    private CityAdapter cityAdapter;
    private CityListFragmentListener cityListFragmentListener;

    public interface CityListFragmentListener extends CityAdapter.CityListInteractionListener{
        void showDialog();
        void onOptionsMenuItemClick(int id);
        void resetBackground(int Color);
    }

    public CityListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.city_list_fragment, container, false);
        cityListFragmentListener.resetBackground(view.getResources().getColor(R.color.colorPrimaryDark));
        DBHelper dbHelper= new DBHelper(view.getContext());
        ArrayList<String> cityList = dbHelper.getAllGeoCities();
        if(cityList!=null && cityList.size()>0) {

            RecyclerView cityRV = view.findViewById(R.id.cityList);
            LinearLayoutManager recyclerLayoutManager = new
                    LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
            cityRV.setLayoutManager(recyclerLayoutManager);
            cityAdapter = new
                    CityAdapter(cityList, new CityAdapter.CityListInteractionListener() {
                @Override
                public void onClickListener(String name, int pos) {
                    cityListFragmentListener.onClickListener(name, pos);
                }

                @Override
                public void onDeleteClickListener(String name, int pos) {
                    cityListFragmentListener.onDeleteClickListener(name, pos);
                    cityAdapter.removeItem(pos);
                }
            });
            cityRV.setAdapter(cityAdapter);
            cityRV.getAdapter().notifyDataSetChanged();
        } else {
            cityListFragmentListener.showDialog();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        cityListFragmentListener.onOptionsMenuItemClick(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            cityListFragmentListener = (CityListFragment.CityListFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement FragmentChangeListener, UIUpdateListener");
        }
    }
}
