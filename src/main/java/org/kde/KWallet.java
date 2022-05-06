package org.kde;

import org.freedesktop.dbus.annotations.MethodNoReply;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;

import java.util.List;
import java.util.Map;

public interface KWallet extends DBusInterface {

    public static class walletListDirty extends DBusSignal {
        /**
         * A wallet was modified, but not yet saved to disc.
         *
         * @param path The path to the object this is emitted from.
         * @throws DBusException Could not communicate properly with the D-Bus.
         */
        public walletListDirty(String path) throws DBusException {
            super(path);
        }
    }

    public static class walletCreated extends DBusSignal {
        public final String wallet;

        /**
         * A new wallet was created.
         *
         * @param path   The path to the object this is emitted from.
         * @param wallet The wallet that has been created.
         * @throws DBusException Could not communicate properly with the D-Bus.
         */
        public walletCreated(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class walletOpened extends DBusSignal {
        public final String wallet;

        /**
         * A wallet was opened.
         *
         * @param path   The path to the object this is emitted from.
         * @param wallet The wallet that has been opened.
         * @throws DBusException Could not communicate properly with the D-Bus.
         */
        public walletOpened(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class walletAsyncOpened extends DBusSignal {
        public final int tId;
        public final int handle;

        /**
         * A wallet was opened asynchronously.
         *
         * @param path   The path to the object this is emitted from.
         * @param tId    Sequential TransactionID.
         * @param handle Handle to the wallet.
         * @throws DBusException Could not communicate properly with the D-Bus.
         */
        public walletAsyncOpened(String path, int tId, int handle) throws DBusException {
            super(path, tId, handle);
            this.tId = tId;
            this.handle = handle;
        }
    }

    public static class walletDeleted extends DBusSignal {
        public final String wallet;

        /**
         * A wallet was deleted.
         *
         * @param path   The path to the object this is emitted from.
         * @param wallet The wallet that has been deleted.
         * @throws DBusException Could not communicate properly with the D-Bus.
         */
        public walletDeleted(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class walletClosed extends DBusSignal {
        public final String wallet;

        /**
         * A wallet was closed.
         *
         * @param path   The path to the object this is emitted from.
         * @param wallet The wallet that has been closed.
         * @throws DBusException Could not communicate properly with the D-Bus.
         */
        public walletClosed(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class walletClosedId extends DBusSignal {
        public final int handle;

        /**
         * A wallet was closed.
         *
         * @param path   The path to the object this is emitted from.
         * @param handle Handle to the wallet.
         * @throws DBusException Could not communicate properly with the D-Bus.
         */
        public walletClosedId(String path, int handle) throws DBusException {
            super(path, handle);
            this.handle = handle;
        }
    }

    public static class allWalletsClosed extends DBusSignal {
        /**
         * All wallets were closed.
         *
         * @param path The path to the object this is emitted from.
         * @throws DBusException Could not communicate properly with the D-Bus.
         */
        public allWalletsClosed(String path) throws DBusException {
            super(path);
        }
    }

    public static class folderListUpdated extends DBusSignal {
        public final String wallet;

        /**
         * The list of folders contained in a wallet was updated.
         *
         * @param path   The path to the object this is emitted from.
         * @param wallet The wallet in which the list of folders has been updated.
         * @throws DBusException Could not communicate properly with the D-Bus.
         */
        public folderListUpdated(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class folderUpdated extends DBusSignal {
        public final String a;
        public final String b;

        /**
         * The content of a folder in a wallet was updated.
         *
         * @param path The path to the object this is emitted from.
         * @param a    The wallet that contains the folder.
         * @param b    The folder whose content has been updated.
         * @throws DBusException Could not communicate properly with the D-Bus.
         */
        public folderUpdated(String path, String a, String b) throws DBusException {
            super(path, a, b);
            this.a = a;
            this.b = b;
        }
    }

    public static class applicationDisconnected extends DBusSignal {
        public final String wallet;
        public final String application;

        /**
         * An application was disconnected from a wallet.
         *
         * @param path        The path to the object this is emitted from.
         * @param wallet      The wallet the application has been disconnected from.
         * @param application The application that has been disconnected.
         * @throws DBusException Could not communicate properly with the D-Bus.
         */
        public applicationDisconnected(String path, String wallet, String application) throws DBusException {
            super(path, wallet, application);
            this.wallet = wallet;
            this.application = application;
        }
    }

    /**
     * Is kwallet installed and the KDE wallet subsystem enabled?
     *
     * @return Indicator, if the kwallet daemon is up and running and the KDE wallet subsystem is enabled or not.
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
     * Close and lock the wallet. The wallet will only be closed if it is open but not in use (rare), or if it is forced closed.
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
     * @return -1 if wallet does not exist, amount of references to the wallet fom other applications.
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
     * Is the wallet open and unlocked by any application?
     *
     * @param wallet    The wallet to be tested.
     * @return True, if the wallet is open and unlocked, false otherwise.
     */
    abstract public boolean isOpen(String wallet);

    /**
     * Is the wallet open, unlocked and has a valid handle?
     *
     * @param handle    The handle to the wallet to be tested.
     * @return True, if the wallet is open, unlocked and the handle is valid, false otherwise.
     */
    abstract public boolean isOpen(int handle);

    /**
     * List the applications that are using the wallet.
     *
     * @param wallet    The wallet to query.
     * @return A list of all application IDs using the wallet.
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

    /**
     * Determine, if the folder exists in the wallet.
     *
     * @param handle Handle to the wallet to be read from.
     * @param folder Name of the folder.
     * @param appid AppID of the app to access the wallet.
     * @return True if the folder exists, false otherwise.
     */
    abstract public boolean hasFolder(int handle, String folder, String appid);

    /**
     * Create a folder.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Name of the folder.
     * @param appid     AppID of the app to access the wallet.
     * @return True on success, false on error or in case the folder already exists.
     */
    abstract public boolean createFolder(int handle, String folder, String appid);

    /**
     * Delete a folder.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Name of the folder.
     * @param appid AppID of the app to access the wallet.
     * @return True on success, false on error.
     */
    abstract public boolean removeFolder(int handle, String folder, String appid);

    /**
     * Get a list of all the entries (keys) in the given folder.
     *
     * @param handle    Handle to the wallet to read from.
     * @param folder    Name of the folder.
     * @param appid     AppID of the app to access the wallet.
     * @return List of entries (keys) in the folder.
     */
    abstract public List<String> entryList(int handle, String folder, String appid);

    /**
     * Read a secret from the wallet.
     *
     * @param handle    Handle to the wallet to read from.
     * @param folder    Folder that contains the secret.
     * @param key       Identifier for the secret.
     * @param appid     AppID of the app to access the wallet.
     * @return The secret or an array bytes with length 0, in case there is no secret stored for that key.
     */
    abstract public byte[] readEntry(int handle, String folder, String key, String appid);

    /**
     * Read a secret of type map from the wallet.
     *
     * @param handle Handle to the wallet to read from.
     * @param folder Folder that contains the secret.
     * @param key    Identifier for the secret.
     * @param appid  AppID of the app to access the wallet.
     * @return The secret or an array bytes with length 0, in case there is no secret stored for that key.
     */
    abstract public byte[] readMap(int handle, String folder, String key, String appid);

    /**
     * Read a secret of type password from the wallet.
     *
     * @param handle    Handle to the wallet to read from.
     * @param folder    Folder that contains the secret.
     * @param key       Identifier for the secret.
     * @param appid     AppID of the app to access the wallet.
     * @return The secret or an empty String, in case there is no secret stored for that key.
     */
    abstract public String readPassword(int handle, String folder, String key, String appid);

    /*
    abstract public Map<String, Variant> readEntryList(int handle, String folder, String key, String appid);

    abstract public Map<String, Variant> readMapList(int handle, String folder, String key, String appid);

    abstract public Map<String, Variant> readPasswordList(int handle, String folder, String key, String appid);
    */

    /**
     * Get a list of all the secrets in the given folder.
     *
     * @param handle Handle to the wallet to read from.
     * @param folder Folder that contains the secret(s).
     * @param appid  AppID of the app to access the wallet.
     * @return Map of secrets of all types  in the folder.
     */
    abstract public Map<String,byte[]> entriesList(int handle, String folder, String appid);

    /**
     * Get a list of all the secrets of type map in the given folder.
     *
     * @param handle Handle to the wallet to read from.
     * @param folder Folder that contains the secret(s).
     * @param appid  AppID of the app to access the wallet.
     * @return Map of maps in the folder.
     */
    abstract public Map<String, byte[]> mapList(int handle, String folder, String appid);

    /**
     * Get a list of all the secrets of type password in the given folder.
     *
     * @param handle Handle to the wallet to read from.
     * @param folder Folder that contains the secret(s).
     * @param appid  AppID of the app to access the wallet.
     * @return Map of passwords in the folder.
     */
    abstract public Map<String,String> passwordList(int handle, String folder, String appid);

    /**
     * Rename an entry that contains a secret within a folder.
     *
     * @param handle  Handle to the wallet to write to.
     * @param folder  Folder that contains the secret.
     * @param oldName Old name of the entry to be changed.
     * @param newName New name.
     * @param appid   AppID of the app to access the wallet.
     * @return 0 if renaming the entry was successful, -1 otherwise.
     */
    abstract public int renameEntry(int handle, String folder, String oldName, String newName, String appid);

    /**
     * Store a secret in the wallet. An existing secret gets overwritten.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Folder to store the secret in.
     * @param key       Identifier for the secret.
     * @param value     The secret itself.
     * @param entryType An enumerated type representing the type of the entry, e.g. 1 for password, 2 for stream, 3 for map
     * @param appid     AppID of the app to access the wallet.
     * @return 0 if storing the secret was successful, -1 otherwise.
     */
    abstract public int writeEntry(int handle, String folder, String key, byte[] value, int entryType, String appid);

    /**
     * Store a secret of type stream in the wallet. An existing secret gets overwritten.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Folder to store the secret in.
     * @param key       Identifier for the secret.
     * @param value     The secret itself.
     * @param appid     AppID of the app to access the wallet.
     * @return 0 if storing the secret was successful, -1 otherwise.
     */
    abstract public int writeEntry(int handle, String folder, String key, byte[] value, String appid);

    /**
     * Store a secret of type map in the wallet. An existing secret gets overwritten.
     *
     * @param handle Handle to the wallet to write to.
     * @param folder Folder to store the secret in.
     * @param key    Identifier for the secret.
     * @param value  The secret itself.
     * @param appid  AppID of the app to access the wallet.
     * @return 0 if storing the secret was successful, -1 otherwise.
     */
    abstract public int writeMap(int handle, String folder, String key, byte[] value, String appid);

    /**
     * Store a secret of type password in the wallet. An existing secret gets overwritten.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Folder to store the secret in.
     * @param key       Identifier for the secret.
     * @param value     The secret itself.
     * @param appid     AppID of the app to access the wallet.
     * @return 0 if storing the secret was successful, -1 otherwise.
     */
    abstract public int writePassword(int handle, String folder, String key, String value, String appid);

    /**
     * Check whether a folder in a wallet contains an identifier for a secret.
     *
     * @param handle    Handle to the wallet to read from.
     * @param folder    Folder to search.
     * @param key       Identifier for the secret.
     * @param appid     AppID of the app to access the wallet.
     * @return True if the folder contains the key, false otherwise.
     */
    abstract public boolean hasEntry(int handle, String folder, String key, String appid);

    /**
     * Determine the type of the entry key in this folder.
     *
     * @param handle    Handle to the wallet to read from.
     * @param folder    Name of the folder.
     * @param key       Identifier for the secret.
     * @param appid     AppID of the app to access the wallet.
     * @return An enumerated type representing the type of the entry on creation, e.g. 1 for password, 2 for stream, 3 for map, 0 if the key was not found.
     */
    abstract public int entryType(int handle, String folder, String key, String appid);

    /**
     * Delete an identifier for a secret from the folder.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Folder to delete the key from.
     * @param key       Identifier for the secret.
     * @param appid     AppID of the app to access the wallet.
     * @return 0 if deleting the key was successful, -1 in case the wallet does not exist, -3 in case the key does not exist.
     */
    abstract public int removeEntry(int handle, String folder, String key, String appid);

    /**
     * Disconnect the application from wallet.
     *
     * @param wallet      The name of the wallet to disconnect from.
     * @param application The name of the application to disconnect.
     * @return True on success, false on error.
     */
    abstract public boolean disconnectApplication(String wallet, String application);

    /**
     * Read kwalletd configuration and configure daemon accordingly.
     *
     */
    abstract public void reconfigure();

    /**
     * Determine, if the folder does not exist in the wallet.
     *
     * @param wallet The wallet to look into.
     * @param folder The folder to look for.
     * @return True, if the folder does not exist, false otherwise.
     */
    abstract public boolean folderDoesNotExist(String wallet, String folder);

    /**
     * Determine, if the identifier for a secret does not exist in the wallet.
     *
     * @param wallet The wallet to look into.
     * @param folder The folder to look into.
     * @param key    The identifier to look for.
     * @return True, if the identifier does not exist, false otherwise.
     */
    abstract public boolean keyDoesNotExist(String wallet, String folder, String key);

    /**
     * Close all wallets.
     *
     */
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

    /**
     * Open a wallet using a pre-hashed password. This is only useful in cooperation
     * with the kwallet PAM module. It's also less secure than manually entering the
     * password as the password hash is transmitted using D-Bus.
     *
     * @param wallet         The wallet to be opened.
     * @param passwordHash   The pre-hashed password.
     * @param sessionTimeout Timeout after which the wallet gets closed.
     */
    @MethodNoReply
    abstract public void pamOpen(String wallet, byte[] passwordHash, int sessionTimeout);
}