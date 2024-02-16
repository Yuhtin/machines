package com.yuhtin.quotes.machines.repository.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import com.yuhtin.quotes.saint.playersleague.repository.adapters.LeagueUserAdapter;
import com.yuhtin.quotes.saint.playersleague.repository.adapters.LeagueUsernameAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@RequiredArgsConstructor
public final class UserRepository {

    private static final String TABLE = "leagues_user_data";

    @Getter
    private final SQLExecutor sqlExecutor;

    public void createTable() {
        sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "username CHAR(36) NOT NULL," +
                "points INT NOT NULL," +
                "rank_id INT NOT NULL," +
                ");"
        );
    }

    public void recreateTable() {
        sqlExecutor.updateQuery("DROP TABLE IF EXISTS " + TABLE);
        createTable();
    }

    public Set<LeagueUser> findAll() {
        return sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE,
                statement -> {},
                LeagueUserAdapter.class
        );
    }

    public LeagueUser find(String username) {
        return sqlExecutor.resultOneQuery(
                "SELECT * FROM " + TABLE + " WHERE username = ?",
                statement -> statement.set(1, username),
                LeagueUserAdapter.class
        );
    }

    public void insert(LeagueUser leagueUser) {
        sqlExecutor.updateQuery(
                "INSERT INTO " + TABLE + " VALUES (?, ?, ?)",
                statement -> {
                    statement.set(1, leagueUser.getUsername());
                    statement.set(2, leagueUser.getPoints());
                    statement.set(3, leagueUser.getRankId());
                }
        );
    }

    public Set<String> orderByPoints() {
        return sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE + " ORDER BY points DESC LIMIT 14",
                statement -> {},
                LeagueUsernameAdapter.class
        );
    }
}
