package org.purejava;

import org.kde.Static;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.handlers.Messaging;
import org.freedesktop.dbus.messages.DBusSignal;
import org.kde.KWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class KDEWallet extends Messaging implements KWallet, AutoCloseable {

    private Logger log = LoggerFactory.getLogger(KDEWallet.class);
    private DBusConnection connection;

    public static final List<Class<? extends DBusSignal>> signals = Arrays.asList(
            applicationDisconnected.class,
            folderUpdated.class,
            folderListUpdated.class,
            allWalletsClosed.class,
            walletClosedInt.class,
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
        return 0;
    }

    @Override
    public int openAsync(String wallet, long wId, String appid, boolean handleSession) {
        Object[] response = send("openAsync", "sxsb", wallet, wId, appid, handleSession);
        return (int) response[0];
    }

    @Override
    public int openPathAsync(String path, long wId, String appid, boolean handleSession) {
        return 0;
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

    }

    @Override
    public int deleteWallet(String wallet) {
        return 0;
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
        return null;
    }

    @Override
    public void changePassword(String wallet, long wId, String appid) {

    }

    @Override
    public List<String> wallets() {
        Object[] response = send("wallets");
        return (List<String>) response[0];
    }

    @Override
    public List<String> folderList(int handle, String appid) {
        return null;
    }

    @Override
    public boolean hasFolder(int handle, String folder, String appid) {
        return false;
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
    public List<Byte> readMap(int handle, String folder, String key, String appid) {
        return null;
    }

    @Override
    public String readPassword(int handle, String folder, String key, String appid) {
        Object[] response = send("readPassword", "isss", handle, folder, key, appid);
        return (String) response[0];
    }

    @Override
    public int renameEntry(int handle, String folder, String oldName, String newName, String appid) {
        return 0;
    }

    @Override
    public int writeEntry(int handle, String folder, String key, List<Byte> value, int entryType, String appid) {
        Object[] response = send("writeEntry", "issayis", handle, folder, key, value, entryType, appid);
        return (int) response[0];
    }

    @Override
    public int writeEntry(int handle, String folder, String key, List<Byte> value, String appid) {
        Object[] response = send("writeEntry", "issays", handle, folder, key, value, appid);
        return (int) response[0];
    }

    @Override
    public int writeMap(int handle, String folder, String key, List<Byte> value, String appid) {
        return 0;
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
        return false;
    }

    @Override
    public void reconfigure() {
    }

    @Override
    public boolean folderDoesNotExist(String wallet, String folder) {
        return false;
    }

    @Override
    public boolean keyDoesNotExist(String wallet, String folder, String key) {
        return false;
    }

    @Override
    public void closeAllWallets() {
    }

    @Override
    public String networkWallet() {
        return null;
    }

    @Override
    public String localWallet() {
        return null;
    }

    @Override
    public void pamOpen(String wallet, List<Byte> passwordHash, int sessionTimeout) {
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public String getObjectPath() {
        return null;
    }

    public void close() {
        try {
            if (connection != null) connection.disconnect();
        } catch (Exception e) {
            log.error(e.toString(), e.getCause());
        }
    }
}