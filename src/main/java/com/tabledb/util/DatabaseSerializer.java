package com.tabledb.util;

import com.tabledb.core.Database;
import com.tabledb.model.Table;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseSerializer {
    private final String filename;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public DatabaseSerializer(String filename) {
        this.filename = filename;
    }

    public void serialize(Database database) {
        lock.writeLock().lock();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(Paths.get(filename))))) {
            oos.writeObject(database);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize database", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Database deserialize() {
        lock.readLock().lock();
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(Paths.get(filename))))) {
            return (Database) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize database", e);
        } finally {
            lock.readLock().unlock();
        }
    }
}