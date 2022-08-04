package com.example.myapplication3.fragments.AboutFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.myapplication3.R;

public class AboutFragment extends Fragment {
    private final String TAG = "AboutFragment";
    ImageView imageViewGit, imageViewTele1, imageViewGit1, imageViewTele2, imageViewGit2,
            imageViewGit3, imageViewTele3;
    Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        initViews(view);
        setUpListeners();
    }

    public void initViews(View view){
        imageViewGit = view.findViewById(R.id.github);
        imageViewGit1 = view.findViewById(R.id.akash_git);
        imageViewTele1 = view.findViewById(R.id.akash_tele);
        imageViewGit2 = view.findViewById(R.id.prath_git);
        imageViewTele2 = view.findViewById(R.id.prath_tele);
        imageViewGit3 = view.findViewById(R.id.sharan_git);
        imageViewTele3 = view.findViewById(R.id.sharan_tele);
    }

    public void setUpListeners(){
        imageViewGit.setOnClickListener(v -> {
            intent.setData(Uri.parse("https://github.com/trax85"));
            startActivity(intent);
        });
        imageViewGit1.setOnClickListener(v -> {
            intent.setData(Uri.parse("https://github.com/Skyking469"));
            startActivity(intent);
        });
        imageViewGit2.setOnClickListener(v -> {
            intent.setData(Uri.parse("https://github.com/prathameshmm02"));
            startActivity(intent);
        });
        imageViewTele1.setOnClickListener(v -> {
            intent.setData(Uri.parse("https://t.me/Skyking469"));
            startActivity(intent);
        });
        imageViewTele2.setOnClickListener(v -> {
            intent.setData(Uri.parse("https://t.me/QuickerSilver"));
            startActivity(intent);
        });
        imageViewGit3.setOnClickListener(v -> {
            intent.setData(Uri.parse("https://github.com/sharan9678"));
            startActivity(intent);
        });
        imageViewTele3.setOnClickListener(v -> {
            intent.setData(Uri.parse("https://t.me/sharanrajt"));
            startActivity(intent);
        });
    }
}