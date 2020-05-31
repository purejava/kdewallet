package org.purejava;

import org.freedesktop.dbus.Static;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KDEWalletTest
{
    private final Logger log = LoggerFactory.getLogger(KDEWalletTest.class);
    private Context context;

    @BeforeEach
    public void beforeEach(TestInfo info) {
        log.info(info.getDisplayName());
        context = new Context(log);
        context.ensureService();
    }

    @AfterEach
    public void afterEach() {
        context.after();
    }

    @Test
    public void wallets() {
        KDEWallet kwallet = new KDEWallet(context.connection);
        Object[] response = kwallet.send("wallets");
        assertEquals(1, response.length);
        String walletName = (String) response[0];
        assertEquals(walletName, Static.DEFAULT_WALLET);
        List<String> walletList = Arrays.asList((String)response[0]);
        log.info(walletList.get(0));
    }
}