package org.freedesktop.dbus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Static {

    public static final String DEFAULT_WALLET = "kdewallet";

    public static class DBus {
        public static class Service {
            public static final String DBUS = "org.freedesktop.DBus";
        }

        public static class ObjectPaths {
            public static final String DBUS = "/org/freedesktop/DBus";
        }

        public static class Interfaces {
            public static final String DBUS = "org.freedesktop.DBus";
            public static final String DBUS_PROPERTIES = "org.freedesktop.DBus.Properties";
        }
    }

    public static class Service {
        public static final String SECRETS = "org.kde.kwalletd5";
    }

    public static class ObjectPaths {
        public static final String SECRETS = "/modules/kwalletd5";
    }

    public static class Interfaces {
        public static final String WALLET = "org.kde.KWallet";
    }

    public static class Algorithm {
        public static final String PLAIN = "plain";
        public static final String DH_IETF1024_SHA256_AES128_CBC_PKCS7 = "dh-ietf1024-sha256-aes128-cbc-pkcs7";
        public static final String DIFFIE_HELLMAN = "DH";
        public static final String AES = "AES";
        public static final String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";
        public static final String SHA1_PRNG = "SHA1PRNG";
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

    /**
     * RFC 2409: https://tools.ietf.org/html/rfc2409
     */
    public static class RFC_2409 {

        /**
         * RFC 2409: https://tools.ietf.org/html/rfc2409#section-6.2
         */
        public static class SecondOakleyGroup {

            public static final byte[] PRIME = new byte[]{
                    (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    (byte) 0xC9, (byte) 0x0F, (byte) 0xDA, (byte) 0xA2, (byte) 0x21, (byte) 0x68, (byte) 0xC2, (byte) 0x34,
                    (byte) 0xC4, (byte) 0xC6, (byte) 0x62, (byte) 0x8B, (byte) 0x80, (byte) 0xDC, (byte) 0x1C, (byte) 0xD1,
                    (byte) 0x29, (byte) 0x02, (byte) 0x4E, (byte) 0x08, (byte) 0x8A, (byte) 0x67, (byte) 0xCC, (byte) 0x74,
                    (byte) 0x02, (byte) 0x0B, (byte) 0xBE, (byte) 0xA6, (byte) 0x3B, (byte) 0x13, (byte) 0x9B, (byte) 0x22,
                    (byte) 0x51, (byte) 0x4A, (byte) 0x08, (byte) 0x79, (byte) 0x8E, (byte) 0x34, (byte) 0x04, (byte) 0xDD,
                    (byte) 0xEF, (byte) 0x95, (byte) 0x19, (byte) 0xB3, (byte) 0xCD, (byte) 0x3A, (byte) 0x43, (byte) 0x1B,
                    (byte) 0x30, (byte) 0x2B, (byte) 0x0A, (byte) 0x6D, (byte) 0xF2, (byte) 0x5F, (byte) 0x14, (byte) 0x37,
                    (byte) 0x4F, (byte) 0xE1, (byte) 0x35, (byte) 0x6D, (byte) 0x6D, (byte) 0x51, (byte) 0xC2, (byte) 0x45,
                    (byte) 0xE4, (byte) 0x85, (byte) 0xB5, (byte) 0x76, (byte) 0x62, (byte) 0x5E, (byte) 0x7E, (byte) 0xC6,
                    (byte) 0xF4, (byte) 0x4C, (byte) 0x42, (byte) 0xE9, (byte) 0xA6, (byte) 0x37, (byte) 0xED, (byte) 0x6B,
                    (byte) 0x0B, (byte) 0xFF, (byte) 0x5C, (byte) 0xB6, (byte) 0xF4, (byte) 0x06, (byte) 0xB7, (byte) 0xED,
                    (byte) 0xEE, (byte) 0x38, (byte) 0x6B, (byte) 0xFB, (byte) 0x5A, (byte) 0x89, (byte) 0x9F, (byte) 0xA5,
                    (byte) 0xAE, (byte) 0x9F, (byte) 0x24, (byte) 0x11, (byte) 0x7C, (byte) 0x4B, (byte) 0x1F, (byte) 0xE6,
                    (byte) 0x49, (byte) 0x28, (byte) 0x66, (byte) 0x51, (byte) 0xEC, (byte) 0xE6, (byte) 0x53, (byte) 0x81,
                    (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            };

            public static final byte[] GENERATOR = new byte[]{(byte) 0x02};

        }

    }

}