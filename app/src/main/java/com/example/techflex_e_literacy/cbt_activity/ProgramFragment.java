package com.example.techflex_e_literacy.cbt_activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.techflex_e_literacy.R;

public class ProgramFragment extends Fragment {
    ViewPager viewPager;
    String[] programList;
    ListView listView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_one, container, false);

        viewPager = getActivity().findViewById(R.id.viewPager);
        listView = view.findViewById(R.id.program_list);
         programList = getResources().getStringArray(R.array.Program_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,programList);
        for (String str: programList){
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    viewPager.setCurrentItem(1);
                }
            });

        }
        return view;
    }
}
