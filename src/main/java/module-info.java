module org.purejava.kwallet {
    requires java.desktop;
    requires org.freedesktop.dbus;
    requires org.slf4j;

    exports org.kde;
    exports org.purejava.kwallet;
    exports org.purejava.kwallet.freedesktop.dbus.handlers;
}