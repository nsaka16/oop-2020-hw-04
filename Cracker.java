// Cracker.java
/*
 Generates SHA hashes of short strings in parallel.
*/

import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Cracker {


    // Array of chars used to produce strings
    public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();
    private CountDownLatch countDownLatch;



    /*
     Given a byte[] array, produces a hex String,
     such as "234a6f". with 2 chars for each byte in the array.
     (provided code)
    */
    public static String hexToString(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            int val = bytes[i];
            val = val & 0xff;  // remove higher bits, sign
            if (val < 16) buff.append('0'); // leading 0
            buff.append(Integer.toString(val, 16));
        }
        return buff.toString();
    }

    /*
     Given a string of hex byte values such as "24a26f", creates
     a byte[] array of those values, one byte value -128..127
     for each 2 chars.
     (provided code)
    */
    public static byte[] hexToArray(String hex) {
        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            result[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return result;
    }


    public static void main(String[] args) throws InterruptedException {
        Cracker cracker = new Cracker();
        if (args.length == 1) {
            String password = args[0];
            System.out.println(cracker.generateHashOfString(password));
        } else if (args.length > 2) {
            String hashValue = args[0];
            int maxLengthOfPasswordToBeGuessed = Integer.parseInt(args[1]);
            int numberOfThreadsUsedInCrack = 1;
            numberOfThreadsUsedInCrack = Integer.parseInt(args[2]);
            if (numberOfThreadsUsedInCrack > 40)
                throw new IllegalArgumentException("Max threads allowed: 40");
            cracker.crackHashCodeOfArbitraryPassword(hashValue.getBytes(), maxLengthOfPasswordToBeGuessed, numberOfThreadsUsedInCrack).forEach(System.out::println);
            // a! 34800e15707fae815d7c90d49de44aca97e2d759
            // xyz 66b27417d37e024c46526c2f6d358a754fc552f3
        } else {
            throw new IllegalArgumentException("Number of arguments not acceptable");
        }

    }

    public String generateHashOfString(String password) {
        byte[] hashOfPassword = digestPassword(password);
        return hexToString(hashOfPassword);
    }

    public List<String> crackHashCodeOfArbitraryPassword(byte[] hashValueOfPassword, int maxLengthOfPassword, int numberOfThreadsAllowed) throws InterruptedException {
        countDownLatch = new CountDownLatch(numberOfThreadsAllowed);
        setupResourcesForUpcomingCracking();
        List<String> resultList = new ArrayList<>();
        int arbitraryPartOfInputSpace = CHARS.length / numberOfThreadsAllowed;
        for (int partOfWorkToDo = 0; partOfWorkToDo < numberOfThreadsAllowed; partOfWorkToDo++) {
            int finalPartOfWorkToDo = partOfWorkToDo;
            new Thread(() -> {
                //Kentoba luwoba samushaos gayopisas problema ar iqneba?
                for (int startCharacterIndex = finalPartOfWorkToDo * arbitraryPartOfInputSpace;
                     startCharacterIndex < (finalPartOfWorkToDo+1)*arbitraryPartOfInputSpace;
                     startCharacterIndex++) {


                }
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        return resultList;
    }

    private void setupResourcesForUpcomingCracking() {

    }


    private byte[] digestPassword(String password) {
        byte[] digestedPassword = null;
        try {
            digestedPassword = tryToDigestPasswordUsingMessageDigest(password);
        } catch (NoSuchAlgorithmException | CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return digestedPassword;
    }

    private byte[] tryToDigestPasswordUsingMessageDigest(String password) throws NoSuchAlgorithmException, CloneNotSupportedException {
        MessageDigest passwordDigest = MessageDigest.getInstance("SHA-1");
        passwordDigest.update(password.getBytes());
        return passwordDigest.digest();
    }
}
