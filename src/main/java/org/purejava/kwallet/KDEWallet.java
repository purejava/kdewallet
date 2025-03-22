package org.purejava.kwallet;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.purejava.kwallet.freedesktop.dbus.handlers.Messaging;
import org.freedesktop.dbus.interfaces.DBus;
import org.freedesktop.dbus.messages.DBusSignal;
import org.freedesktop.dbus.types.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KDEWallet extends Messaging implements KWallet, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(KDEWallet.class);
    private static String SERVICE;
    private static String OBJECT_PATHS;
    private final DBusConnection connection;

    public static final List<Class<? extends DBusSignal>> SIGNALS = Arrays.asList(
            applicationDisconnected.class,
            folderUpdated.class,
            folderListUpdated.class,
            allWalletsClosed.class,
            walletClosedId.class,
            walletClosed.class,
            walletDeleted.class,
            walletAsyncOpened.class,
            walletOpened.class,
            walletCreated.class,
            walletListDirty.class);

    public KDEWallet(DBusConnection connection) {
        super(connection,
                SIGNALS,
                SERVICE,
                OBJECT_PATHS,
                Static.Interfaces.KWALLET);
        this.connection = connection;
    }

    static {
        try (var connection = DBusConnectionBuilder.forSessionBus().withShared(false).build()) {
            try {
                var bus = connection.getRemoteObject("org.freedesktop.DBus",
                        "/org/freedesktop/DBus", DBus.class);
                if (Arrays.asList(bus.ListActivatableNames()).contains(Static.Service.KWALLETD6)) {
                    SERVICE = Static.Service.KWALLETD6;
                    OBJECT_PATHS = Static.ObjectPaths.KWALLETD6;
                    LOG.debug("Kwallet daemon v6 initialized");
                } else if (Arrays.asList(bus.ListActivatableNames()).contains(Static.Service.KWALLETD5)) {
                    SERVICE = Static.Service.KWALLETD5;
                    OBJECT_PATHS = Static.ObjectPaths.KWALLETD5;
                    LOG.debug("Kwallet daemon v5 initialized");
                }
            } catch (DBusException e) {
                LOG.error(e.toString(), e.getCause());
            }
        } catch (IOException | DBusException e) {
            LOG.error(e.toString(), e.getCause());
        }
    }

    @Override
    public boolean isEnabled() {
        if (null == connection) {
            LOG.debug("No d-bus connection available");
            return false;
        }
        try {
            var bus = connection.getRemoteObject("org.freedesktop.DBus",
                    "/org/freedesktop/DBus", DBus.class);
            if (Arrays.asList(bus.ListActivatableNames()).contains(Static.Service.KWALLETD6)) {
                LOG.debug("Kwallet daemon v6 is available");
                return true;
            } else if (Arrays.asList(bus.ListActivatableNames()).contains(Static.Service.KWALLETD5)) {
                LOG.debug("Kwallet daemon v5 is available");
                return true;
            } else {
                return false;
            }
        } catch (DBusException e) {
            LOG.error(e.toString(), e.getCause());
            return false;
        }
    }

    @Override
    public int open(String wallet, long wId, String appid) {
        var response = send("open", "sxs", wallet, wId, appid);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public int openPath(String path, long wId, String appid) {
        var response = send("openPath", "sxs", path, wId, appid);
        return null == response ? 0 : (int) response[0];
    }

    @Override
    public int openAsync(String wallet, long wId, String appid, boolean handleSession) {
        var response = send("openAsync", "sxsb", wallet, wId, appid, handleSession);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public int openPathAsync(String path, long wId, String appid, boolean handleSession) {
        var response = send("openPathAsync", "sxsb", path, wId, appid, handleSession);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public int close(String wallet, boolean force) {
        var response = send("close", "sb", wallet, force);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public int close(int handle, boolean force, String appid) {
        var response = send("close", "ibs", handle, force, appid);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public void sync(int handle, String appid) {
        send("sync", "is", handle, appid);
    }

    @Override
    public int deleteWallet(String wallet) {
        var response = send("deleteWallet", "s", wallet);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public boolean isOpen(String wallet) {
        var response = send("isOpen", "s", wallet);
        return null != response && (boolean) response[0];
    }

    @Override
    public boolean isOpen(int handle) {
        var response = send("isOpen", "i", handle);
        return null != response && (boolean) response[0];
    }

    @Override
    public List<String> users(String wallet) {
        return contentOrEmptyList(send("users", "s", wallet));
    }

    @Override
    public void changePassword(String wallet, long wId, String appid) {
        send("changePassword", "sxs", wallet, wId, appid);
    }

    @Override
    public List<String> wallets() {
        return contentOrEmptyList(send("wallets"));
    }

    @Override
    public List<String> folderList(int handle, String appid) {
        return contentOrEmptyList(send("folderList", "is", handle, appid));
    }

    @Override
    public boolean hasFolder(int handle, String folder, String appid) {
        var response = send("hasFolder", "iss", handle, folder, appid);
        return null != response && (boolean) response[0];
    }

    @Override
    public boolean createFolder(int handle, String folder, String appid) {
        var response = send("createFolder", "iss", handle, folder, appid);
        return null != response && (boolean) response[0];
    }

    @Override
    public boolean removeFolder(int handle, String folder, String appid) {
        var response = send("removeFolder", "iss", handle, folder, appid);
        return null != response && (boolean) response[0];
    }

    @Override
    public List<String> entryList(int handle, String folder, String appid) {
        return contentOrEmptyList(send("entryList", "iss", handle, folder, appid));
    }

    @Override
    public byte[] readEntry(int handle, String folder, String key, String appid) {
        var response = send("readEntry", "isss", handle, folder, key, appid);
        return getBytes(response);
    }

    @Override
    public byte[] readMap(int handle, String folder, String key, String appid) {
        var response = send("readMap", "isss", handle, folder, key, appid);
        return getBytes(response);
    }

    /**
     * Helper method to convert d-bus arg type "ay" (array of byte) into a byte[]
     *
     * @param response The b-bus reposnse to process
     * @return The data part of the response converted
     */
    private byte[] getBytes(Object[] response) {
        if (null == response) {
            return new byte[0];
        } else {
            var objectList = (List<?>) response[0];
            var entry = new byte[objectList.size()];
            IntStream.range(0, objectList.size()).forEach(i -> entry[i] = (Byte) objectList.get(i));
            return entry;
        }
    }

    @Override
    public String readPassword(int handle, String folder, String key, String appid) {
        var response = send("readPassword", "isss", handle, folder, key, appid);
        return null == response ? "" : (String) response[0];
    }

    @Override
    public Map<String, byte[]> entriesList(int handle, String folder, String appid) {
        var map = contentOrEmptyMap(send("entriesList", "iss", handle, folder, appid));
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    var objectList = (List<?>) e.getValue().getValue();
                    var innerMap = new byte[objectList.size()];
                    IntStream.range(0, objectList.size()).forEach(i -> innerMap[i] = (Byte) objectList.get(i));
                    return innerMap;
                }));
    }

    @Override
    public Map<String, byte[]> mapList(int handle, String folder, String appid) {
        var map = contentOrEmptyMap(send("mapList", "iss", handle, folder, appid));
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    var objectList = (List<?>) e.getValue().getValue();
                    var innerMap = new byte[objectList.size()];
                    IntStream.range(0, objectList.size()).forEach(i -> innerMap[i] = (Byte) objectList.get(i));
                    return innerMap;
                }));
    }

    @Override
    public Map<String, String> passwordList(int handle, String folder, String appid) {
        var map = contentOrEmptyMap(send("passwordList", "iss", handle, folder, appid));
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue().getValue()));
    }

    @Override
    public int renameEntry(int handle, String folder, String oldName, String newName, String appid) {
        var response = send("renameEntry", "issss", handle, folder, oldName, newName, appid);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public int writeEntry(int handle, String folder, String key, byte[] value, int entryType, String appid) {
        var response = send("writeEntry", "issayis", handle, folder, key, value, entryType, appid);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public int writeEntry(int handle, String folder, String key, byte[] value, String appid) {
        var response = send("writeEntry", "issays", handle, folder, key, value, appid);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public int writeMap(int handle, String folder, String key, byte[] value, String appid) {
        var response = send("writeMap", "issays", handle, folder, key, value, appid);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public int writePassword(int handle, String folder, String key, String value, String appid) {
        var response = send("writePassword", "issss", handle, folder, key, value, appid);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public boolean hasEntry(int handle, String folder, String key, String appid) {
        var response = send("hasEntry", "isss", handle, folder, key, appid);
        return null != response && (boolean) response[0];
    }

    @Override
    public int entryType(int handle, String folder, String key, String appid) {
        var response = send("entryType", "isss", handle, folder, key, appid);
        return null == response ? 0 : (int) response[0];
    }

    @Override
    public int removeEntry(int handle, String folder, String key, String appid) {
        var response = send("removeEntry", "isss", handle, folder, key, appid);
        return null == response ? -1 : (int) response[0];
    }

    @Override
    public boolean disconnectApplication(String wallet, String application) {
        var response = send("disconnectApplication", "ss", wallet, application);
        return null != response && (boolean) response[0];
    }

    @Override
    public void reconfigure() {
        send("reconfigure");
    }

    @Override
    public boolean folderDoesNotExist(String wallet, String folder) {
        var response = send("folderDoesNotExist", "ss", wallet, folder);
        return null != response && (boolean) response[0];
    }

    @Override
    public boolean keyDoesNotExist(String wallet, String folder, String key) {
        var response = send("keyDoesNotExist", "sss", wallet, folder, key);
        return null != response && (boolean) response[0];
    }

    @Override
    public void closeAllWallets() {
        send("closeAllWallets");
    }

    @Override
    public String networkWallet() {
        var response = send("networkWallet");
        return null == response ? "" : (String) response[0];
    }

    @Override
    public String localWallet() {
        var response = send("localWallet");
        return null == response ? "" : (String) response[0];
    }

    @Override
    public void pamOpen(String wallet, byte[] passwordHash, int sessionTimeout) {
        send("pamOpen", "sayi", wallet, passwordHash, sessionTimeout);
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public String getObjectPath() {
        return super.getObjectPath();
    }

    public void close() {
        try {
            if (null != connection && connection.isConnected()) connection.disconnect();
        } catch (Exception e) {
            LOG.error(e.toString(), e.getCause());
        }
    }

    private List<String> contentOrEmptyList(Object[] o) {
        return null == o ? List.of() : (List<String>) o[0];
    }

    private Map<String, Variant> contentOrEmptyMap(Object[] o) {
        return null == o ? Map.of() : (Map<String, Variant>) o[0];
    }
}