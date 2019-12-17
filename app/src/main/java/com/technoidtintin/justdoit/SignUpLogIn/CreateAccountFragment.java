package com.technoidtintin.justdoit.SignUpLogIn;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.databinding.FragmentCreateAccountBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountFragment extends Fragment {


    FragmentCreateAccountBinding createAccountBinding;

    public CreateAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initializing DataBinding class
        createAccountBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_create_account,container,false);


        return createAccountBinding.getRoot();
    }

}
