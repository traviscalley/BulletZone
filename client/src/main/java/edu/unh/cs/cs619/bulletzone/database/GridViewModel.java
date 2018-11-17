package edu.unh.cs.cs619.bulletzone.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

public class GridViewModel extends AndroidViewModel {
    private GridRepo repo;

    private List<GridEntity> allGrids;

    public GridViewModel(Application application){
        super(application);
        repo = new GridRepo(application);
        //allGrids = repo.getAll();
    }

    public List<GridEntity> getAllGrids() { return allGrids; }

    public void insert(GridWrapper ge) { repo.insert(ge); }
}
