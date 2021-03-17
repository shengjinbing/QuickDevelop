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
 * 1.Insert(插入)
 *   当DAO里面的某个方法添加了@Insert注解。Room会生成一个实现，将所有参数插入到数据库中的一个单个事务。
 *        @Delete和@Update和@Insert注解可以设置一个属性：
 * onConflict：默认值是OnConflictStrategy.ABORT，表示当插入有冲突的时候的处理策略。OnConflictStrategy封装了Room解决冲突的相关策略：
 *        1. OnConflictStrategy.REPLACE：冲突策略是取代旧数据同时继续事务。
 *        2. OnConflictStrategy.ROLLBACK：冲突策略是回滚事务。
 *        3. OnConflictStrategy.ABORT：冲突策略是终止事务。
 *        4. OnConflictStrategy.FAIL：冲突策略是事务失败。
 *        5. OnConflictStrategy.IGNORE：冲突策略是忽略冲突。
 *
 * 2.Query(查询)
 *      @Query注解是DAO类中使用的主要注释。它允许您对数据库执行读/写操作。@Query在编译的时候会验证准确性，所以如果查询出现问题在编译的时候就会报错。
 *        Room还会验证查询的返回值，如果返回对象中的字段名称与查询响应中的相应列名称不匹配的时候，Room会通过以下两种方式之一提醒您：
 * 如果只有一些字段名称匹配，它会发出警告。
 * 如果没有字段名称匹配，它会发生错误。
 *     @Query注解value参数：查询语句，这也是我们查询操作最关键的部分。
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
