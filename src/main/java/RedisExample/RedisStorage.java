package RedisExample;

import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Date;

import static java.lang.System.out;

public class RedisStorage {
    // Объект для работы с Redis
    private RedissonClient redisson;

    // Объект для работы с ключами
    private RKeys rKeys;

    // Объект для работы с Sorted Set'ом
    private RScoredSortedSet<String> onlineUsers;

    private final static String KEY = "ONLINE_USERS";

    private double getTs() {
        return new Date().getTime() / 1000;
    }

    // Вывод всех ключей
    public void listKeys() {
        Iterable<String> keys = rKeys.getKeys();
        for(String key: keys) {
            out.println("KEY: " + key + ", type:" + rKeys.getType(key));
        }
    }

    // Инициализация
    public void init() throws Exception{
        Config config = new Config();
        config.useSingleServer().setAddress(PropertiesUtil.getInstance().getRedis());

        redisson = Redisson.create(config);
        rKeys = redisson.getKeys();
        onlineUsers = redisson.getScoredSortedSet(KEY);
        rKeys.delete(KEY);
    }

    public void shutdown() {
        if (redisson == null){
            return;
        }

        redisson.shutdown();
    }

    // Фиксируем посещение пользователем страницы
    public void logPageVisit(int user_id)
    {
        if (onlineUsers == null){
            return;
        }
        //ZADD ONLINE_USERS
        onlineUsers.add(getTs(), String.valueOf(user_id));
    }

    // Удаление
    public void deleteOldEntries(int secondsAgo)
    {
        if (onlineUsers == null){
            return;
        }
        //ZREVRANGEBYSCORE ONLINE_USERS 0 <time_5_seconds_ago>
        onlineUsers.removeRangeByScore(0, true, getTs() - secondsAgo, true);
    }

    // Число пользователей онлайн
    public int calculateUsersNumber()
    {
        if (onlineUsers == null){
            return 0;
        }
        //ZCOUNT ONLINE_USERS
        return onlineUsers.count(Double.NEGATIVE_INFINITY, true, Double.POSITIVE_INFINITY, true);
    }
}