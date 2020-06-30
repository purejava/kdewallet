package org.freedesktop.dbus.interfaces;

import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.handlers.Messaging;
import org.freedesktop.dbus.messages.DBusSignal;

import java.util.List;

public abstract class AbstractInterface extends Messaging {

    public AbstractInterface(DBusConnection connection, List<Class<? extends DBusSignal>> signals,
                             String serviceName, String objectPath, String interfaceName) {
        super(connection, signals, serviceName, objectPath, interfaceName);
    }

    @DBusInterfaceName("org.kde.KWallet.walletClosed")
    public static class walletClosed extends DBusSignal {
        public final int handle;

        public walletClosed(String path, int handle) throws DBusException {
            super(path, handle);
            this.handle = handle;
        }
    }
}