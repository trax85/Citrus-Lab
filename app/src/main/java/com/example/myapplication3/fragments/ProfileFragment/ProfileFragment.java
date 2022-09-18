package com.example.myapplication3.fragments.ProfileFragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication3.R;
import com.example.myapplication3.tools.Utils;
import com.google.android.material.button.MaterialButton;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ProfileFragment extends Fragment {
    final static String TAG = "ProfileFragment";

    MaterialButton power_button, balance_button, perf_button;
    RelativeLayout prof1, prof2, prof3;
    RelativeLayout[] custProfArr;
    ImageView addProf1, addProf2, addProf3, editName1, editName2, editName3;
    ImageView[] addProfArr, editNameArr;
    TextView editText1, editText2, editText3, unsetText1, unsetText2, unsetText3;
    TextView textView1, textView2, textView3;
    TextView[] textViews, editTextViews, unSetTextViews;
    CustomProfile cprofile;
    Intent intent;
    int curIdx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        cprofile = new CustomProfile(this);
        cprofile.initCustomProfiles(requireContext());
        setListeners();
    }

    void initViews(View view){
        power_button = view.findViewById(R.id.button_power);
        balance_button = view.findViewById(R.id.button_balance);
        perf_button = view.findViewById(R.id.button_performance);
        prof1 = view.findViewById(R.id.button_add1);
        prof2 = view.findViewById(R.id.button_add2);
        prof3 = view.findViewById(R.id.button_add3);
        custProfArr = new RelativeLayout[]{prof1, prof2, prof3};

        addProf1 = view.findViewById(R.id.imageViewAdd1);
        addProf2 = view.findViewById(R.id.imageViewAdd2);
        addProf3 = view.findViewById(R.id.imageViewAdd3);
        addProfArr = new ImageView[]{addProf1, addProf2, addProf3};

        textView1 = view.findViewById(R.id.proftext1);
        textView2 = view.findViewById(R.id.proftext2);
        textView3 = view.findViewById(R.id.proftext3);
        textViews = new TextView[]{textView1, textView2, textView3};

        editText1 = view.findViewById(R.id.editbutton1);
        editText2 = view.findViewById(R.id.editbutton2);
        editText3 = view.findViewById(R.id.editbutton3);
        editTextViews = new TextView[]{editText1, editText2, editText3};
        unsetText1 = view.findViewById(R.id.unsetbutton1);
        unsetText2 = view.findViewById(R.id.unsetbutton2);
        unsetText3 = view.findViewById(R.id.unsetbutton3);
        unSetTextViews = new TextView[]{unsetText1, unsetText2, unsetText3};

        editName1 = view.findViewById(R.id.editName1);
        editName2 = view.findViewById(R.id.editName2);
        editName3 = view.findViewById(R.id.editName3);
        editNameArr = new ImageView[]{editName1, editName2, editName3};
    }

    void setListeners(){
        power_button.setOnClickListener(v -> {
            String out = readTextFile(requireContext(), R.raw.power);
            Toast.makeText(getContext(), "Applying Power Profile...",Toast.LENGTH_SHORT).show();
            Utils.execCmdString(out);
        });
        balance_button.setOnClickListener(v -> {
            String out = readTextFile(requireContext(), R.raw.balance);
            Toast.makeText(getContext(), "Applying Balance Profile...",Toast.LENGTH_SHORT).show();
            Utils.execCmdString(out);
        });
        perf_button.setOnClickListener(v -> {
            String out = readTextFile(requireContext(), R.raw.performance);
            Toast.makeText(getContext(), "Applying Performance Profile...",Toast.LENGTH_SHORT).show();
            Utils.execCmdString(out);
        });
    }

    public static String readTextFile(Context context, @RawRes int id){
        InputStream inputStream = context.getResources().openRawResource(id);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            int i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    public void setAddButton(int idx){
        custProfArr[idx].setOnClickListener(v -> showPopUpWindow(idx));
    }

    public void showPopUpWindow(int itemNumber){
        View view = View.inflate(getContext(), R.layout.profile_popup_menu, null);
        Button button_cust, button_cur;
        TextView cancelButton, clearButton;
        button_cust = view.findViewById(R.id.profile_popup_cust);
        button_cur = view.findViewById(R.id.profile_popup_cur);
        cancelButton = view.findViewById(R.id.popup_cancel);
        clearButton = view.findViewById(R.id.popup_clear);
        setDIm();

        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow popupWindow = new PopupWindow(view, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupWindow.setOnDismissListener(this::unsetDim);

        button_cust.setOnClickListener(v -> {
            popupWindow.dismiss();
            getFile(itemNumber);
        });
        button_cur.setOnClickListener(v -> {
            popupWindow.dismiss();
            showCheckBoxDialouge(itemNumber);
        });
        clearButton.setOnClickListener(v -> {
            cprofile.clearItem(itemNumber);
            cprofile.hideView(itemNumber);
            popupWindow.dismiss();
        });
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());
    }

    private void showCheckBoxDialouge(int index)
    {
        ArrayList<String> selectedList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Choose settings to apply");
        builder.setMultiChoiceItems(R.array.profile_array, null, (dialog, which, isChecked) -> {
            String[] items = requireActivity().getResources().getStringArray(R.array.profile_array);
            if(isChecked){
                selectedList.add(items[which]);
            } else selectedList.remove(items[which]);
        });

        builder.setPositiveButton("Ok", (dialog, which) -> setName(index, selectedList));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setDIm(){
        requireView().findViewById(R.id.profile_layout).setAlpha((float) 0.5);
    }
    private void unsetDim(){
        requireView().findViewById(R.id.profile_layout).setAlpha((float) 1.0);
    }

    public void setName(int index, ArrayList<String> selectedList){
        MaterialAlertDialogBuilder builder = new
                MaterialAlertDialogBuilder(requireActivity());
        final EditText weightInput = new EditText(getActivity());

        builder.setTitle("Edit Name");
        weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
        weightInput.setHint(textViews[index].getText().toString());
        builder.setView(weightInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String fileName = weightInput.getText().toString();
            ProfileScriptGenerator scriptGenerator =
                    new ProfileScriptGenerator(this, selectedList, fileName);
            cprofile.profArr[index] = scriptGenerator.makeScript();
            fileName = scriptGenerator.mkFile();
            textViews[index].setText(fileName);
            cprofile.nameArr[index] = fileName;
            cprofile.setItem(index);
            cprofile.hideAddView(index);
        });builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void getFile(int idx){
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse("/Download"); // a directory
        intent.setDataAndType(uri, "*/*");
        curIdx = idx;
        getFileContentActivity.launch(intent);
    }

    ActivityResultLauncher<Intent> getFileContentActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent intent = result.getData();
                Uri uri;
                try {
                    uri = intent.getData();
                }catch (NullPointerException e){
                    return;
                }
                File file = new File(uri.getPath());
                String name = file.getName();
                if(!isValidFile(name)){
                    Toast.makeText(getContext(),"Invalid file",Toast.LENGTH_SHORT).show();
                    return;
                }

                InputStream inputStream;
                String content;
                try {
                    AssetFileDescriptor fileDescriptor = requireContext().getContentResolver()
                            .openAssetFileDescriptor(uri, "r");
                    long fileSize = fileDescriptor.getLength();
                    if(fileSize > 10000){
                        Toast.makeText(getContext(),"File too big",Toast.LENGTH_LONG).show();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    inputStream = requireActivity().getContentResolver().openInputStream(uri);
                    content = new BufferedReader(new InputStreamReader(inputStream))
                            .lines().collect(Collectors.joining("\n"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }

                cprofile.profArr[curIdx] = content;
                cprofile.nameArr[curIdx] = getFileName(file.getName());
                cprofile.setItem(curIdx);
                cprofile.hideAddView(curIdx);
            });

    public boolean isValidFile(String file){
        return file.contains(".sh");
    }

    public String getFileName(String name){
        if(name.contains("primary"))
            name = name.split(":")[1];
        name = name.split("\\.")[0];
        return name;
    }
}