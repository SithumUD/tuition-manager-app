package com.example.tutionmanager.Fragments.AdminUser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.tutionmanager.R;

public class ReportsFragment extends Fragment {

    private RadioGroup reportTypeRadioGroup;
    private LinearLayout classGradeContainer, groupByContainer;
    private LinearLayout subjectContainer, assessmentContainer;
    private TextView classGradeLabel, groupByLabel;
    private TextView subjectLabel, assessmentLabel;
    private TextView reportPlaceholder;
    private LinearLayout attendancePreview;
    private Button generateButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        // Initialize views
        reportTypeRadioGroup = view.findViewById(R.id.reportTypeRadioGroup);
        classGradeContainer = view.findViewById(R.id.classGradeContainer);
        groupByContainer = view.findViewById(R.id.groupByContainer);
        subjectContainer = view.findViewById(R.id.subjectContainer);
        assessmentContainer = view.findViewById(R.id.assessmentContainer);
        classGradeLabel = view.findViewById(R.id.classGradeLabel);
        groupByLabel = view.findViewById(R.id.groupByLabel);
        subjectLabel = view.findViewById(R.id.subjectLabel);
        assessmentLabel = view.findViewById(R.id.assessmentLabel);
        reportPlaceholder = view.findViewById(R.id.reportPlaceholder);
        attendancePreview = view.findViewById(R.id.attendancePreview);
        generateButton = view.findViewById(R.id.generateButton);

        // Set up radio group listener
        reportTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Reset all views
            classGradeContainer.setVisibility(View.GONE);
            groupByContainer.setVisibility(View.GONE);
            subjectContainer.setVisibility(View.GONE);
            assessmentContainer.setVisibility(View.GONE);
            classGradeLabel.setVisibility(View.GONE);
            groupByLabel.setVisibility(View.GONE);
            subjectLabel.setVisibility(View.GONE);
            assessmentLabel.setVisibility(View.GONE);
            reportPlaceholder.setVisibility(View.GONE);
            attendancePreview.setVisibility(View.GONE);

            if (checkedId == R.id.attendanceReport) {
                classGradeContainer.setVisibility(View.VISIBLE);
                groupByContainer.setVisibility(View.VISIBLE);
                classGradeLabel.setVisibility(View.VISIBLE);
                groupByLabel.setVisibility(View.VISIBLE);
                reportPlaceholder.setVisibility(View.GONE);
            } else if (checkedId == R.id.academicPerformance) {
                subjectContainer.setVisibility(View.VISIBLE);
                assessmentContainer.setVisibility(View.VISIBLE);
                subjectLabel.setVisibility(View.VISIBLE);
                assessmentLabel.setVisibility(View.VISIBLE);
                reportPlaceholder.setVisibility(View.VISIBLE);
                reportPlaceholder.setText("Generate an academic performance report to view results");
            } else if (checkedId == R.id.enrollmentStats) {
                reportPlaceholder.setVisibility(View.VISIBLE);
                reportPlaceholder.setText("Generate an enrollment statistics report to view results");
            } else if (checkedId == R.id.scheduleAnalysis) {
                reportPlaceholder.setVisibility(View.VISIBLE);
                reportPlaceholder.setText("Generate a schedule analysis report to view results");
            } else if (checkedId == R.id.resourcesUsage) {
                reportPlaceholder.setVisibility(View.VISIBLE);
                reportPlaceholder.setText("Generate a resources usage report to view results");
            }
        });

        // Generate button click
        generateButton.setOnClickListener(v -> {
            int checkedId = reportTypeRadioGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.attendanceReport) {
                attendancePreview.setVisibility(View.VISIBLE);
                reportPlaceholder.setVisibility(View.GONE);
            } else {
                reportPlaceholder.setVisibility(View.VISIBLE);
                attendancePreview.setVisibility(View.GONE);
            }
        });

        return view;
    }
}