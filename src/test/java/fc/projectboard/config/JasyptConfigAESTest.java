package fc.projectboard.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.jupiter.api.Test;

class JasyptConfigAESTest {

    @Test
    void stringEncryptor() {
        String url = "db_url";
        String username = "db_username";
        String password = "";
//
//        System.out.println(jasyptEncoding(url));
//        System.out.println(jasyptEncoding(username));
        System.out.println(jasyptEncoding(password));
    }

    public String jasyptEncoding(String value) {

        String key = "sdjf3hi6uwhr7ui3hrui3";
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        pbeEnc.setPassword(key);
        pbeEnc.setIvGenerator(new RandomIvGenerator());
        return pbeEnc.encrypt(value);
    }
}