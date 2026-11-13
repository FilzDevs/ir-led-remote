package de.filzdevs.irledremote;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Main remote control UI for NEC IR RGB LED strips.
 */
public class MainActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String PREFS_NAME = "ir_led_remote_prefs";
    private static final String KEY_PROFILE_ID = "selected_profile";

    private NecIrEncoder irEncoder;
    private List<RemoteProfiles.Profile> profiles;
    private RemoteProfiles.Profile activeProfile;

    private GridLayout buttonGrid;
    private TextView lastSentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        irEncoder = new NecIrEncoder(this);
        profiles = RemoteProfiles.getProfiles();
        activeProfile = loadSavedProfile();

        buildUi();
        renderRemoteGrid();
    }

    private RemoteProfiles.Profile loadSavedProfile() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedId = prefs.getString(KEY_PROFILE_ID, profiles.get(0).id);
        for (RemoteProfiles.Profile p : profiles) {
            if (p.id.equals(savedId)) {
                return p;
            }
        }
        return profiles.get(0);
    }

    private void saveProfile(RemoteProfiles.Profile profile) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_PROFILE_ID, profile.id)
                .apply();
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF121216);
        int pad = dp(16);
        root.setPadding(pad, pad, pad, pad);

        // Header
        TextView title = new TextView(this);
        title.setText("IR LED Fernbedienung");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(title);

        // IR Hardware Status
        boolean hasIr = irEncoder.hasIrEmitter();
        TextView irStatus = new TextView(this);
        irStatus.setText(hasIr
                ? "● IR-Blaster bereit (38 kHz NEC)"
                : "● Kein IR-Blaster erkannt – Gerät hat keinen Infrarot-Sender");
        irStatus.setTextColor(hasIr ? 0xFF66BB6A : 0xFFFFB74D);
        irStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        LinearLayout.LayoutParams statusLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        statusLp.topMargin = dp(4);
        statusLp.bottomMargin = dp(12);
        root.addView(irStatus, statusLp);

        // Profile selector container
        LinearLayout selectorCard = new LinearLayout(this);
        selectorCard.setOrientation(LinearLayout.VERTICAL);
        selectorCard.setPadding(dp(12), dp(8), dp(12), dp(8));
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(0xFF1E2028);
        cardBg.setCornerRadius(dp(12));
        selectorCard.setBackground(cardBg);

        TextView profileLabel = new TextView(this);
        profileLabel.setText("Fernbedienungs-Profil");
        profileLabel.setTextColor(0xFF9EA4B8);
        profileLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        selectorCard.addView(profileLabel);

        List<String> profileNames = new ArrayList<>();
        int activeIndex = 0;
        for (int i = 0; i < profiles.size(); i++) {
            profileNames.add(profiles.get(i).name);
            if (profiles.get(i).id.equals(activeProfile.id)) {
                activeIndex = i;
            }
        }

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, profileNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(activeIndex);
        spinner.setOnItemSelectedListener(this);
        selectorCard.addView(spinner);

        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLp.bottomMargin = dp(12);
        root.addView(selectorCard, cardLp);

        // Scrollable 4-column button grid
        ScrollView scrollView = new ScrollView(this);
        scrollView.setVerticalScrollBarEnabled(false);

        buttonGrid = new GridLayout(this);
        buttonGrid.setColumnCount(4);
        scrollView.addView(buttonGrid, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        root.addView(scrollView, scrollLp);

        // Footer status
        lastSentText = new TextView(this);
        lastSentText.setText("Tippe auf eine Taste, um das IR-Signal zu senden.");
        lastSentText.setTextColor(0xFF8E95AA);
        lastSentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        lastSentText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footerLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        footerLp.topMargin = dp(10);
        root.addView(lastSentText, footerLp);

        setContentView(root);
    }

    private void renderRemoteGrid() {
        buttonGrid.removeAllViews();
        int margin = dp(5);
        int btnHeight = activeProfile.buttons.size() > 24 ? dp(48) : dp(60);

        for (final RemoteProfiles.RemoteButton item : activeProfile.buttons) {
            Button btn = new Button(this);
            btn.setText(item.label);
            btn.setTextColor(item.textColor);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.label.length() > 4 ? 11 : 13);
            btn.setTypeface(Typeface.DEFAULT_BOLD);
            btn.setAllCaps(false);
            btn.setPadding(dp(4), dp(4), dp(4), dp(4));

            GradientDrawable shape = new GradientDrawable();
            shape.setColor(item.bgColor);
            shape.setCornerRadius(dp(14));
            shape.setStroke(dp(1), 0x22FFFFFF);

            RippleDrawable ripple = new RippleDrawable(
                    ColorStateList.valueOf(0x44FFFFFF), shape, null);
            btn.setBackground(ripple);

            btn.setTag(item);
            btn.setOnClickListener(this);

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = btnHeight;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(margin, margin, margin, margin);
            buttonGrid.addView(btn, lp);
        }
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(0xFFFFFFFF);
        }
        RemoteProfiles.Profile selected = profiles.get(pos);
        if (!selected.id.equals(activeProfile.id)) {
            activeProfile = selected;
            saveProfile(selected);
            renderRemoteGrid();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (!(tag instanceof RemoteProfiles.RemoteButton)) {
            return;
        }
        RemoteProfiles.RemoteButton item = (RemoteProfiles.RemoteButton) tag;
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        boolean sent = irEncoder.sendNec(activeProfile.address16, item.command);
        String name = item.label.isEmpty() ? "Farbe" : item.label;
        if (sent) {
            lastSentText.setText(String.format(Locale.US,
                    "Gesendet: %s (Addr 0x%04X · Cmd 0x%02X)",
                    name, activeProfile.address16, item.command));
        } else {
            lastSentText.setText(String.format(Locale.US,
                    "Kein IR-Sender! (Code wäre: 0x%04X / 0x%02X)",
                    activeProfile.address16, item.command));
        }
    }
}
