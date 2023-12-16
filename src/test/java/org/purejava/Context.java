package org.purejava;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Context {

    private static final Logger LOG = LoggerFactory.getLogger(Context.class);

    public DBusConnection connection = null;

    public Context() { }

    public void ensureService() {
        try {
            connection = DBusConnectionBuilder.forSessionBus().withShared(false).build();
        } catch (DBusException e) {
            LOG.error(e.toString(), e.getCause());
        }
    }

    public void after() {
        try {
            connection.disconnect();
            Thread.sleep(150L);
        } catch (InterruptedException e) {
            LOG.error(e.toString(), e.getCause());
        }
    }
}