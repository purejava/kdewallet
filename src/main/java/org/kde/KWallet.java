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

    /**
     * Is kwallet installed?
     *
     * @return Indicator, if the kwallet daemon is up and running or not.
     */
    abstract public boolean isEnabled();

    /**
     * Open and unlock the wallet.
     *
     * @param wallet The wallet to be opened and unlocked.
     * @param wId    The window id to associate any dialogs with. You can pass 0 if you don't have a window the password dialog should associate with.
     * @param appid  The application that accesses the wallet.
     * @return Handle to the wallet or -1, if opening fails.
     */
    abstract public int open(String wallet, long wId, String appid);

    /**
     * Open and unlock the wallet with this path.
     *
     * @param path  Path to the wallet.
     * @param wId   The window id to associate any dialogs with. You can pass 0 if you don't have a window the password dialog should associate with.
     * @param appid The application that accesses the wallet.
     * @return Always 0.
     */
    abstract public int openPath(String path, long wId, String appid);

    /**
     * Open the wallet asynchronously.
     *
     * @param wallet        The wallet to be opened.
     * @param wId           The window id to associate any dialogs with. You can pass 0 if you don't have a window the password dialog should associate with.
     * @param appid         The application that accesses the wallet.
     * @param handleSession Handle session with kwalletsessionstore or not.
     * @return Sequential TransactionID.
     */
    abstract public int openAsync(String wallet, long wId, String appid, boolean handleSession);

    /**
     * Open and unlock the wallet with this path asynchronously.
     *
     * @param path          Path to the wallet.
     * @param wId           The window id to associate any dialogs with. You can pass 0 if you don't have a window the password dialog should associate with.
     * @param appid         The application that accesses the wallet.
     * @param handleSession Handle session with kwalletsessionstore or not.
     * @return Sequential TransactionID.
     */
    abstract public int openPathAsync(String path, long wId, String appid, boolean handleSession);

    /**
     * Close and lock the wallet.
     * <p>
     * If force = true, will close it for all users.  Behave.  This
     * can break applications, and is generally intended for use by
     * the wallet manager app only.
     *
     * @param wallet The wallet to be closed and locked.
     * @param force  Forced closing or not.
     * @return -1 if wallet does not exist, 0 if all references fom applications to the wallet have been removed.
     */
    abstract public int close(String wallet, boolean force);

    /**
     * Close and lock the wallet.
     * <p>
     * If force = true, will close it for all users.  Behave.  This
     * can break applications, and is generally intended for use by
     * the wallet manager app only.
     *
     * @param handle Handle to the wallet to be closed and locked.
     * @param force  Forced closing or not.
     * @param appid  AppID of the app to access the wallet.
     * @return -1 if wallet does not exist, 0 if all references fom applications to the wallet have been removed.
     */
    abstract public int close(int handle, boolean force, String appid);

    /**
     * Save to disk but leave open.
     *
     * @param handle Handle to the wallet to be saved.
     * @param appid  AppID of the app to access the wallet.
     */
    @MethodNoReply
    abstract public void sync(int handle, String appid);

    /**
     * Physically deletes the wallet from disk.
     *
     * @param wallet The wallet to be deleted.
     * @return -1 if something went wrong, 0 if the wallet could be deleted.
     */
    abstract public int deleteWallet(String wallet);

    /**
     * ???
     *
     * @param wallet    The wallet to be tested.
     * @return ??? or not.
     */
    abstract public boolean isOpen(String wallet);

    /**
     * ???
     *
     * @param handle    The handle to the wallet to be tested.
     * @return ??? or not.
     */
    abstract public boolean isOpen(int handle);

    /**
     * List the applications that are using the wallet.
     *
     * @param wallet    The wallet to query.
     * @return Returns a list of all application IDs using the wallet.
     */
    abstract public List<String> users(String wallet);

    /**
     * Request to the wallet service to change the password of the wallet.
     *
     * @param wallet    The wallet to change the password of.
     * @param wId       The window id to associate any dialogs with. You can pass 0 if you don't have a window the password dialog should associate with.
     * @param appid     AppID of the app to access the wallet.
     */
    abstract public void changePassword(String wallet, long wId, String appid);

    /**
     * List of all wallets.
     *
     * @return List of wallets available.
     */
    abstract public List<String> wallets();

    /**
     * Obtain the list of all folders contained in the wallet.
     *
     * @param handle    Handle to the wallet to be read from.
     * @param appid     AppID of the app to access the wallet.
     * @return List of folders in that wallet.
     */
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

    /**
     * Disconnect the application from wallet.
     *
     * @param wallet      The name of the wallet to disconnect from.
     * @param application The name of the application to be disconnected.
     * @return Returns true on success, false on error.
     */
    abstract public boolean disconnectApplication(String wallet, String application);

    abstract public void reconfigure();

    abstract public boolean folderDoesNotExist(String wallet, String folder);

    abstract public boolean keyDoesNotExist(String wallet, String folder, String key);

    abstract public void closeAllWallets();

    /**
     * The name of the wallet used to store network passwords.
     *
     * @return Name of the wallet.
     */
    abstract public String networkWallet();

    /**
     * The name of the wallet used to store local passwords.
     *
     * @return Name of the wallet.
     */
    abstract public String localWallet();

    @MethodNoReply
    abstract public void pamOpen(String wallet, List<Byte> passwordHash, int sessionTimeout);

}