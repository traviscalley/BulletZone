package edu.unh.cs.cs619.bulletzone.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.api.BackgroundExecutor;

import java.util.List;
import java.util.function.Function;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EBean
public class GridRepo {

    private GridDao mGridDao;
    private List<GridEntity> mAllGrids;

    @Bean
    BusProvider busProvider;

    public GridRepo(Context application)
    {
        GridDatabase db = GridDatabase.getDatabase(application);
        mGridDao = db.gridDao();
    }

    @AfterInject
    void afterInject() {
        busProvider.getEventBus().register(gridEventHandler);
    }


    private Object gridEventHandler = new Object() {
        @Subscribe
        public void onUpdateGrid(GridUpdateEvent event) {
            Log.d("fuck you", "lol");
            insert(event.gw);
        }
    };

    /*public void startGetAll(Function<List<GridEntity>, Void> callback){

        new getAllTask(mGridDao).execute(callback);///mGridDao.getAll();//mAllGrids;
    }*/

    public List<GridEntity> getAll(){
        return mGridDao.getAll();
    }

    public void deleteAll(){

        BackgroundExecutor.execute(new BackgroundExecutor.Task("dbdelete", 0L, "") {
            @Override
            public void execute() {
                mGridDao.deleteAll();
            }
        });
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
