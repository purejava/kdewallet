package org.freedesktop.dbus.handlers;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.messages.DBusSignal;
import org.kde.KWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SignalHandler implements DBusSigHandler {

    private static SignalHandler instance = new SignalHandler();

    private Logger log = LoggerFactory.getLogger(SignalHandler.class);

    private PropertyChangeSupport support;
    private DBusConnection connection = null;
    private List<Class<? extends DBusSignal>> registered = new ArrayList();
    private DBusSignal[] handled = new DBusSignal[250];
    private int count = 0;

    private SignalHandler() {
        support = new PropertyChangeSupport(this);
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                disconnect()
        ));
    }

    public static SignalHandler getInstance() {
        return instance;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void connect(DBusConnection connection, List<Class<? extends DBusSignal>> signals) {
        if (this.connection == null) {
            this.connection = connection;
        }
        if (signals != null) {
            try {
                for (Class sc : signals) {
                    if (!registered.contains(sc)) {
                        connection.addSigHandler(sc, this);
                        this.registered.add(sc);
                    }
                }
            } catch (DBusException e) {
                log.error(e.toString(), e.getCause());
            }
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                log.debug("remove signal handlers");
                for (Class sc : registered) {
                    if (connection.isConnected()) {
                        log.trace("remove signal handler: " + sc.getName());
                        connection.removeSigHandler(sc, this);
                    }
                }
            } catch (DBusException e) {
                log.error(e.toString(), e.getCause());
            }
        }
    }

    @Override
    public void handle(DBusSignal s) {

        Collections.rotate(Arrays.asList(handled), 1);
        handled[0] = s;
        count += 1;

        if (s instanceof KWallet.walletOpened) {
            KWallet.walletOpened wo = (KWallet.walletOpened) s;
            support.firePropertyChange("KWallet.walletOpened", null, wo.wallet);
            log.info("Received signal KWallet.walletOpened: " + wo.wallet);
        } else if (s instanceof KWallet.walletAsyncOpened) {
            KWallet.walletAsyncOpened wo = (KWallet.walletAsyncOpened) s;
            support.firePropertyChange("KWallet.walletAsyncOpened", null, wo.handle);
            log.info("Received signal KWallet.walletAsyncOpened: {TransactionID: " + wo.tId + ", handle: " + wo.handle + "}");
        } else if (s instanceof KWallet.walletDeleted) {
            KWallet.walletDeleted wd = (KWallet.walletDeleted) s;
            support.firePropertyChange("KWallet.walletDeleted", null, wd.wallet);
            log.info("Received signal KWallet.walletDeleted: " + wd.wallet);
        } else if (s instanceof KWallet.walletClosedInt) {
            KWallet.walletClosedInt wc = (KWallet.walletClosedInt) s;
            support.firePropertyChange("KWallet.walletClosedInt", null, wc.handle);
            log.info("Received signal KWallet.walletClosedInt: " + wc.handle);
        } else if (s instanceof KWallet.walletClosed) {
            KWallet.walletClosed wc = (KWallet.walletClosed) s;
            support.firePropertyChange("KWallet.walletClosed", null, wc.wallet);
            log.info("Received signal KWallet.walletClosed: " + wc.wallet);
        } else if (s instanceof KWallet.allWalletsClosed) {
            support.firePropertyChange("KWallet.allWalletsClosed", null, s.getPath());
            log.info("Received signal KWallet.allWalletsClosed: " + s.getPath());
        } else if (s instanceof KWallet.folderListUpdated) {
            KWallet.folderListUpdated flu = (KWallet.folderListUpdated) s;
            support.firePropertyChange("KWallet.folderListUpdated", null, flu.wallet);
            log.info("Received signal KWallet.folderListUpdated: " + flu.wallet);
        } else if (s instanceof KWallet.folderUpdated) {
            KWallet.folderUpdated fu = (KWallet.folderUpdated) s;
            support.firePropertyChange("KWallet.folderUpdated", null, fu.a + "/" + fu.b);
            log.info("Received signal KWallet.folderUpdated: {wallet: " + fu.a + ", folder: " + fu.b + "}");
        } else if (s instanceof KWallet.applicationDisconnected) {
            KWallet.applicationDisconnected ad = (KWallet.applicationDisconnected) s;
            support.firePropertyChange("KWallet.applicationDisconnected", null, ad.application + "/" + ad.wallet);
            log.info("Received signal KWallet.applicationDisconnected: {application: " + ad.application + ", wallet: " + ad.wallet + "}");
        } else if (s instanceof KWallet.walletListDirty) {
            support.firePropertyChange("KWallet.walletListDirty", null, s.getPath());
            log.debug("Received signal KWallet.walletListDirty: " + s.getPath());
        } else if (s instanceof KWallet.walletCreated) {
            KWallet.walletCreated wc = (KWallet.walletCreated) s;
            support.firePropertyChange("KWallet.walletCreated", null, wc.wallet);
            log.info("Received signal KWallet.walletCreated: " + wc.wallet);
        } else {
            log.warn("Handled unknown signal: " + s.getClass().toString() + " {" + s.toString() + "}");
        }
    }

    public DBusSignal[] getHandledSignals() {
        return handled;
    }

    public <S extends DBusSignal> List<S> getHandledSignals(Class<S> s) {
        return Arrays.stream(handled)
                .filter(signal -> signal != null)
                .filter(signal -> signal.getClass().equals(s))
                .map(signal -> (S) signal)
                .collect(Collectors.toList());
    }

    public <S extends DBusSignal> List<S> getHandledSignals(Class<S> s, String path) {
        return Arrays.stream(handled)
                .filter(signal -> signal != null)
                .filter(signal -> signal.getClass().equals(s))
                .filter(signal -> signal.getPath().equals(path))
                .map(signal -> (S) signal)
                .collect(Collectors.toList());
    }

    public int getCount() {
        return count;
    }

    public DBusSignal getLastHandledSignal() {
        return handled.length > 0 ? handled[0] : null;
    }

    public <S extends DBusSignal> S getLastHandledSignal(Class<S> s) {
        return !getHandledSignals(s).isEmpty() ? getHandledSignals(s).get(0) : null;
    }

    public <S extends DBusSignal> S getLastHandledSignal(Class<S> s, String path) {
        return !getHandledSignals(s, path).isEmpty() ? getHandledSignals(s, path).get(0) : null;
    }
}
