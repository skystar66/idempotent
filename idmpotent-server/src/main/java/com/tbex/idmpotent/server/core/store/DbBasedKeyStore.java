package com.tbex.idmpotent.server.core.store;

import com.tbex.idmpotent.server.core.enums.KeyState;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 */
public abstract class DbBasedKeyStore {

    /**
     * 插入SQL
     */
    public static final String INSERT_SQL = "insert into idpkey (id, key_state, content) values(?, ?, ?);";
    /**
     * 保存SQL，如果id已存在则更新
     */
    static final String SAVE_SQL = "insert into idpkey (id, key_state, content) values(?, ?, ?) on duplicate key update key_state = ?, content = ?;";

    static final String QUERY_SQL = "select id, key_state, content from idpkey where id = ?;";

    /**
     * 查询SQL，加行锁
     */
    static final String QUERY_LOCKSQL = "select id, key_state, content from idpkey where id = ? for update;";
    /**
     * 条件查询SQL，in子句需要拼接sql
     */
    private static final String QUERY_INSTATES_LOCKSQL_PREFIX = "select id, key_state, content from idpkey where id = ? or key_state in (";
    private static final String QUERY_INSTATES_LOCKSQL_SUFFIX = ") for update";
    /**
     * 删除SQL
     */
    static final String DELETE_SQL = "delete from idpkey where id = ?;";
    /**
     * 过期清理SQL
     */
    static final String CLEANUP_SQL = "delete from idpkey where created_time < date_add(now(), interval -5 minute);";

    private final Map<Integer, String> SQL_CACHE = new HashMap<>();

    private Integer hashing(Set<KeyState> states) {
        int hash = 0;
        for (KeyState state : states) {
            hash += state.getValue();
        }
        return hash;
    }

    String genQueryInStatesSql(Set<KeyState> states) {
        String sql;
        int hash = hashing(states);
        if (null != (sql = SQL_CACHE.get(hash))) {
            return sql;
        }
        StringBuilder sqlSB = new StringBuilder(QUERY_INSTATES_LOCKSQL_PREFIX);
        KeyState[] ss = states.toArray(new KeyState[0]);
        for (int i = 0; i < ss.length; i++) {
            sqlSB.append("?");
            if (i != ss.length - 1) {
                sqlSB.append(", ");
            }
        }
        sqlSB.append(QUERY_INSTATES_LOCKSQL_SUFFIX);
        sql = sqlSB.toString();
        SQL_CACHE.put(hash, sql);
        return sql;
    }

}
