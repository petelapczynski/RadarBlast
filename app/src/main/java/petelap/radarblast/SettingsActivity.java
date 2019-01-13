package petelap.radarblast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static Context settingsContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingsContext = this;
        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

        // Reset Preference Button

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SceneManager.changeScene("MENU");
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            //bindPreferenceSummaryToValue(findPreference("username"));
            bindPreferenceSummaryToValue(findPreference("background"));
            //bindPreferenceSummaryToValue(findPreference("gradient"));
            //bindPreferenceSummaryToValue(findPreference("music"));
            //bindPreferenceSummaryToValue(findPreference("sound"));
            bindPreferenceSummaryToValue(findPreference("particles"));
            bindPreferenceSummaryToValue(findPreference("color_Obj"));
            bindPreferenceSummaryToValue(findPreference("color_Square"));
            bindPreferenceSummaryToValue(findPreference("color_Rectangle"));
            bindPreferenceSummaryToValue(findPreference("color_Circle"));
            bindPreferenceSummaryToValue(findPreference("color_Triangle"));
            bindPreferenceSummaryToValue(findPreference("color_Rhombus"));
            bindPreferenceSummaryToValue(findPreference("color_Hexagon"));

            setPreferenceClickListener(findPreference("restoreDefaultSettings"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            //startActivity(new Intent(getActivity(), SettingsActivity.class));
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(),""));
    }

    private static void setPreferenceClickListener(Preference preference) {
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(settingsContext);
                builder.setTitle("Reset Settings");
                builder.setMessage("Do you want to reset all settings to the default values?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(settingsContext);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        PreferenceManager.setDefaultValues(settingsContext, R.xml.pref_main,true);
                        Toast.makeText(settingsContext,"Settings Reset",Toast.LENGTH_LONG).show();
                        dialogInterface.cancel();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
        });
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof EditTextPreference) {
                if (preference.getKey().equals("key_gallery_name")) {
                    // update the changed gallery name to summary filed
                    preference.setSummary(stringValue);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
}