/*
 * =====================================
 *  Copyright (c) 2021 NextMove Software
 * =====================================
 */

package com.nextmovesoftware.contrib;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RoundTripCli {
  private static boolean opt_pack = false;
  private static byte[]  buffer   = new byte[14];

  public static void main(String[] args) {
    long pass  = 0;
    long total = 0;
    try (FileInputStream in = new FileInputStream(args[0]);
         InputStreamReader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
         BufferedReader brdr = new BufferedReader(rdr)) {
      InChIKey ikey = new InChIKey();
      String   line;
      while ((line = brdr.readLine()) != null) {
        if (verify(ikey, line)) {
          pass++;
        } else {
          System.err.println("ERROR: Did not round trip " + line);
        }
        total++;
        if ((total % 100000) == 0)
          System.err.printf("\r%d/%d", pass, total);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.err.printf("\r%d/%d\n", pass, total);
  }

  private static boolean verify(InChIKey key, String line) {
    key.decode(line);
    if (opt_pack) {
      key.pack(buffer, 0);
      key.unpack(buffer, 0);
    }
    return key.toString().equals(line);
  }
}
