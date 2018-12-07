package edu.unh.cs.cs619.bulletzone.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import org.androidannotations.annotations.Background;

import java.util.List;
import java.util.function.Function;

import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

public class GridRepo {

    private GridDao mGridDao;
    private List<GridEntity> mAllGrids;

    public GridRepo(Application application)
    {
        GridDatabase db = GridDatabase.getDatabase(application);
        mGridDao = db.gridDao();
        //mAllGrids = mGridDao.getAll();
    }

    /*public void startGetAll(Function<List<GridEntity>, Void> callback){

        new getAllTask(mGridDao).execute(callback);///mGridDao.getAll();//mAllGrids;
    }*/

    public List<GridEntity> getAll(){
        return mGridDao.getAll();
    }

    public void deleteAll(){
        mGridDao.deleteAll();
    }
    //access point???
    //public void insert(GridEntity grid) {
        //new insertAsyncTask(mGridDao).execute(grid);
    //}

    //access point???
    public void insert(GridWrapper ge) {
        new insertAsyncTask(mGridDao).execute(ge);
    }

    /*private static class getAllTask extends AsyncTask<Function<List<GridEntity>, Void>, Void, Void>{
        private GridDao mAsyncTaskDao;

        getAllTask(GridDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Function... functions) {
            functions[0].apply(mAsyncTaskDao.getAll());
            return null;
        }

        /*@Override
        protected List<GridEntity> doInBackground(Void... voids) {
            return mAsyncTaskDao.getAll();
        }
    }*/

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
            //Log.d(null, Long.toString(params[0].getTimeStamp()));
            //this.timestamp = gw.getTimeStamp();

            mAsyncTaskDao.insert(new GridEntity(res.toString(), params[0].getTimeStamp()));
            return null;
        }
    }
}
