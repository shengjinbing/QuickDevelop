package am.server.android.com.database.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

/**
 * Created by ${lixiang} on 2018/6/27.
 */

public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;

    private LiveData<List<User>> mAllWords;

    public UserViewModel (Application application) {
        super(application);
        mRepository = new UserRepository(application);
        mAllWords = mRepository.getAllWords();
    }

    public LiveData<List<User>> getAllWords() { return mAllWords; }

    /**
     * 增
     * @param user
     */
    public void insert(User user) { mRepository.insert(user); }

    public void delete(User user){mRepository.detele(user);}

    public void update(User user){mRepository.update(user);};

}
