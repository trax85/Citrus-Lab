package com.example.myapplication3.fragments.AboutFragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication3.MainActivity;
import com.example.myapplication3.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = new Intent();
        MainActivity activity = (MainActivity) getActivity();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        ImageView imageViewGit = view.findViewById(R.id.github);
        ImageView imageViewGmail = view.findViewById(R.id.gmail);
        ImageView imageViewDown = view.findViewById(R.id.downloads);
        imageViewGit.setOnClickListener(v -> {
            Toast.makeText(getActivity(),
                    "Github",
                    Toast.LENGTH_SHORT);
            intent.setData(Uri.parse("https://github.com/trax85"));});
        imageViewGmail.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Gmail",
                    Toast.LENGTH_SHORT);
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("gmail id", "tejasudupa1002@gmail.com");
            clipboard.setPrimaryClip(clip);
        });
        imageViewDown.setOnClickListener(v -> intent.setData(Uri.parse("https://github.com/trax85/Citrus-Control")));
    }
}