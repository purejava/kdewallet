package org.purejava.kwallet.freedesktop.dbus.handlers;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.messages.DBusSignal;
import org.purejava.kwallet.KWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SignalHandler implements DBusSigHandler {

    private static SignalHandler instance = new SignalHandler();

    private static final Logger LOG = LoggerFactory.getLogger(SignalHandler.class);

    private PropertyChangeSupport support;
    private DBusConnection connection = null;
    private List<Class<? extends DBusSignal>> registered = new ArrayList();
    private DBusSignal[] handled = new DBusSignal[250];
    private int count = 0;

    private SignalHandler() {
        support = new PropertyChangeSupport(this);
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
        if (null == this.connection) {
            this.connection = connection;
        }
        if (signals != null) {
            try {
                for (Class sc : signals) {
                    if (!registered.contains(sc)) {
                        connection.addSigHandler(sc, this);
                        registered.add(sc);
                    }
                }
            } catch (DBusException e) {
                LOG.error(e.toString(), e.getCause());
            }
        }
    }

    @Override
    public void handle(DBusSignal s) {

        Collections.rotate(Arrays.asList(handled), 1);
        handled[0] = s;
        count += 1;

        if (s instanceof KWallet.walletOpened wo) {
            support.firePropertyChange("KWallet.walletOpened", null, wo.wallet);
            LOG.info("Received signal KWallet.walletOpened: {}", wo.wallet);
        } else if (s instanceof KWallet.walletAsyncOpened wo) {
            support.firePropertyChange("KWallet.walletAsyncOpened", null, wo.handle);
            LOG.info("Received signal KWallet.walletAsyncOpened: {TransactionID: {}, handle: {}}", wo.tId, wo.handle);
        } else if (s instanceof KWallet.walletDeleted wd) {
            support.firePropertyChange("KWallet.walletDeleted", null, wd.wallet);
            LOG.info("Received signal KWallet.walletDeleted: {}", wd.wallet);
        } else if (s instanceof KWallet.walletClosedId wc) {
            support.firePropertyChange("KWallet.walletClosedId", null, wc.handle);
            LOG.info("Received signal KWallet.walletClosedId: {}", wc.handle);
        } else if (s instanceof KWallet.walletClosed wc) {
            support.firePropertyChange("KWallet.walletClosed", null, wc.wallet);
            LOG.info("Received signal KWallet.walletClosed: {}", wc.wallet);
        } else if (s instanceof KWallet.allWalletsClosed) {
            support.firePropertyChange("KWallet.allWalletsClosed", null, s.getPath());
            LOG.info("Received signal KWallet.allWalletsClosed: {}", s.getPath());
        } else if (s instanceof KWallet.folderListUpdated flu) {
            support.firePropertyChange("KWallet.folderListUpdated", null, flu.wallet);
            LOG.info("Received signal KWallet.folderListUpdated: {}", flu.wallet);
        } else if (s instanceof KWallet.folderUpdated fu) {
            support.firePropertyChange("KWallet.folderUpdated", null, fu.a + "/" + fu.b);
            LOG.info("Received signal KWallet.folderUpdated: {wallet: {}, folder: {}}", fu.a, fu.b);
        } else if (s instanceof KWallet.applicationDisconnected ad) {
            support.firePropertyChange("KWallet.applicationDisconnected", null, ad.application + "/" + ad.wallet);
            LOG.info("Received signal KWallet.applicationDisconnected: {application: {}, wallet: {}}", ad.application, ad.wallet);
        } else if (s instanceof KWallet.walletListDirty) {
            support.firePropertyChange("KWallet.walletListDirty", null, s.getPath());
            LOG.debug("Received signal KWallet.walletListDirty: {}", s.getPath());
        } else if (s instanceof KWallet.walletCreated wc) {
            support.firePropertyChange("KWallet.walletCreated", null, wc.wallet);
            LOG.info("Received signal KWallet.walletCreated: {}", wc.wallet);
        } else {
            LOG.warn("Received unknown signal: {} {{}}", s.getClass(), s);
        }
    }

    public DBusSignal[] getHandledSignals() {
        return handled;
    }

    public <S extends DBusSignal> List<S> getHandledSignals(Class<S> s) {
        return Arrays.stream(handled)
                .filter(Objects::nonNull)
                .filter(signal -> signal.getClass().equals(s))
                .map(signal -> (S) signal)
                .collect(Collectors.toList());
    }

    public <S extends DBusSignal> List<S> getHandledSignals(Class<S> s, String path) {
        return Arrays.stream(handled)
                .filter(Objects::nonNull)
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
        return getHandledSignals(s).isEmpty() ? null : getHandledSignals(s).get(0);
    }

    public <S extends DBusSignal> S getLastHandledSignal(Class<S> s, String path) {
        return getHandledSignals(s, path).isEmpty() ? null : getHandledSignals(s, path).get(0);
    }

    /**
     * @deprecated Please use {@link #addPropertyChangeListener(PropertyChangeListener)} instead
     */
    @Deprecated
    public <S extends DBusSignal> S await(Class<S> s, String path, Callable action) {
        final Duration timeout = Duration.ofSeconds(120);
        return await(s, path, action, timeout);
    }

    /**
     * @deprecated Please use {@link #addPropertyChangeListener(PropertyChangeListener)} instead
     */
    @Deprecated
    public <S extends DBusSignal> S await(Class<S> s, String path, Callable action, Duration timeout) {
        final int init = getCount();

        try {
            action.call();
        } catch (Exception e) {
            LOG.error(e.toString(), e.getCause());
        }

        var executor = Executors.newSingleThreadExecutor();

        LOG.info("Await signal {}" + "({}) within {} seconds.", s.getName(), path, timeout.getSeconds());

        final Future<S> handler = executor.submit((Callable) () -> {
            var await = init;
            List<S> signals = null;
            while (await == init) {
                if (Thread.currentThread().isInterrupted()) return null;
                Thread.currentThread().sleep(50L);
                signals = getHandledSignals(s, path);
                await = getCount();
            }
            if (!signals.isEmpty()) {
                return signals.get(0);
            } else {
                return null;
            }
        });

        try {
            return handler.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            handler.cancel(true);
            LOG.warn(e.toString(), e.getCause());
        } finally {
            executor.shutdownNow();
        }

        return null;
    }
}
