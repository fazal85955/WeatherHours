package com.apps.sfrcreativity.weatherhours.activities;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.apps.sfrcreativity.weatherhours.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        return new PreferenceGroupAdapter(preferenceScreen) {
            @SuppressLint("RestrictedApi")
            @Override
            public void onBindViewHolder(PreferenceViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                Preference preference = getItem(position);
                if (preference instanceof PreferenceCategory)
                    setZeroPaddingToLayoutChildren(holder.itemView);
                else {
                    View iconFrame = holder.itemView.findViewById(R.id.icon_frame);
                    if (iconFrame != null) {
                        iconFrame.setVisibility(preference.getIcon() == null ? View.GONE : View.VISIBLE);
                    }
                }
            }
        };
    }

    private void setZeroPaddingToLayoutChildren(View view) {
        if (!(view instanceof ViewGroup))
            return;
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            setZeroPaddingToLayoutChildren(viewGroup.getChildAt(i));
            viewGroup.setPadding(0, viewGroup.getPaddingTop(), viewGroup.getPaddingRight(), viewGroup.getPaddingBottom());
        }
    }
}
