package com.shaw.demo.config;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import java.lang.reflect.Method;



/**
 * @author yichen
 * @Description:
 * @date 2021/7/21 15:04
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
}
)
public class DataPermissionInterceptor implements Interceptor {
    private static final Integer MAPPED_STATEMENT_INDEX = 0;
    private static final Integer PARAM_OBJ_INDEX = 1;
    private static final Integer ROW_BOUNDS_INDEX = 2;
    private static final Integer RESULT_HANDLER_INDEX = 3;
    private static final Integer CACHE_KEY_INDEX = 4;
    private static final Integer BOUND_SQL_INDEX = 5;
    private static final String COUNT_PRE = "_COUNT";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        /**
         * 获取执行SQL各个信息
         */
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[MAPPED_STATEMENT_INDEX];
        Object parameter = args[PARAM_OBJ_INDEX];
        RowBounds rowBounds = (RowBounds) args[ROW_BOUNDS_INDEX];
        ResultHandler resultHandler = (ResultHandler) args[RESULT_HANDLER_INDEX];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        if (args.length == 4) {
            //4 个参数时
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[CACHE_KEY_INDEX];
            boundSql = (BoundSql) args[BOUND_SQL_INDEX];
        }
        /**
         * 不需要数据权限，即执行的mapper方法参数上没有@DataAuth注解
         */
        DataAuth dataAuth = getPermissionByDelegate(ms);
        if (dataAuth == null) {//执行的方法无注解，执行原SQL
            return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        }
        /**
         * 需要数据权限，即执行的mapper方法参数上有@DataAuth注解
         */
        String sql = boundSql.getSql();//原sql
        String newSql = permissionSql(sql);//新SQL
        //根据newSql重新生成MappedStatement
        MappedStatement newMappedStatement = setCurrentSql(ms, parameter, boundSql, newSql);
        return executor.query(newMappedStatement, parameter, rowBounds, resultHandler, cacheKey, boundSql);

    }

    /**
     * 方法是否被打上注解@DataAuth
     *
     * @param mappedStatement
     * @return
     */
    private DataAuth getPermissionByDelegate(MappedStatement mappedStatement) {
        try {
            String id = mappedStatement.getId();
            //统计SQL取得注解也是实际查询id上得注解，所以需要去掉_COUNT
            if (id.contains(COUNT_PRE)) {
                id = id.replace(COUNT_PRE, "");
            }
            String className = id.substring(0, id.lastIndexOf("."));
            String methodName = id.substring(id.lastIndexOf(".") + 1, id.length());
            /**
             * 反射获取类及其中方法,判断是否打了@DataAuth
             */
            final Class<?> cls = Class.forName(className);
            final Method[] methods = cls.getMethods();
            for (Method me : methods) {
                if (me.getName().equals(methodName) && me.isAnnotationPresent(DataAuth.class)) {
                    return me.getAnnotation(DataAuth.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param sql:原SQL。
     * @Description: 给原SQL加权限
     * @return: java.lang.String
     * @Author: yichen.xiao
     * @Date: 2021/7/21 17:46
     */
    private String permissionSql(String sql) {
        StringBuffer authSql = new StringBuffer(sql);

        StringBuffer temp = new StringBuffer(" ");
        //TODO 获取权限信息然后将拼接的权限SQL注入到temp。eg. temp.append("权限SQL")

        /**
         * SQL关键字位置
         */
        String str = sql.toLowerCase();
        int groupBy = str.indexOf("group by");
        int orderBy = str.indexOf("order by");
        int limit = str.indexOf("limit");
        groupBy = groupBy == -1 ? Integer.MAX_VALUE : groupBy;
        orderBy = orderBy == -1 ? Integer.MAX_VALUE : orderBy;
        limit = limit == -1 ? Integer.MAX_VALUE : limit;
        int index = Math.min(groupBy, Math.min(orderBy, limit));
        /**
         * 没有where关键字
         */
        if (str.indexOf("where") < 0) {
            temp.insert(0, "  where 1=1 ");
        }
        /**
         * 在关键字前插入权限SQL
         */
        if (index == Integer.MAX_VALUE) {
            authSql.append(temp);
        } else {
            authSql.insert(index - 1, temp);
        }
        authSql.append(" and view_scope =1");
        System.out.println(String.format("权限SQL：%s", authSql));
        return authSql.toString();
    }

    /**
     * 用新Sql生成MappedStatement
     *
     * @param mappedStatement
     * @param paramObj
     * @param boundSql
     * @param sql
     * @return
     */
    private MappedStatement setCurrentSql(MappedStatement mappedStatement, Object paramObj, BoundSql boundSql, String sql) {
        BoundSqlSource boundSqlSource = new BoundSqlSource(boundSql);
        MappedStatement newMappedStatement = copyFromMappedStatement(mappedStatement, boundSqlSource);
        MetaObject metaObject = MetaObject.forObject(newMappedStatement,
                new DefaultObjectFactory(), new DefaultObjectWrapperFactory(),
                new DefaultReflectorFactory());
        metaObject.setValue("sqlSource.boundSql.sql", sql);
        return newMappedStatement;
    }

    /**
     * 自定义私有SqlSource
     */
    private class BoundSqlSource implements SqlSource {

        private BoundSql boundSql;

        private BoundSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }


    /**
     * copy
     *
     * @param ms
     * @param newSqlSource
     * @return
     */
    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }


}