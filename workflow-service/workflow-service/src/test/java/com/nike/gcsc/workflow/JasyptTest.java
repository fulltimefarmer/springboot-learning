 package com.nike.gcsc.workflow;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author roger yang
 * @date 8/06/2019
 */
@Slf4j
public class JasyptTest {
    @Test
    public void test() {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("yR2Fs51GEU73rZ5L");
        String text = textEncryptor.encrypt("7yonN2NzzzTeuQI5");
        log.info(text);
    }
}
