# kdewallet
![KWallet](KWallet.png)

[![Java CI with Maven](https://github.com/purejava/kdewallet/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/purejava/kdewallet/actions?query=workflow%3A%22Java+CI+with+Maven%22)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/b1379afd2db3447abfbdca82fbdc2b7a)](https://www.codacy.com/gh/purejava/kdewallet/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=purejava/kdewallet&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://img.shields.io/maven-central/v/org.purejava/kdewallet.svg?label=Maven%20Central)](https://central.sonatype.com/search?q=kdewallet&smo=true&namespace=org.purejava)
[![License](https://img.shields.io/github/license/purejava/kdewallet.svg)](https://github.com/purejava/kdewallet/blob/master/LICENSE)

A Java library for storing secrets on linux in a KDE wallet over D-Bus.

The KDE wallet functionality itself is provided by the kwallet daemon [kwalletd](https://github.com/KDE/kwallet/tree/master/src/runtime/kwalletd).

## Usage
The library provides an API, which sends secrets over D-Bus and has D-Bus signaling enabled.

### Dependency
Add `kdewallet` as a dependency to your project.
```maven
<dependency>
    <groupId>org.purejava</groupId>
    <artifactId>kdewallet</artifactId>
    <version>1.3.0</version>
</dependency>
```

### Accessing the kwallet daemon
Creating a folder in a wallet can be done like this:
```java
package org.example;

import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.kde.KWallet;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        DBusConnection connection = null;

        try {
            connection = DBusConnectionBuilder.forSessionBus().withShared(false).build();

            var service = connection.getRemoteObject("org.kde.kwalletd5",
                    "/modules/kwalletd5", KWallet.class);

            var wallet = "kdewallet";
            var wId = 0;
            var appid = "Tester";
            var handle = service.open(wallet, wId, appid);
            var folder = "Test-Folder";
            var created = service.createFolder(handle, folder, appid);
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

### Signal handling
D-Bus signals are emitted on every change to a wallet, e.g. when it's opened asynchronously, closed etc.
Listening to these signals can be done asynchronously with few effort.

For the complete API and examples see the [Wiki](https://github.com/purejava/kdewallet/wiki/Home).

## Thank you
Thanks to David M., who wrote an improved version of [Java DBus](https://github.com/hypfvieh/dbus-java) library provided by freedesktop.org.
Thanks to Sebastian Wiesendahl, who implemented the original core messaging interface to DBus in his [secret-service](https://github.com/swiesend/secret-service) library.

## Copyright
Copyright (C) 2020-2023 Ralph Plawetzki
