/*
 * Copyright (C) 2022 Project Lighthouse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lighthouse.settings.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import android.content.Context;
import android.provider.Settings;
import com.android.internal.util.lighthouse.udfps.UdfpsUtils;

import com.android.internal.util.lighthouse.udfps.UdfpsUtils;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import android.hardware.fingerprint.FingerprintManager;

import com.lighthouse.settings.preference.SystemSettingSwitchPreference;

public class LockscreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String FINGERPRINT_SUCCESS_VIB = "fingerprint_success_vib";
    private static final String FINGERPRINT_ERROR_VIB = "fingerprint_error_vib";
    private static final String UDFPS_CATEGORY = "udfps_category";


    private FingerprintManager mFingerprintManager;
    private SystemSettingSwitchPreference mFingerprintSuccessVib;
    private SystemSettingSwitchPreference mFingerprintErrorVib;
    private PreferenceCategory mUdfpsCategory;

    private static final String TAG = "LockscreenSettings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lighthouse_settings_lockscreen);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();
        final PackageManager mPm = getActivity().getPackageManager();
        final PreferenceCategory fpCategory = (PreferenceCategory)
                findPreference("lockscreen_fingerprint_category");

        mFingerprintManager = (FingerprintManager)
                getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintSuccessVib = findPreference(FINGERPRINT_SUCCESS_VIB);
        mFingerprintErrorVib = findPreference(FINGERPRINT_ERROR_VIB);

        if (mPm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) &&
                 mFingerprintManager != null) {
            if (!mFingerprintManager.isHardwareDetected()){
                prefSet.removePreference(fpCategory);
            } 
            // else {
            //     mFingerprintSuccessVib.setChecked((Settings.System.getInt(getContentResolver(),
            //             Settings.System.FP_SUCCESS_VIBRATE, 1) == 1));
            //     mFingerprintSuccessVib.setOnPreferenceChangeListener(this);
            //     mFingerprintErrorVib.setChecked((Settings.System.getInt(getContentResolver(),
            //             Settings.System.FP_ERROR_VIBRATE, 1) == 1));
            //     mFingerprintErrorVib.setOnPreferenceChangeListener(this);
            //     if (UdfpsUtils.hasUdfpsSupport(getActivity())) {
            //         mUdfpsHapticFeedback.setChecked((Settings.System.getInt(getContentResolver(),
            //                 Settings.System.UDFPS_HAPTIC_FEEDBACK, 1) == 1));
            //         mUdfpsHapticFeedback.setOnPreferenceChangeListener(this);
            //     } else {
            //         fpCategory.removePreference(mUdfpsHapticFeedback);
            //     }
            // }
        } else {
            prefSet.removePreference(fpCategory);
        }
    
        mUdfpsCategory = findPreference(UDFPS_CATEGORY);
        if (!UdfpsUtils.hasUdfpsSupport(getContext())) {
            prefSet.removePreference(mUdfpsCategory);
        }

    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SUPPLIES;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mFingerprintSuccessVib) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FP_SUCCESS_VIBRATE, value ? 1 : 0);
            return true;
        } else if (preference == mFingerprintErrorVib) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FP_ERROR_VIBRATE, value ? 1 : 0);
            return true;
        }
        return false;
    }

}