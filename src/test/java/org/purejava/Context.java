package org.purejava;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class Context {

    public Logger log;

    public DBusConnection connection = null;

    public Context(Logger log) {
        this.log = log;
    }

    public void ensureService() {
        try {
            connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);
        } catch (DBusException e) {
            log.error(e.toString(), e.getCause());
        }
    }

    public void after() {
        try {
            connection.disconnect();
            Thread.sleep(150L);
        } catch (InterruptedException e) {
            log.error(e.toString(), e.getCause());
        }
    }

    public static String label(String name, String msg) {
        return name + ": \"" + msg + "\"";
    }

    public static String label(String name, int number) {
        return name + ": " + number;
    }

    public static String label(String name, long number) {
        return name + ": " + number;
    }

    public static String label(String name, BigInteger number) {
        return name + ": " + number;
    }

    public static String label(String name, byte[] bytes) {
        return name + ": " + Arrays.toString(bytes);
    }

    public static String label(String name, List list) {
        return name + ": " + Arrays.toString(list.toArray());
    }

}