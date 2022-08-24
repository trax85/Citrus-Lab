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
    private static final String[] gitLinks= {"https://github.com/trax85",
            "https://github.com/Skyking469", "https://github.com/prathameshmm02",
            "https://github.com/sharan9678"};
    private static final String[] teleLinks = {"https://t.me/Skyking469",
            "https://t.me/QuickerSilver", "https://t.me/sharanrajt" };
    ImageView imageViewGit, imageViewTele1, imageViewGit1, imageViewTele2, imageViewGit2,
            imageViewGit3, imageViewTele3;
    Intent intent;
    private static ImageView[] imageViewsGit;
    private static ImageView[] imageViewsTel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AsyncInitTask initTask = new AsyncInitTask(view);
        initTask.start();
    }

    public void initViews(View view){
        imageViewGit = view.findViewById(R.id.github);
        imageViewGit1 = view.findViewById(R.id.akash_git);
        imageViewTele1 = view.findViewById(R.id.akash_tele);
        imageViewGit2 = view.findViewById(R.id.prath_git);
        imageViewTele2 = view.findViewById(R.id.prath_tele);
        imageViewGit3 = view.findViewById(R.id.sharan_git);
        imageViewTele3 = view.findViewById(R.id.sharan_tele);
        imageViewsGit = new ImageView[]{imageViewGit, imageViewGit1, imageViewGit2, imageViewGit3};
        imageViewsTel = new ImageView[]{imageViewTele1, imageViewTele2, imageViewTele3};
    }

    public void setGitListeners(){
        for(int i = 0; i < imageViewsGit.length; i++){
            int finalI = i;
            imageViewsGit[i].setOnClickListener(v -> {
                intent.setData(Uri.parse(gitLinks[finalI]));
                startActivity(intent);
            });
        }
    }

    public void setTelListeners(){
        for(int i = 0; i < imageViewsTel.length; i++){
            int finalI = i;
            imageViewsTel[i].setOnClickListener(v -> {
                intent.setData(Uri.parse(teleLinks[finalI]));
                startActivity(intent);
            });
        }
    }

    class AsyncInitTask extends Thread {
        View view;
        public AsyncInitTask(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            initViews(view);
            setGitListeners();
            setTelListeners();
        }
    }

}