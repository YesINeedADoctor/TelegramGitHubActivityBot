package io.project.SpringTelegramGHActivityBot.db;

import java.util.Collection;
import java.util.Optional;

public interface Dao<T> {
    Optional get(Long id);
    Collection getAll();
    Optional save(T t);
    void update(T t);
    void delete(T t);
}
