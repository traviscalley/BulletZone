package edu.unh.cs.cs619.bulletzone.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class GridRepo {

    private GridDao mGridDao;
    private LiveData<List<GridEntity>> mAllGrids;

    public GridRepo(Application application)
    {
        GridDatabase db = GridDatabase.getDatabase(application);
        mGridDao = db.gridDao();
        mAllGrids = mGridDao.getAll();
    }

    public LiveData<List<GridEntity>> getAll(){
        return mAllGrids;
    }

    //access point???
    public void insert(GridEntity grid) {
        new insertAsyncTask(mGridDao).execute(grid);
    }

    private static class insertAsyncTask extends AsyncTask<GridEntity, Void, Void> {

        private GridDao mAsyncTaskDao;

        insertAsyncTask(GridDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final GridEntity... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
