package org.kde;

import org.freedesktop.dbus.Static;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.annotations.MethodNoReply;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.AbstractInterface;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;

import java.util.List;

@DBusInterfaceName(Static.Interfaces.WALLET)
public abstract class KWallet extends AbstractInterface implements DBusInterface {

    public KWallet(DBusConnection connection, List<Class<? extends DBusSignal>> signals,
                   String serviceName, String objectPath, String interfaceName) {
        super(connection, signals, serviceName, objectPath, interfaceName);
    }

    public static class WalletListDirty extends DBusSignal {
        public WalletListDirty(String path) throws DBusException {
            super(path);
        }
    }

    public static class WalletCreated extends DBusSignal {
        public final String wallet;

        public WalletCreated(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class WalletOpened extends DBusSignal {
        public final String wallet;

        public WalletOpened(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class WalletAsyncOpened extends DBusSignal {
        public final int tId;
        public final int handle;

        public WalletAsyncOpened(String path, int tId, int handle) throws DBusException {
            super(path, tId, handle);
            this.tId = tId;
            this.handle = handle;
        }
    }

    public static class WalletDeleted extends DBusSignal {
        public final String wallet;

        public WalletDeleted(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class WalletClosed extends DBusSignal {
        public final String wallet;

        public WalletClosed(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class AllWalletsClosed extends DBusSignal {
        public AllWalletsClosed(String path) throws DBusException {
            super(path);
        }
    }

    public static class FolderListUpdated extends DBusSignal {
        public final String wallet;

        public FolderListUpdated(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class FolderUpdated extends DBusSignal {
        public final String a;
        public final String b;

        public FolderUpdated(String path, String a, String b) throws DBusException {
            super(path, a, b);
            this.a = a;
            this.b = b;
        }
    }

    public static class ApplicationDisconnected extends DBusSignal {
        public final String wallet;
        public final String application;

        public ApplicationDisconnected(String path, String wallet, String application) throws DBusException {
            super(path, wallet, application);
            this.wallet = wallet;
            this.application = application;
        }
    }

    abstract public boolean isEnabled();

    abstract public int open(String wallet, long wId, String appid);

    abstract public int openPath(String path, long wId, String appid);

    abstract public int openAsync(String wallet, long wId, String appid, boolean handleSession);

    abstract public int openPathAsync(String path, long wId, String appid, boolean handleSession);

    abstract public int close(String wallet, boolean force);

    abstract public int close(int handle, boolean force, String appid);

    @MethodNoReply
    abstract public void sync(int handle, String appid);

    abstract public int deleteWallet(String wallet);

    abstract public boolean isOpen(String wallet);

    abstract public boolean isOpen(int handle);

    abstract public List<String> users(String wallet);

    abstract public void changePassword(String wallet, long wId, String appid);

    abstract public List<String> wallets();

    abstract public List<String> folderList(int handle, String appid);

    abstract public boolean hasFolder(int handle, String folder, String appid);

    abstract public boolean createFolder(int handle, String folder, String appid);

    abstract public boolean removeFolder(int handle, String folder, String appid);

    abstract public List<String> entryList(int handle, String folder, String appid);

    abstract public List<Byte> readEntry(int handle, String folder, String key, String appid);

    abstract public List<Byte> readMap(int handle, String folder, String key, String appid);

    abstract public String readPassword(int handle, String folder, String key, String appid);

    /*
    abstract public Map<String, Variant> readEntryList(int handle, String folder, String key, String appid);

    abstract public Map<String, Variant> readMapList(int handle, String folder, String key, String appid);

    abstract public Map<String, Variant> readPasswordList(int handle, String folder, String key, String appid);
    */

    abstract public int renameEntry(int handle, String folder, String oldName, String newName, String appid);

    abstract public int writeEntry(int handle, String folder, String key, List<Byte> value, int entryType, String appid);

    abstract public int writeEntry(int handle, String folder, String key, List<Byte> value, String appid);

    abstract public int writeMap(int handle, String folder, String key, List<Byte> value, String appid);

    abstract public int writePassword(int handle, String folder, String key, String value, String appid);

    abstract public boolean hasEntry(int handle, String folder, String key, String appid);

    abstract public int entryType(int handle, String folder, String key, String appid);

    abstract public int removeEntry(int handle, String folder, String key, String appid);

    abstract public boolean disconnectApplication(String wallet, String application);

    abstract public void reconfigure();

    abstract public boolean folderDoesNotExist(String wallet, String folder);

    abstract public boolean keyDoesNotExist(String wallet, String folder, String key);

    abstract public void closeAllWallets();

    abstract public String networkWallet();

    abstract public String localWallet();

    @MethodNoReply
    abstract public void pamOpen(String wallet, List<Byte> passwordHash, int sessionTimeout);

}