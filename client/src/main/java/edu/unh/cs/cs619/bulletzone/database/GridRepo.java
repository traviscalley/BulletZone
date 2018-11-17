package edu.unh.cs.cs619.bulletzone.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

public class GridRepo {

    private GridDao mGridDao;
    private List<GridEntity> mAllGrids;

    public GridRepo(Application application)
    {
        GridDatabase db = GridDatabase.getDatabase(application);
        mGridDao = db.gridDao();
        mAllGrids = mGridDao.getAll();
    }

    public List<GridEntity> getAll(){
        return mAllGrids;
    }

    //access point???
    //public void insert(GridEntity grid) {
        //new insertAsyncTask(mGridDao).execute(grid);
    //}

    //access point???
    public void insert(GridWrapper ge) {
        new insertAsyncTask(mGridDao).execute(ge);
    }

    private static class insertAsyncTask extends AsyncTask<GridWrapper, Void, Void> {

        private GridDao mAsyncTaskDao;

        insertAsyncTask(GridDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final GridWrapper... params) {
            //mAsyncTaskDao.insert(params[0]);
            //maybe put this not in main thread??
            int[][] rawData = params[0].getGrid();
            StringBuilder res = new StringBuilder("[");
            for(int[] i : rawData)
            {
                res.append("[");
                for(int j : i)
                    res.append(Integer.toString(j)).append(",");
                res.append("],");
            }
            res.append("]");
            //this.grid = res.toString();
            Log.d(null, Long.toString(params[0].getTimeStamp()));
            //this.timestamp = gw.getTimeStamp();

            mAsyncTaskDao.insert(new GridEntity(res.toString(), params[0].getTimeStamp()));
            return null;
        }
    }
}
