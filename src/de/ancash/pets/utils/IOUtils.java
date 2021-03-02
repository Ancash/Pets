package de.ancash.pets.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({ "unused" })
public class IOUtils {
  
	public static void closeQuietly(InputStream paramCloseable) {
		try {
			if ((Closeable) paramCloseable != null)
				paramCloseable.close(); 
		} catch (IOException iOException) {
			
		}
	}
	  
	public static void closeQuietly(OutputStream paramCloseable) {
		try {
			if ((Closeable) paramCloseable != null)
				paramCloseable.close(); 
		} catch (IOException iOException) {
	    		
		}
	}
  
	public static int copy(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
		long l = copyLarge(paramInputStream, paramOutputStream);
		if (l > 2147483647L)
			return -1; 
		return (int)l;
	}
  
	public static long copyLarge(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
		return copyLarge(paramInputStream, paramOutputStream, new byte[4096]);
	}
  
	public static long copyLarge(InputStream paramInputStream, OutputStream paramOutputStream, byte[] paramArrayOfbyte) throws IOException {
		long l = 0L;
		int i = 0;
		while (-1 != (i = paramInputStream.read(paramArrayOfbyte))) {
			paramOutputStream.write(paramArrayOfbyte, 0, i);
			l += i;
		} 
		return l;
	} 
}

