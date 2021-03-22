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

public class PrefixCompress {

  static int prefix_len(String prev, String curr) {
    if (prev == null)
      return 0;
    int i = 0;
    while (i<curr.length()) {
      if (prev.charAt(i) != curr.charAt(i))
        break;
      i++;
    }
    return i;
  }

  static int suffix_len(String prev, String curr) {
    if (prev == null)
      return 0;
    int i = 0;
    while (i<curr.length()) {
      int j = curr.length()-i-1;
      if (prev.charAt(j) != curr.charAt(j))
        break;
      i++;
    }
    return i;
  }

  public static void main(String[] args) {
    long pass  = 0;
    long total = 0;
    StringBuilder sb = new StringBuilder();
    try (FileInputStream in = new FileInputStream(args[0]);
         InputStreamReader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
         BufferedReader brdr = new BufferedReader(rdr)) {
      String prev = null;
      String line;
      while ((line = brdr.readLine()) != null) {
        int prefix = prefix_len(prev, line);
        int suffix = suffix_len(prev, line);
        sb.append((char)('a'+prefix));
        sb.append((char)('a'+suffix));
        sb.append(line, prefix, line.length() - suffix);
        sb.append('\n');
        prev = line;
        total++;
        if ((total % 100000) == 0) {
          System.err.printf("\r%d...",  total);
          System.out.print(sb.toString());
          sb.setLength(0);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.err.printf("\r%d...\n",  total);
    System.out.print(sb.toString());
  }
}
