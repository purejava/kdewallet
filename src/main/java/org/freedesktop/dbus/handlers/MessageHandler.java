package org.freedesktop.dbus.handlers;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.messages.MethodCall;
import org.freedesktop.dbus.types.Variant;
import org.kde.Static;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MessageHandler {

    private Logger log = LoggerFactory.getLogger(MessageHandler.class);

    private DBusConnection connection;

    public MessageHandler(DBusConnection connection) {
        this.connection = connection;

        if (this.connection != null) {
            this.connection.setWeakReferences(true);
            Runtime.getRuntime().addShutdownHook(new Thread(() ->
                    this.connection.disconnect()
            ));
        }
    }

    public Object[] send(String service, String path, String iface, String method, String signature, Object... args) {
        try {
            org.freedesktop.dbus.messages.Message message = new MethodCall(
                    service,
                    path,
                    iface,
                    method, (byte) 0, signature, args);

            connection.sendMessage(message);

            org.freedesktop.dbus.messages.Message response = ((MethodCall) message).getReply(2000L);
            log.trace(String.valueOf(response));

            if (response instanceof org.freedesktop.dbus.errors.Error) {
                throw new DBusException(response.getName() + ": " + response.getParameters()[0]);
            }

            Object[] parameters = response.getParameters();
            log.debug(Arrays.deepToString(parameters));
            return parameters;

        } catch (DBusException e) {
            log.error(e.toString(), e.getCause());
        }
        return null;
    }

    public Variant getProperty(String service, String path, String iface, String property) {
        Object[] response = send(service, path, Static.DBus.Interfaces.DBUS_PROPERTIES,
                "Get", "ss", iface, property);
        if (response == null) return null;
        return (Variant) response[0];
    }

    public Variant getAllProperties(String service, String path, String iface) {
        Object[] response = send(service, path, Static.DBus.Interfaces.DBUS_PROPERTIES,
                "GetAll", "ss", iface);
        if (response == null) return null;
        return (Variant) response[0];
    }

    public void setProperty(String service, String path, String iface, String property, Variant value) {
        send(service, path, Static.DBus.Interfaces.DBUS_PROPERTIES,
                "Set", "ssv", iface, property, value);
    }

    public DBusConnection getConnection() {
        return connection;
    }

}
