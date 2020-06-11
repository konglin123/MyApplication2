package com.example.myapplication.ui.find;

import com.example.libnavannotation.FragmentDestination;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FindViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FindViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is find fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}