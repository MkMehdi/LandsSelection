package com.sample.lands.mapbox.ui.lands;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sample.lands.R;
import com.sample.lands.mapbox.db.model.Land;

import java.util.List;

public class LandListFragment extends Fragment implements LandsAdapter.OnClickListener {

    private LandsAdapter adapter;
    private LandListViewModel mViewModel;

    View parentView;
    RecyclerView recyclerView;
    FloatingActionButton addBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        parentView = inflater.inflate(R.layout.land_list_fragment, container, false);
         recyclerView = parentView.findViewById(R.id.recyclerList);
        addBtn = parentView.findViewById(R.id.add_button);

        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LandListViewModel.class);

        mViewModel.getLands().observe(getActivity(),lands -> {
            setupRecycler(lands);
        });

        addBtn.setOnClickListener(view -> {
            Navigation.findNavController(parentView).navigate(R.id.action_landsFragment_to_JMapFragment);
        });
    }

    private void setupRecycler(List<Land> lands){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new LandsAdapter(lands,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(Land land) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("land", land);
        Navigation.findNavController(parentView).navigate(R.id.action_landsFragment_to_JMapFragment,bundle);
    }
}