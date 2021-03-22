/*
 * =====================================
 *  Copyright (c) 2021 NextMove Software
 * =====================================
 */

package com.nextmovesoftware.contrib;

import java.util.Arrays;

/**
 * Represents an InChI-Key
 */
public final class InChIKey {

  private static final byte[]  UHFFFAOYSA = new byte[]{-29, -80, -60, 66, 24};
  private final        byte[]  fst        = new byte[9]; // 65-bits
  private final        byte[]  snd        = new byte[5]; // 37-bits
  private              boolean std        = true;
  private              char    ver        = 'A';
  private              char    chg        = 'N';

  public InChIKey(String key) {
    decode(key);
  }

  public InChIKey() {
  }

  public void decode(String key) {
    int off = 0;
    if (startsWith(key, "InChIKey="))
      off += "InChIKey=".length();
    if (key.length() - off != 27)
      throw new IllegalArgumentException("Invalid InChI Key len = " + (key.length() - off));
    DecodeUtil.decode_part1(fst, key, off);
    DecodeUtil.decode_part2(snd, key, off + 15);
    std = key.charAt(off + 23) == 'S';
    ver = key.charAt(off + 24);
    chg = key.charAt(off + 26);
  }

  private char toLower(char ch) {
    return ch >= 'A' && ch <= 'Z' ? (char) (ch + 32) : ch;
  }

  private boolean startsWith(String key, String prefix) {
    if (prefix.length() > key.length())
      return false;
    for (int i = 0; i < prefix.length(); i++) {
      if (toLower(key.charAt(i)) != toLower(prefix.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  boolean isStandard() {
    return std;
  }

  char getVersion() {
    return ver;
  }

  char getProtonation() {
    return chg;
  }

  int pack(byte[] dst, int pos) {
    int len = 9;
    System.arraycopy(fst, 0, dst, pos, 9);
    dst[pos + 8] |= ((chg - 'A') << 2);
    if (isStandard() && Arrays.equals(snd, UHFFFAOYSA)) {
      dst[pos + 8] |= 0x2;
      return len;
    }
    len += 5;
    System.arraycopy(snd, 0, dst, pos + 9, 5);
    if (!isStandard())
      dst[pos + 13] |= 1 << 5;
    return len;
  }

  int unpack(byte[] src, int pos) {
    System.arraycopy(src, pos, fst, 0, 9);
    fst[8] &= 0x1;
    chg = (char) ('A' + ((src[pos + 8] >>> 2) & 0x1f));
    if (((src[pos + 8] >>> 1) & 0x1) == 1) {
      System.arraycopy(UHFFFAOYSA, 0, snd, 0, 5);
      std = true;
      return 9;
    } else {
      System.arraycopy(src, pos + 9, snd, 0, 5);
      std = ((src[pos + 13] >>> 5) & 0x1) != 0x1;
      return 14;
    }
  }

  public String toString() {
    return DecodeUtil.encode_part1(fst) + "-" +
           DecodeUtil.encode_part2(snd) +
           (std ? 'S' : 'N') + ver + "-" + chg;
  }
}
