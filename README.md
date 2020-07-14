# kdewallet
[![Java CI with Maven](https://github.com/purejava/kdewallet/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/purejava/kdewallet/actions?query=workflow%3A%22Java+CI+with+Maven%22)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/da634cf61b71475293312f9bfadafde7)](https://www.codacy.com/manual/purejava/kdewallet?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=purejava/kdewallet&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://img.shields.io/maven-central/v/org.purejava/kdewallet.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.purejava%22%20AND%20a:%22kdewallet%22)
[![License](https://img.shields.io/github/license/purejava/kdewallet.svg)](https://github.com/purejava/kdewallet/blob/master/LICENSE)

A Java library for storing secrets on linux in a KDE wallet over D-Bus.

The KDE wallet functionality itself is provided by the kwallet daemon [kwalletd](https://github.com/KDE/kwallet/tree/master/src/runtime/kwalletd).

# Usage
The library provides an API, which sends secrets over D-Bus and has D-Bus signaling enabled.

## Dependency
Add `kdewallet` as a dependency to your project.
```maven
<dependency>
    <groupId>org.purejava</groupId>
    <artifactId>kdewallet</artifactId>
    <version>1.0.0-RC.1</version>
</dependency>
```

## Accessing the kwallet daemon
Creating a folder in a wallet can be done like this:
```java
package org.example;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.kde.KWallet;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        DBusConnection connection = null;

        try {
            connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);

            KWallet service = connection.getRemoteObject("org.kde.kwalletd5",
                    "/modules/kwalletd5", KWallet.class);

            String wallet = "kdewallet";
            int wId = 0;
            String appid = "Tester";
            int handle = service.open(wallet, wId, appid);
            String folder = "Test-Folder";
            boolean created = service.createFolder(handle, folder, appid);
            service.close(handle, false, appid);
        } catch (DBusException e) {
            System.out.println(e.toString() + e.getCause());
        }
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

Fetching and handling of D-Bus signals can be done with the `await` method.
```java
wallet.openAsync(Static.DEFAULT_WALLET, 0, APP_NAME, false);
wallet.getSignalHandler().await(KWallet.walletAsyncOpened.class, Static.ObjectPaths.SECRETS, () -> {
    return null;
});
handle = wallet.getSignalHandler().getLastHandledSignal(KWallet.walletAsyncOpened.class, Static.ObjectPaths.SECRETS).handle;
```
Please note, that order is important here: you cannot use the `getLastHandledSignal` method before the `await` method.

The complete API is documented in the [Wiki](https://github.com/purejava/kdewallet/wiki/Home).

# To Do
The handling of D-Bus signals needs some improvements and robustness added.

# Thank you
Thanks to Sebastian Wiesendahl, how implemented the original core messaging interface to DBus in his [secret-service](https://github.com/swiesend/secret-service) library.

# Copyright
Copyright (C) 2020 Ralph Plawetzki