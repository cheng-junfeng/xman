package com.xman.app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xman.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    Unbinder unbinder;

    protected abstract int setContentView();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View containView = LayoutInflater.from(this.getActivity()).inflate(setContentView(), null);
        unbinder = ButterKnife.bind(this, containView);
        return containView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    protected void readGo(Class<?> cls){
        readGo(cls, null);
    }

    protected void readGo(Class<?> cls, Bundle bundle){
        Intent inten = new Intent(getActivity(), cls);
        if(bundle != null){
            inten.putExtras(bundle);
        }
        startActivity(inten);
    }
}
