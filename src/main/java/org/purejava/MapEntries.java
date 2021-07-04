package org.purejava;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * KWallet allows to store secrets as byte fields, that contain maps.
 * This is a helper class to manage these maps.
 */
public class MapEntries {

    private Logger log = LoggerFactory.getLogger(MapEntries.class);
    private Map<String, String> map = new HashMap<>();
    private final byte[] EMPTY_ENTRY = new byte[]{-1, -1, -1, -1};
    private final byte[] EMPTY_VALUE = new byte[]{0, 0, 0, 0};

    /**
     * Add or replace an entry to the map.
     *
     * @param key   The key for the entry in the map.
     * @param value The secret to be stored with the key.
     */
    public void storeEntry(String key, String value) {
        map.put(Objects.toString(key, ""), Objects.toString(value, ""));
    }

    /**
     * Delete an entry from the map.
     *
     * @param key   The key for the entry in the map.
     * @param value The secret to be deleted in conjunction with the key.
     */
    public void removeEntry(String key, String value) {
        map.remove(key, value);
    }

    /**
     * Check, whether the map contains an entry with the given key.
     *
     * @param key The key to search for.
     * @return True if the key was found, false otherwise.
     */
    public boolean hasKey(String key) {
        return map.containsKey(key);
    }

    /**
     * Check, whether the map contains an entry with the given key and value.
     *
     * @param key   The key to search for.
     * @param value The value that is stored with that key.
     * @return True if the according entry was found, false otherwise.
     */
    public boolean hasValue(String key, String value) {
        return hasKey(key) && map.get(key).equals(value);
    }

    /**
     * Get the value of an entry for a given key.
     *
     * @param key The key to search for.
     * @return The value if set, an empty String otherwise.
     */
    public String getValue(String key) {
        return hasKey(key) && null != map.get(key) ? map.get(key) : "";
    }

    /**
     * Change the value of an entry in the map.
     *
     * @param key   The key on which the value should be changed.
     * @param value The new value.
     * @return True, if the key exists. The value was changed then. False otherwise.
     */
    public boolean changeValue(String key, String value) {
        if (hasKey(key)) {
            storeEntry(key, value);
            return true;
        }
        return false;
    }

    /**
     * Count number of entries in the map.
     *
     * @return Number or 0 if map is empty.
     */
    public int count() {
        return null != map && !map.isEmpty() ? map.size() : 0;
    }

    /**
     * Take the intern representation of the map entries and convert it to a KWallet map compatible byte field.
     * @see org.purejava.MapEntries#setByteField(byte[])
     *
     * @return The byte field or byte[0] in case something went wrong.
     */
    public byte[] getByteField() {
        if (null == map || map.isEmpty()) return new byte[0];
        try (ByteArrayOutputStream b = new ByteArrayOutputStream();
             DataOutputStream s = new DataOutputStream(b)) {

            s.writeInt(map.size());

            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (null == entry.getKey() || entry.getKey().isEmpty()) {
                    s.write(EMPTY_ENTRY);
                    s.write(EMPTY_ENTRY);

                } else if (null == entry.getValue() || entry.getValue().isEmpty()) {
                    s.writeInt(entry.getKey().length() * 2);
                    s.writeChars(entry.getKey());
                    s.write(EMPTY_VALUE);

                } else {
                    s.writeInt(entry.getKey().length() * 2);
                    s.writeChars(entry.getKey());
                    s.writeInt(entry.getValue().length() * 2);
                    s.writeChars(entry.getValue());
                }
            }
            return b.toByteArray();

        } catch (IOException e) {
            log.error(e.toString(), e.getCause());
            return new byte[0];
        }
    }

    /**
     * Take a KWallet map compatible byte field and store it as a HashMap.
     *
     * @param s The byte field. Format is as follows:
     *          {0,0,0,2,                   // always starts with the number of entries (int) - key/value-combinations
     *                                      // followed by the entries themselves, which are _either_
     *          0,0,0,2,                    // the number of bytes for the key
     *          0,65,                       // the key as bytes
     *          0,0,0,2,                    // the number of bytes for the value
     *          0,66,                       // the value as bytes
     *                                      / note: empty values are 0,0,0,0 instead of number of bytes for the key and value bytes
     *          -1,-1,-1,-1,-1,-1,-1,-1}    // _or_ an empty entry
     * @return True if converting the byte field succeeded without errors, false otherwise.
     */
    public boolean setByteField(byte[] s) {
        map = new HashMap<>();
        if (null == s || s.length == 0) return true;

        try (ByteArrayInputStream b = new ByteArrayInputStream(s);
             DataInputStream x = new DataInputStream(b)) {

            int mapSize = x.readInt();

            for (int i = 0; i < mapSize; i++) {
                // check if the mext part is a number or an EMPTY_ENTRY
                byte[] nextPart = new byte[4];
                for (int k = 0; k < nextPart.length; k++) {
                    nextPart[k] = x.readByte();
                }
                if (Arrays.equals(nextPart, EMPTY_ENTRY)) {
                    map.put("", "");
                    x.skipBytes(4);
                    continue;
                }
                // we have a number
                int keySize = fourBytesToInt(nextPart) / 2;
                StringBuilder k = new StringBuilder();

                for (int j = 0; j < keySize; j++) {
                    k.append(x.readChar());
                }

                // check if the next part is a number or an EMPTY_VALUE
                nextPart = new byte[4];
                for (int l = 0; l < nextPart.length; l++) {
                    nextPart[l] = x.readByte();
                }
                if (Arrays.equals(nextPart, EMPTY_VALUE)) {
                    map.put(k.toString(), "");
                    continue;
                }
                int valueSize = fourBytesToInt(nextPart) / 2;
                StringBuilder v = new StringBuilder();
                for (int j = 0; j < valueSize; j++) {
                    v.append(x.readChar());
                }
                map.put(k.toString(), v.toString());
            }
            return true;

        } catch (IOException e) {
            log.error(e.toString(), e.getCause());
            return false;
        }
    }

    private int fourBytesToInt(byte[] b) {
        return ((b[0] << 24) + (b[1] << 16) + (b[2] << 8) + (b[3] << 0));
    }

    @Override
    public String toString() {
        StringBuilder sout = new StringBuilder();
        int i = 1;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key.isEmpty()) key = "''";
            String value = entry.getValue();
            if (value.isEmpty()) value = "''";
            sout.append("MapEntries (").append(i).append(") {key: ").append(key).append(", value: ").append(value).append("}\n");
            i++;
        }
        return sout.substring(0, sout.toString().length()-1);
    }
}
