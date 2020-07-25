package org.kde;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.ObjectPath;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Static {

    public static final String DEFAULT_WALLET = "kdewallet";

    public static class DBus {
        public static class Interfaces {
            public static final String DBUS_PROPERTIES = "org.freedesktop.DBus.Properties";
        }
    }

    public static class Service {
        public static final String KWALLETD5 = "org.kde.kwalletd5";
    }

    public static class ObjectPaths {
        public static final String KWALLETD5 = "/modules/kwalletd5";
    }

    public static class Interfaces {
        public static final String KWALLET = "org.kde.KWallet";
    }

    public static class Convert {

        public static byte[] toByteArray(List<Byte> list) {
            byte[] result = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = list.get(i).byteValue();
            }
            return result;
        }

        public static String toString(byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }

        public static ObjectPath toObjectPath(String path) {
            return new ObjectPath("", path);
        }

        public static List<String> toStrings(List<ObjectPath> paths) {
            ArrayList<String> ps = new ArrayList();
            for (ObjectPath p : paths) {
                ps.add(p.getPath());
            }
            return ps;
        }

        public static List<DBusPath> toDBusPaths(List<ObjectPath> paths) {
            ArrayList<DBusPath> ps = new ArrayList();
            for (ObjectPath p : paths) {
                ps.add(p);
            }
            return ps;
        }
    }
}