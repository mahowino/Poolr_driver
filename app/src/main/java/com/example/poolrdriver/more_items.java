package com.example.poolrdriver;


import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.userRegistrationJourney.LogInScreen;
import com.example.poolrdriver.util.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;

public class more_items extends Fragment implements View.OnClickListener {
    CardView schedule;
    CardView logOut;
    CardView DriverSettings;
    CardView myRequests;
    Button showProfile;
    TextView name;
    ImageView profilePic;
    FirebaseAuth auth;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_more_items, container, false);
        initializeVariables(view);
        setListeners();
        setProfile();
        return view;
    }

    private void setProfile() {
        name.setText((user.getName()));
        if(user.getProfilePic()!=null)Glide.with(this).load(user.getProfilePic()).into(profilePic);
    }

    private void setListeners() {
        schedule.setOnClickListener(this);
        logOut.setOnClickListener(this);
        showProfile.setOnClickListener(v -> redirectActivity(getActivity(), MyProfile.class));

    }

    private void initializeVariables(View view) {

        //initializations
        showProfile=(Button)view.findViewById(R.id.buttonShowProfile);
        schedule=view.findViewById(R.id.card_calendar_schedule);
        logOut=view.findViewById(R.id.card_log_out);
        DriverSettings=view.findViewById(R.id.card_driver_section);
        myRequests=view.findViewById(R.id.card_my_requests);
        name=(TextView) view.findViewById(R.id.name_more);
        profilePic=(ImageView) view.findViewById(R.id.profile_pic_account);

        auth=FirebaseAuth.getInstance();
        user=new User();
    }

    @Override
    public void onClick(View v) {
        LoadingDialog dialog=new LoadingDialog(getActivity());
        dialog.startLoadingAlertDialog();

        int id = v.getId();

        if (id == R.id.card_calendar_schedule) {redirectActivity(getActivity(), wallet.class);dialog.dismissDialog();

        } else if (id == R.id.card_log_out) {
            new AlertDialog.Builder(getContext())
                    .setTitle("log out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton(android.R.string.yes, (dialog1, which) -> {user.signOut();redirectActivity(getActivity(), LogInScreen.class);
                        requireActivity().finish();dialog1.dismiss();
                    })
                    .setNegativeButton(android.R.string.no, (dialog12, which) -> dialog12.dismiss())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            dialog.dismissDialog();

        } else if (id == R.id.card_my_requests) {//todo:set up requests page
            dialog.dismissDialog();
        }

    }
}