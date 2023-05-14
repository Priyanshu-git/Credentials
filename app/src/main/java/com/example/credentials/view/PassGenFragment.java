package com.example.credentials.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.credentials.R;
import com.example.credentials.util.PasswordGen;

public class PassGenFragment extends Fragment {
    private View root;
    private CheckBox sm, ca, sy, d;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_pass_gen, container, false);

        EditText output = root.findViewById(R.id.textOutput);
        Button generate = root.findViewById(R.id.btn_generate);
        ca = root.findViewById(R.id.cb_caps);
        sm = root.findViewById(R.id.cb_small);
        d = root.findViewById(R.id.cb_nums);
        sy = root.findViewById(R.id.cb_symbols);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean cb1, cb2, cb3, cb4;
                EditText input_length = root.findViewById(R.id.pwd_length);

                int l;
                String s = input_length.getText().toString();
                if (s.length() < 1) {
                    Toast.makeText(getContext(), "Enter length of password", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    l =Integer.parseInt(s);
                    if (l<6){
                        Toast.makeText(getContext(), "Length must be >= 6", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                cb1 = ca.isChecked();
                cb2 = sm.isChecked();
                cb3 = d.isChecked();
                cb4 = sy.isChecked();

                if (!cb1 & !cb2 & !cb3 & !cb4) {
                    Toast.makeText(getContext(), "Select at least 1 option", Toast.LENGTH_LONG).show();
                } else {
                    String pwd = PasswordGen.generate(l, cb1, cb2, cb3, cb4);
                    output.setText(pwd);
                }
            }
        });

        Button copy = root.findViewById(R.id.btn_copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = output.getText().toString();
                if (str.length() > 0) {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Password", str);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }
}