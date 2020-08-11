package com.tbex.idmpotent.client.keystore.jdbc;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.tbex.idmpotent.client.IdpKey;
import com.tbex.idmpotent.client.KeyState;
import com.tbex.idmpotent.client.Msgs;
import com.tbex.idmpotent.client.Pair;
import com.tbex.idmpotent.client.keystore.KeyStore;
import com.tbex.idmpotent.client.keystore.KeyStoreException;
import com.tbex.idmpotent.client.spring.DebugConfig;
import com.tbex.idmpotent.client.util.NamedThreadFactory;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: JdbcKeyStore
 * @Description: todo jdbc持久化
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:48
 * @Version: 1.0
 */

@Slf4j
@Data
@Accessors(chain = true)
@SuppressWarnings("all")
public class JdbcKeyStore extends DbBasedKeyStore implements KeyStore {

    private DataSource dataSource;

    /**
     * 过期清理任务线程池
     */
    private static final ScheduledExecutorService CLEANUP_POOL = Executors
            .newScheduledThreadPool(1, new NamedThreadFactory("idp-cleanup"));

    public JdbcKeyStore() {
        // 每隔5分钟清理一次过期tidp
        CLEANUP_POOL.scheduleAtFixedRate(() -> {
                    if (DebugConfig.getMode() == DebugConfig.Mode.DEBUG) {
                        log.info(">> IdpKey清理开始");
                    }
                    try (Connection conn = dataSource.getConnection()) {
                        PreparedStatement pstmt = conn.prepareStatement(CLEANUP_SQL);
                        pstmt.executeUpdate();
                    } catch (SQLException cause) {
                        if (DebugConfig.getMode() == DebugConfig.Mode.DEBUG) {
                            log.error(Msgs.IDPKEY_KEYSTORE_CLEANUP_EXCEPTION, cause);
                        }
                    }
                    if (DebugConfig.getMode() == DebugConfig.Mode.DEBUG) {
                        log.info("<< IdpKey清理结束");
                    }
                },
                EXPIRE_TIME, EXPIRE_TIME, TimeUnit.SECONDS);
    }

    @Override
    public void replace(IdpKey k) throws KeyStoreException {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(SAVE_SQL);
            pstmt.setString(1, k.getId());
            pstmt.setString(2, k.getKeyState().toString());
            pstmt.setBytes(3, k.getContent());
            pstmt.setString(4, k.getKeyState().toString());
            pstmt.setBytes(5, k.getContent());
            if (DebugConfig.getMode() == DebugConfig.Mode.DEBUG) {
                log.info("save_sql: " + pstmt.toString());
            }
            pstmt.executeUpdate();
        } catch (SQLException cause) {
            throw new KeyStoreException(Msgs.IDPKEY_KEYSTORE_SAVE_EXCEPTION, k, cause);
        }
    }

    /**
     * 数据库中实现swap操作，使用了行锁
     */
    @Override
    public Pair putIfAbsent(IdpKey k) throws KeyStoreException {
        Preconditions.checkNotNull(k);
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement queryPStmt = conn.prepareStatement(QUERY_LOCKSQL);
            queryPStmt.setString(1, k.getId());
            ResultSet rs = queryPStmt.executeQuery();
            IdpKey oldK;
            Integer updatedCount = 0;
            if (rs.next()) {
                // 如果存在，则将旧值返回
                oldK = new IdpKey()
                        .setId(rs.getString(1))
                        .setKeyState(Enum.valueOf(KeyState.class, rs.getString(2)))
                        .setContent(rs.getBytes(3));
            } else {
                // 如果不存在，则插入新值
                PreparedStatement savePStmt = conn.prepareStatement(SAVE_SQL);
                savePStmt.setString(1, k.getId());
                savePStmt.setString(2, k.getKeyState().toString());
                savePStmt.setBytes(3, k.getContent());
                savePStmt.setString(4, k.getKeyState().toString());
                savePStmt.setBytes(5, k.getContent());
                updatedCount = savePStmt.executeUpdate();
                oldK = k;
            }
            conn.commit();
            return new Pair(oldK, updatedCount);
        } catch (SQLException cause) {
            throw new KeyStoreException(Msgs.IDPKEY_KEYSTORE_SAVE_EXCEPTION, k, cause);
        }
    }

    @Override
    public Pair putIfAbsentOrInStates(IdpKey k, Set<KeyState> states)
            throws KeyStoreException {
        Preconditions.checkNotNull(k);
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            String sql = genQueryInStatesSql(states);
            PreparedStatement queryPStmt = conn.prepareStatement(sql);
            queryPStmt.setString(1, k.getId());
            KeyState[] ss = states.toArray(new KeyState[0]);
            for (int i = 0; i < ss.length; i++) {
                queryPStmt.setString(2 + i, ss[i].toString());
            }
            if (DebugConfig.getMode() == DebugConfig.Mode.DEBUG) {
                log.info("query_in_states_sql: " + queryPStmt.toString());
            }
            ResultSet rs = queryPStmt.executeQuery();
            IdpKey oldK;
            Integer updatedCount = 0;
            if (rs.next()) {
                // 如果存在，则将旧值返回
                oldK = new IdpKey()
                        .setId(rs.getString(1))
                        .setKeyState(Enum.valueOf(KeyState.class, rs.getString(2)))
                        .setContent(rs.getBytes(3));
            } else {
                // 如果不存在，则插入新值，并将新值返回
                PreparedStatement savePStmt = conn.prepareStatement(SAVE_SQL);
                savePStmt.setString(1, k.getId());
                savePStmt.setString(2, k.getKeyState().toString());
                savePStmt.setBytes(3, k.getContent());
                savePStmt.setString(4, k.getKeyState().toString());
                savePStmt.setBytes(5, k.getContent());
                if (DebugConfig.getMode() == DebugConfig.Mode.DEBUG) {
                    log.info("save_sql: " + savePStmt.toString());
                }
                updatedCount = savePStmt.executeUpdate();
                oldK = k;
            }
            conn.commit();
            return new Pair(oldK, updatedCount);
        } catch (SQLException cause) {
            throw new KeyStoreException(Msgs.IDPKEY_KEYSTORE_SAVE_EXCEPTION, k, cause);
        }
    }

    @Override
    public void remove(String id) throws KeyStoreException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException cause) {
            throw new KeyStoreException(Msgs.IDPKEY_KEYSTORE_DELETE_EXCEPTION, id, cause);
        }
    }
}
