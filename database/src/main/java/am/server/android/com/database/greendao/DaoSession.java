package am.server.android.com.database.greendao;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

import am.server.android.com.database.greendao.utils.StudentBean;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig studentBeanDaoConfig;

    private final StudentBeanDao studentBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        studentBeanDaoConfig = daoConfigMap.get(StudentBeanDao.class).clone();
        studentBeanDaoConfig.initIdentityScope(type);

        studentBeanDao = new StudentBeanDao(studentBeanDaoConfig, this);

        registerDao(StudentBean.class, studentBeanDao);
    }
    
    public void clear() {
        studentBeanDaoConfig.clearIdentityScope();
    }

    public StudentBeanDao getStudentBeanDao() {
        return studentBeanDao;
    }

}
