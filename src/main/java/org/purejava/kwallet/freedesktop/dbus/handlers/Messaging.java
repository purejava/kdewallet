package org.purejava.kwallet.freedesktop.dbus.handlers;

import org.freedesktop.dbus.ObjectPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.messages.DBusSignal;
import org.freedesktop.dbus.types.Variant;
import org.purejava.kwallet.Static;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

abstract public class Messaging {

    private static final Logger LOG = LoggerFactory.getLogger(Messaging.class);
    private DBusConnection connection;
    private MessageHandler msg;
    private SignalHandler sh = SignalHandler.getInstance();
    private String serviceName;
    private String objectPath;
    private String interfaceName;

    public Messaging(DBusConnection connection, List<Class<? extends DBusSignal>> signals,
                     String serviceName, String objectPath, String interfaceName) {
        this.connection = connection;
        this.msg = new MessageHandler(connection);
        if (null != signals) {
            this.sh.connect(connection, signals);
        }
        if (null == serviceName || null == objectPath) {
            LOG.error("Kwallet daemon not initialized properly");
        }
        this.serviceName = serviceName;
        this.objectPath = objectPath;
        this.interfaceName = interfaceName;
    }

    public Object[] send(String method) {
        return msg.send(serviceName, objectPath, interfaceName, method, "");
    }

    public Object[] send(String method, String signature, Object... arguments) {
        return msg.send(serviceName, objectPath, interfaceName, method, signature, arguments);
    }

    protected Variant getProperty(String property) {
        return msg.getProperty(serviceName, objectPath, interfaceName, property);
    }

    protected Variant getAllProperties() {
        return msg.getAllProperties(serviceName, objectPath, interfaceName);
    }

    protected void setProperty(String property, Variant value) {
        msg.setProperty(serviceName, objectPath, interfaceName, property, value);
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getObjectPath() {
        return objectPath;
    }

    public ObjectPath getPath() {
        return Static.Convert.toObjectPath(objectPath);
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public MessageHandler getMessageHandler() {
        return msg;
    }

    public SignalHandler getSignalHandler() {
        return sh;
    }

    public DBusConnection getConnection() {
        return connection;
    }

}