package org.purejava;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;

public class Context {

    private Logger log;

    public DBusConnection connection = null;

    public Context(Logger log) {
        this.log = log;
    }

    public void ensureService() {
        try {
            connection = DBusConnectionBuilder.forSessionBus().withShared(false).build();
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
}