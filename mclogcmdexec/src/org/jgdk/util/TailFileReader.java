package org.jgdk.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class TailFileReader extends Reader {
    private FileReader reader;

    public TailFileReader(File file) throws IOException {
        reader = new FileReader(file);
        
        reader.skip(file.length());
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return reader.read(cbuf, off, len);
    }
}