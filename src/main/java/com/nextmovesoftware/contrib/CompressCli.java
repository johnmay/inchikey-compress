/*
 * =====================================
 *  Copyright (c) 2021 NextMove Software
 * =====================================
 */

package com.nextmovesoftware.contrib;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class CompressCli {

  private static boolean opt_decompress = false;
  private static String  input;
  private static String  output;

  private static void displayUsage() {
    System.err.println("Usage: ikeyzip.jar [-d] {input} {output}\n");
    System.exit(1);
  }

  private static void processCommandLine(String[] args) {
    int j = 0;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.equals("-d")) {
        opt_decompress = true;
      } else {
        switch (j++) {
          case 0:
            input = arg;
            break;
          case 1:
            output = arg;
            break;
        }
      }
    }
    if (input == null)
      displayUsage();
  }

  private static void pack(InputStream in, OutputStream out) {
    try (InputStreamReader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
         OutputStream wtr = new BufferedOutputStream(out, 4096);
         BufferedReader brdr = new BufferedReader(rdr)) {
      InChIKey ikey   = new InChIKey();
      String   line;
      byte[]   buffer = new byte[14];
      while ((line = brdr.readLine()) != null) {
        ikey.decode(line);
        int len = ikey.pack(buffer, 0);
        wtr.write(buffer, 0, len);
      }
    } catch (IOException e) {
      System.err.println("ERROR - IO Error");
    }
  }

  private static void unpack(InputStream in, OutputStream out) {
    byte[] buffer = new byte[14 * 20];
    try (BufferedWriter bwtr = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
      int      read;
      int      off = 0;
      InChIKey key = new InChIKey();
      while ((read = in.read(buffer, off, buffer.length - off)) >= 0) {
        read += off;
        int i = 0;
        while (i + 14 < read) {
          i += key.unpack(buffer, i);
          bwtr.write(key.toString());
          bwtr.newLine();
        }
        off = read - i;
        System.arraycopy(buffer, i, buffer, 0, off);
      }
      if (off == 9 || off == 14) {
        key.unpack(buffer, 0);
        bwtr.write(key.toString());
        bwtr.newLine();
      } else {
        // error
      }
    } catch (IOException e) {
      System.err.println("ERROR - IO Error");
    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    processCommandLine(args);
    InputStream  in;
    OutputStream out;
    if (input == null || input.equals("-"))
      in = System.in;
    else
      in = new FileInputStream(input);
    if (output == null || output.equals("-"))
      out = System.out;
    else
      out = new FileOutputStream(output);
    if (opt_decompress) {
      unpack(in, out);
    } else {
      pack(in, out);
    }
    try {
      in.close();
    } catch (IOException ignore) {
    }
    try {
      out.close();
    } catch (IOException ignore) {
    }
  }
}
