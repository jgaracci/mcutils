package org.jgdk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class DataFeedReader implements Runnable {
    private static final Log LOG = LogFactory.getLog( DataFeedReader.class );
    
    private BufferedReader dataFeedReader;
    private long pauseInterval = 50; // 50 milliseconds
    private boolean running;
    private boolean tailing;

    public DataFeedReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            dataFeedReader = (BufferedReader)reader;            
        }
        else {
            dataFeedReader = new BufferedReader(reader); 
        }
    }

    @Override
    public void run() {
        try {
            running = true;
            for (String line;running;Thread.yield()){
                line = readLine();
                if (null != line) {
                    processData(line);
                }
                else {
                    if (tailing) {
                        pause();
                    }
                    else {
                        running = false;
                    }
                }
            }
        } catch (IOException e) {
            LOG.error( "Unable to read from dataFeed: ", e);
        }
        finally {
            closeInputStream();
        }
    }

	protected String readLine() throws IOException {
		return dataFeedReader.readLine();
	}
    
    public void stop() {
        running = false;
        synchronized(this) {
            notifyAll();
        }
    }

    private void pause() {
        try {
            synchronized(this) {
                wait(pauseInterval);
            }
        } catch (InterruptedException e) {
            LOG.error("Interrupted while waiting: ", e);
        }
    }

    protected abstract void processData(String line);

    public void closeInputStream() {
        if (null != dataFeedReader) {
            try {
                dataFeedReader.close();
            } catch (IOException e) {
                LOG.error("Unable to close InputStream: ", e);
            }
        }
    }
    
    public void setIsTailing(boolean tailing) {
        this.tailing = tailing;
    }
}