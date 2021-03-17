package am.server.android.com.database.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by ${lixiang} on 2018/6/26.
 * 1.个Entity代表数据库中某个表的实体类。默认情况下Room会把Entity里面所有的字段对应到表上的每一列。如果需要制定某个字段
 * 不作为表中的一列需要添加@Ignore注解。
 * 2.tableName：设置表名字。默认是类的名字。
 * indices：设置索引。
 * inheritSuperIndices：父类的索引是否会自动被当前类继承。
 * primaryKeys：设置主键。
 * foreignKeys：设置外键。
 * @ColumnInfo 默认情况下Entity类中字段的名字就是表中列的名字。我们也是可以通过@ColumnInfo注解来自定义表中列的名字
 * 3.设置复合主键
 * @Entity(primaryKeys = {"firstName",
 *                        "lastName"})
 * 4.设置索引： (1)数据库索引用于提高数据库表的数据访问速度的。数据库里面的索引有单列索引和组合索引。Room里面可以通过@Entity的
 *     indices属性来给表格添加索引。
 *            (2)索引也是分两种的唯一索引和非唯一索引。唯一索引就想主键一样重复会报错的。可以通过的@Index的unique数学来设置是否唯一索引。
 * @Entity(indices = {@Index("firstName"),
 *         @Index(value = {"last_name", "address"},unique = true)})
 */

@Entity(tableName = "test_table")
public class User {
    @PrimaryKey
    @NonNull
    private int uid;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @NonNull
    public int getUid() {
        return uid;
    }

    public void setUid(@NonNull int uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
