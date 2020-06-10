import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

//I needed help for this part of homework
public class WebWorker extends Thread {
    private String urlStr;
    private WebFrame webFrame;
    private int row;
    private Runnable acquireSemaphore;
    private Runnable releaseSemaphore;

    public WebWorker(String urlStr, int row, WebFrame webFrame, Runnable acquireSemaphore, Runnable releaseSemaphore){
        this.row = row;
        this.urlStr = urlStr;
        this.webFrame = webFrame;
        this.releaseSemaphore = releaseSemaphore;
        this.acquireSemaphore = acquireSemaphore;
    }

    @Override
    public void run(){
        acquireSemaphore.run();
        tryToDownload();
        releaseSemaphore.run();
    }

    private void tryToDownload() {
        webFrame.increaseActiveThreadCount();
        download();
        webFrame.decreaseActiveThreadCount();
    }

    private void download(){
        InputStream input = null;
        StringBuilder contents = null;
        try {
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();

            // Set connect() to throw an IOException
            // if connection does not succeed in this many msecs.
            connection.setConnectTimeout(5000);

            connection.connect();
            input = connection.getInputStream();

            BufferedReader reader  = new BufferedReader(new InputStreamReader(input));

            char[] array = new char[1000];
            int len;
            contents = new StringBuilder(1000);
            while ((len = reader.read(array, 0, array.length)) > 0) {
                contents.append(array, 0, len);
                Thread.sleep(100);
            }
            notifySuccessfulDownload(contents);
            // Successful download if we get here

        }
        // Otherwise control jumps to a catch...
        catch(MalformedURLException ignored) {}
        catch(InterruptedException exception) {
            // YOUR CODE HERE
            // deal with interruption
            notifyInterrupt();
        }
        catch(IOException ignored) {}
        // "finally" clause, to close the input stream
        // in any case
        finally {
            try{
                if (input != null) input.close();
            }
            catch(IOException ignored) {}
        }
    }

    private void notifyInterrupt() {
        webFrame.activeThreadFinished(row,"Interrupted");
    }

    private void notifySuccessfulDownload(StringBuilder contents) {
        webFrame.activeThreadFinished(row,new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }
}
