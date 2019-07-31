package am.server.android.com.database.room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by ${lixiang} on 2018/6/27.
 */

public class UserRepository {
    private UserDao mWordDao;
    private LiveData<List<User>> mAllWords;
    private static int type = 0;

    UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mWordDao = db.userDao();
        mAllWords = mWordDao.getAll();
    }

    LiveData<List<User>> getAllWords() {
        return mAllWords;
    }


    public void insert(User user) {
        type = TaskType.INSERT;
        new insertAsyncTask(mWordDao).execute(user);
    }

    public void detele(User user){
        type = TaskType.DELETE;
        new insertAsyncTask(mWordDao).execute(user);
    }

    public void update(User user){
        type = TaskType.UPDATA;
        new insertAsyncTask(mWordDao).execute(user);
    }

    private static class insertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        insertAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            if (type == TaskType.INSERT){
                mAsyncTaskDao.insertAll(params);
            }else if (type == TaskType.DELETE){
                mAsyncTaskDao.delete(params);
            }else if (type == TaskType.UPDATA){
                mAsyncTaskDao.update(params);
            }
            return null;
        }
    }
}
