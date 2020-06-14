package org.kde;

import org.freedesktop.dbus.Static;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.interfaces.AbstractInterface;
import org.freedesktop.dbus.messages.DBusSignal;

import java.util.List;

@DBusInterfaceName(Static.Interfaces.WALLET)
public abstract class KWallet extends AbstractInterface implements KWalletIface {

    public KWallet(DBusConnection connection, List<Class<? extends DBusSignal>> signals,
                   String serviceName, String objectPath, String interfaceName) {
        super(connection, signals, serviceName, objectPath, interfaceName);
    }

    /*
    abstract public Map<String, Variant> readEntryList(int handle, String folder, String key, String appid);

    abstract public Map<String, Variant> readMapList(int handle, String folder, String key, String appid);

    abstract public Map<String, Variant> readPasswordList(int handle, String folder, String key, String appid);
    */

}