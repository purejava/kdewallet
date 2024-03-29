package org.purejava.kwallet.freedesktop.dbus.handlers;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.types.Variant;
import org.purejava.kwallet.Static;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MessageHandler.class);

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
            var msgFactory = connection.getMessageFactory();
            var message = msgFactory.createMethodCall(service, path, iface, method, (byte) 0, signature, args);

            if (LOG.isTraceEnabled()) LOG.trace(String.valueOf(message));
            connection.sendMessage(message);

            var response = message.getReply(2000L);
            if (LOG.isTraceEnabled()) LOG.trace(String.valueOf(response));

            Object[] parameters = null;
            if (response != null) {
                parameters = response.getParameters();
                LOG.debug(Arrays.deepToString(parameters));
            }

            if (response instanceof org.freedesktop.dbus.messages.Error) {
                var error = response.getName();
                switch (error) {
                    case "org.freedesktop.DBus.Error.NoReply",
                         "org.freedesktop.DBus.Error.UnknownMethod",
                         "org.freedesktop.dbus.exceptions.NotConnected" -> {
                        LOG.debug(error);
                        return null;
                    }
                    default -> throw new DBusException(error);
                }
            }
            return parameters;

        } catch (DBusException e) {
            LOG.error("Unexpected D-Bus response:", e);
        }

        return null;
    }

    public Variant getProperty(String service, String path, String iface, String property) {
        var response = send(service, path, Static.DBus.Interfaces.DBUS_PROPERTIES,
                "Get", "ss", iface, property);
        return response == null ? null : (Variant) response[0];
    }

    public Variant getAllProperties(String service, String path, String iface) {
        var response = send(service, path, Static.DBus.Interfaces.DBUS_PROPERTIES,
                "GetAll", "ss", iface);
        return response == null ? null : (Variant) response[0];
    }

    public void setProperty(String service, String path, String iface, String property, Variant value) {
        send(service, path, Static.DBus.Interfaces.DBUS_PROPERTIES,
                "Set", "ssv", iface, property, value);
    }

    public DBusConnection getConnection() {
        return connection;
    }

}
