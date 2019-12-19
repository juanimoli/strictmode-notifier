package com.juanimoli.strictmodenotifier;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.juanimoli.strictmodenotifier.commons.StrictModeViolation;

import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class StrictModeReportActivity extends Activity {

    private static final String EXTRA_REPORT = "EXTRA_REPORT";

    private ReportAdapter adapter;
    private ViolationStore violationStore;

    public static Intent createIntent(Context context, StrictModeViolation report) {
        Intent intent = new Intent(context, StrictModeReportActivity.class);
        intent.putExtra(EXTRA_REPORT, report);
        return intent;
    }

    public static PendingIntent createPendingIntent(Context context, StrictModeViolation report) {
        Intent intent = StrictModeReportActivity.createIntent(context, report);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 1, intent, FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strictmode_notifier_activity_report);

        ReportActivityUtils.setTitle(this,
                getString(R.string.strictmode_notifier_title, getPackageName()));

        adapter = new ReportAdapter(this);
        ListView listView = findViewById(R.id.__list_view);
        listView.setAdapter(adapter);

        violationStore = new ViolationStore(this);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            StrictModeViolation report = adapter.getItem(position);
            StrictModeReportDetailActivity.start(StrictModeReportActivity.this, report);
        });

        ToggleButton toggleButton = findViewById(R.id.__enable_button);
        toggleButton.setChecked(StringModeConfig.from(this).isEnabled());
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> StringModeConfig.from(StrictModeReportActivity.this).toggle());

        findViewById(R.id.__delete_button).setOnClickListener(v -> {
            violationStore.clear();
            adapter.clear();
            adapter.notifyDataSetChanged();
        });

        if (savedInstanceState == null) {
            StrictModeViolation report = (StrictModeViolation) getIntent().getSerializableExtra(EXTRA_REPORT);
            if (report != null) {
                StrictModeReportDetailActivity.start(this, report);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<StrictModeViolation> reports = violationStore.getAll();
        adapter.clear();
        adapter.addAll(reports);
        adapter.notifyDataSetChanged();
    }
}