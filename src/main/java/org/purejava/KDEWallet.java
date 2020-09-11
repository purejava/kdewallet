package org.purejava;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.handlers.Messaging;
import org.freedesktop.dbus.messages.DBusSignal;
import org.freedesktop.dbus.types.Variant;
import org.kde.KWallet;
import org.kde.Static;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KDEWallet extends Messaging implements KWallet, AutoCloseable {

    private Logger log = LoggerFactory.getLogger(KDEWallet.class);
    private DBusConnection connection;

    public static final List<Class<? extends DBusSignal>> signals = Arrays.asList(
            applicationDisconnected.class,
            folderUpdated.class,
            folderListUpdated.class,
            allWalletsClosed.class,
            walletClosedInt.class,
            // you can either register walletClosed or walletClosedInt
            // but not both at at the same time
            // see https://github.com/hypfvieh/dbus-java/issues/110
            // walletClosed.class,
            walletDeleted.class,
            walletAsyncOpened.class,
            walletOpened.class,
            walletCreated.class,
            walletListDirty.class);

    public KDEWallet(DBusConnection connection) {
        super(connection,
                signals,
                Static.Service.KWALLETD5,
                Static.ObjectPaths.KWALLETD5,
                Static.Interfaces.KWALLET);
        this.connection = connection;
    }

    @Override
    public boolean isEnabled() {
        try {
            connection.getRemoteObject("org.kde.kwalletd5",
                    "/modules/kwalletd5", KWallet.class);
            log.info("Kwallet daemon is available.");
            return true;
        } catch (DBusException e) {
            log.error(e.toString(), e.getCause());
            return false;
        }
    }

    @Override
    public int open(String wallet, long wId, String appid) {
        Object[] response = send("open", "sxs", wallet, wId, appid);
        return (int) response[0];
    }

    @Override
    public int openPath(String path, long wId, String appid) {
        Object[] response = send("openPath", "sxs", path, wId, appid);
        return (int) response[0];
    }

    @Override
    public int openAsync(String wallet, long wId, String appid, boolean handleSession) {
        Object[] response = send("openAsync", "sxsb", wallet, wId, appid, handleSession);
        return (int) response[0];
    }

    @Override
    public int openPathAsync(String path, long wId, String appid, boolean handleSession) {
        Object[] response = send("openPathAsync", "sxsb", path, wId, appid, handleSession);
        return (int) response[0];
    }

    @Override
    public int close(String wallet, boolean force) {
        Object[] response = send("close", "sb", wallet, force);
        return (int) response[0];
    }

    @Override
    public int close(int handle, boolean force, String appid) {
        Object[] response = send("close", "ibs", handle, force, appid);
        return (int) response[0];
    }

    @Override
    public void sync(int handle, String appid) {
        send("sync", "is", handle, appid);
    }

    @Override
    public int deleteWallet(String wallet) {
        Object[] response = send("deleteWallet", "s", wallet);
        return (int) response[0];
    }

    @Override
    public boolean isOpen(String wallet) {
        Object[] response = send("isOpen", "s", wallet);
        return (boolean) response[0];
    }

    @Override
    public boolean isOpen(int handle) {
        Object[] response = send("isOpen", "i", handle);
        return (boolean) response[0];
    }

    @Override
    public List<String> users(String wallet) {
        Object[] response = send("users", "s", wallet);
        return (List<String>) response[0];
    }

    @Override
    public void changePassword(String wallet, long wId, String appid) {
        send("changePassword", "sxs", wallet, wId, appid);
    }

    @Override
    public List<String> wallets() {
        Object[] response = send("wallets");
        return (List<String>) response[0];
    }

    @Override
    public List<String> folderList(int handle, String appid) {
        Object[] response = send("folderList", "is", handle, appid);
        return (List<String>) response[0];
    }

    @Override
    public boolean hasFolder(int handle, String folder, String appid) {
        Object[] response = send("hasFolder", "iss", handle, folder, appid);
        return (boolean) response[0];
    }

    @Override
    public boolean createFolder(int handle, String folder, String appid) {
        Object[] response = send("createFolder", "iss", handle, folder, appid);
        return (boolean) response[0];
    }

    @Override
    public boolean removeFolder(int handle, String folder, String appid) {
        Object[] response = send("removeFolder", "iss", handle, folder, appid);
        return (boolean) response[0];
    }

    @Override
    public List<String> entryList(int handle, String folder, String appid) {
        Object[] response = send("entryList", "iss", handle, folder, appid);
        return (List<String>) response[0];
    }

    @Override
    public byte[] readEntry(int handle, String folder, String key, String appid) {
        Object[] response = send("readEntry", "isss", handle, folder, key, appid);
        return (byte[]) response[0];
    }

    @Override
    public byte[] readMap(int handle, String folder, String key, String appid) {
        Object[] response = send("readMap", "isss", handle, folder, key, appid);
        return (byte[]) response[0];
    }

    @Override
    public String readPassword(int handle, String folder, String key, String appid) {
        Object[] response = send("readPassword", "isss", handle, folder, key, appid);
        return (String) response[0];
    }

    @Override
    public Map<String, byte[]> entriesList(int handle, String folder, String appid) {
        Object[] response = send("entriesList", "iss", handle, folder, appid);
        Map<String, Variant> map = (Map<String, Variant>) response[0];
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (byte[]) e.getValue().getValue()));
    }

    @Override
    public Map<String, byte[]> mapList(int handle, String folder, String appid) {
        Object[] response = send("mapList", "iss", handle, folder, appid);
        Map<String, Variant> map = (Map<String, Variant>) response[0];
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (byte[]) e.getValue().getValue()));
    }

    @Override
    public Map<String, String> passwordList(int handle, String folder, String appid) {
        Object[] response = send("passwordList", "iss", handle, folder, appid);
        Map<String, Variant> map = (Map<String, Variant>) response[0];
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue().getValue()));
    }

    @Override
    public int renameEntry(int handle, String folder, String oldName, String newName, String appid) {
        Object[] response = send("renameEntry", "issss", handle, folder, oldName, newName, appid);
        return (int) response[0];
    }

    @Override
    public int writeEntry(int handle, String folder, String key, byte[] value, int entryType, String appid) {
        Object[] response = send("writeEntry", "issayis", handle, folder, key, value, entryType, appid);
        return (int) response[0];
    }

    @Override
    public int writeEntry(int handle, String folder, String key, byte[] value, String appid) {
        Object[] response = send("writeEntry", "issays", handle, folder, key, value, appid);
        return (int) response[0];
    }

    @Override
    public int writeMap(int handle, String folder, String key, byte[] value, String appid) {
        Object[] response = send("writeMap", "issays", handle, folder, key, value, appid);
        return (int) response[0];
    }

    @Override
    public int writePassword(int handle, String folder, String key, String value, String appid) {
        Object[] response = send("writePassword", "issss", handle, folder, key, value, appid);
        return (int) response[0];
    }

    @Override
    public boolean hasEntry(int handle, String folder, String key, String appid) {
        Object[] response = send("hasEntry", "isss", handle, folder, key, appid);
        return (boolean) response[0];
    }

    @Override
    public int entryType(int handle, String folder, String key, String appid) {
        Object[] response = send("entryType", "isss", handle, folder, key, appid);
        return (int) response[0];
    }

    @Override
    public int removeEntry(int handle, String folder, String key, String appid) {
        Object[] response = send("removeEntry", "isss", handle, folder, key, appid);
        return (int) response[0];
    }

    @Override
    public boolean disconnectApplication(String wallet, String application) {
        Object[] response = send("disconnectApplication", "ss", wallet, application);
        return (boolean) response[0];
    }

    @Override
    public void reconfigure() {
        send("reconfigure");
    }

    @Override
    public boolean folderDoesNotExist(String wallet, String folder) {
        Object[] response = send("folderDoesNotExist", "ss", wallet, folder);
        return (boolean) response[0];
    }

    @Override
    public boolean keyDoesNotExist(String wallet, String folder, String key) {
        Object[] response = send("keyDoesNotExist", "sss", wallet, folder, key);
        return (boolean) response[0];
    }

    @Override
    public void closeAllWallets() {
        send("closeAllWallets");
    }

    @Override
    public String networkWallet() {
        Object[] response = send("networkWallet");
        return (String) response[0];
    }

    @Override
    public String localWallet() {
        Object[] response = send("localWallet");
        return (String) response[0];
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
            if (connection != null) connection.disconnect();
        } catch (Exception e) {
            log.error(e.toString(), e.getCause());
        }
    }
}