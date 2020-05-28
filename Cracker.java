// Cracker.java
/*
 Generates SHA hashes of short strings in parallel.
*/

import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

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
            System.out.println(cracker.generateHashCodeOfString(password));
        } else if (args.length > 2) {
            String hashValue = args[0];
            int maxLengthOfPasswordToBeGuessed = Integer.parseInt(args[1]);
            int numberOfThreadsUsedInCracking = 1;
            numberOfThreadsUsedInCracking = Integer.parseInt(args[2]);
            if (numberOfThreadsUsedInCracking > 40)
                throw new IllegalArgumentException("Max threads allowed: 40");
            System.out.println(cracker.crackGivenHashCode(Cracker.hexToArray(hashValue), maxLengthOfPasswordToBeGuessed, numberOfThreadsUsedInCracking));
            // a! 34800e15707fae815d7c90d49de44aca97e2d759
            // xyz 66b27417d37e024c46526c2f6d358a754fc552f3
        } else {
            throw new IllegalArgumentException("Number of arguments not acceptable");
        }
    }

    protected String generateHashCodeOfString(String password) {
        byte[] hashOfPassword = digestPassword(password);
        return hexToString(hashOfPassword);
    }

    protected String crackGivenHashCode(byte[] hashValueOfPassword, int maxLengthOfPassword, int numberOfThreadsAllowed) throws InterruptedException {
        countDownLatch = new CountDownLatch(numberOfThreadsAllowed);
        String potentialResult = startCrackingThePassword(hashValueOfPassword,maxLengthOfPassword,numberOfThreadsAllowed);
        return potentialResult;
    }

    //Only works for good number like 4,8,2,10,5
    private String startCrackingThePassword(byte[] hashValueOfPassword, int maxLengthOfPassword, int numberOfThreadsAllowed) throws InterruptedException {
        List<String> result = new ArrayList<>();
        for(int workerThread =0; workerThread<numberOfThreadsAllowed; workerThread++){
            startUpThread(hashValueOfPassword, maxLengthOfPassword, CHARS.length/numberOfThreadsAllowed, result, workerThread);
        }
        countDownLatch.await();
        return result.get(0);
    }

    private void startUpThread(byte[] hashValueOfPassword, int maxLengthOfPassword, int sizeOfWorkForEachThread, List<String> result, int workerThread) {
        new Thread(()->{
            crackPasswordUsingPartOfCharacters(hashValueOfPassword, maxLengthOfPassword, sizeOfWorkForEachThread, result, workerThread);
        }).start();
    }

    private void crackPasswordUsingPartOfCharacters(byte[] hashValueOfPassword, int maxLengthOfPassword, int sizeOfWorkForEachThread, List<String> result, int finalWorkerThread) {
        int starIndex = finalWorkerThread *sizeOfWorkForEachThread;
        int endIndex = (finalWorkerThread +1)*sizeOfWorkForEachThread;
        for(int i=starIndex; i<endIndex; i++){
            String maybeResult = findPasswordRecursive(CHARS[i],hashValueOfPassword,maxLengthOfPassword);
            if(maybeResult!=null) result.add(maybeResult);
        }
        countDownLatch.countDown();
    }

    private String findPasswordRecursive(char startingChar, byte[] hash, int maxLengthOfPassword){
        List<String> list = new ArrayList<>();
        findPasswordRecursiveHelper(startingChar+"",hash,list,maxLengthOfPassword);
        return list.size()==0?null:list.get(0);
    }

    protected void findPasswordRecursiveHelper(String result, byte[] hash, List<String> listOfResults, int maxAllowedLength){
        if(result.length()>maxAllowedLength){
            return;
        }
        else if(passedPasswordEqualsToHash(result,hash)){
            listOfResults.add(result);
            return;
        }
        for(int i=0; i<CHARS.length; i++){
            findPasswordRecursiveHelper(result+CHARS[i],hash,listOfResults,maxAllowedLength);
        }
    }

    public boolean passedPasswordEqualsToHash(String password,byte[] hash){
        return Arrays.equals(hash,digestPassword(password));
    }

    private byte[] digestPassword(String password) {
        byte[] digestedPassword = null;
        try {
            digestedPassword = tryToDigestPasswordUsingMessageDigest(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return digestedPassword;
    }

    private byte[] tryToDigestPasswordUsingMessageDigest(String password) throws NoSuchAlgorithmException {
        MessageDigest passwordDigest = MessageDigest.getInstance("SHA-1");
        passwordDigest.update(password.getBytes());
        return passwordDigest.digest();
    }
}
