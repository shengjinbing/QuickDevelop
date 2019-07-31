package am.server.android.com.database.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by ${lixiang} on 2018/6/26.
 */
@Dao
public interface UserDao {
    @Query("SELECT * FROM test_table")
    LiveData<List<User>> getAll();

    @Query("SELECT * FROM test_table WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM test_table WHERE first_name LIKE :first AND "
            + "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);

    @Query("DELETE  FROM test_table")
    void deleteAll();

    /**
     * 使用replace策略插入数据
     *
     * @param users
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);

    @Delete
    void delete(User... user);

    /**
     * 根据主键匹配更新数据
     *
     * @param user
     */
    @Update
    void update(User... user);
}
