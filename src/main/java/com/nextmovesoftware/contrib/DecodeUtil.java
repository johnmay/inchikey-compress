/*
 * =====================================
 *  Copyright (c) 2021 NextMove Software
 * =====================================
 */

package com.nextmovesoftware.contrib;

class DecodeUtil {

  public static String encode3(int i) {
    i &= 0x3fff; // 14 bit back
    if (i >= 2704) // 4*26*26
      i += 676; // 26*26 skipping EAA ... EZZ
    if (i >= 12844) // 19*26*26
      i += 516; // (19*26)+22 skipping TAA ... TTV
    char c3 = (char) ('A' + Math.max(0, i % 26));
    char c2 = (char) ('A' + Math.max(0, (i /= 26) % 26));
    char c1 = (char) ('A' + Math.max(0, (i / 26) % 26));
    return new String(new char[]{c1, c2, c3});
  }

  public static String encode2(int i) {
    i &= 0x1ff; // 9 bit mask
    char c2 = (char) ('A' + Math.max(0, i % 26));
    char c1 = (char) ('A' + Math.max(0, (i / 26) % 26));
    return new String(new char[]{c1, c2});
  }

  public static int decode3(String str, int beg) {
    int i = 676 * (str.charAt(beg) - 'A') +
            26 * (str.charAt(beg + 1) - 'A') +
            (str.charAt(beg + 2) - 'A');
    if (i >= 12844) // 19*26*26
      i -= 516; // (19*26)+22 skipping TAA ... TTV
    if (i >= 2704) // 4*26*26
      i -= 676; // 26*26 skipping EAA ... EZZ
    return i;
  }

  public static int decode2(String str, int beg) {
    return 26 * (str.charAt(beg) - 'A') + (str.charAt(beg + 1) - 'A');
  }

  public static int decode2(String str) {
    return decode2(str, 0);
  }

  public static int decode3(String str) {
    return decode3(str, 0);
  }

  public static byte[] decode_part1(String str) {
    return decode_part1(new byte[9], str, 0);
  }

  public static byte[] decode_part1(byte[] bytes, String str, int beg) {
    int a = decode3(str, beg);
    int b = decode3(str, beg + 3);
    int c = decode3(str, beg + 6);
    int d = decode3(str, beg + 9);
    int e = decode2(str, beg + 12);
    bytes[0] = (byte) (a & 0xff); // 8 from a
    bytes[1] = (byte) (((a >>> 8) & 0x3f) | ((b << 6) & 0xc0)); // 6 from a + 2 from b
    bytes[2] = (byte) (((b >>> 2) & 0xff)); // 8 from b
    bytes[3] = (byte) (((b >>> 10) & 0x0f) | ((c << 4) & 0xf0)); // 4 from b, 4 from c
    bytes[4] = (byte) (((c >>> 4) & 0xff)); // 8 from c
    bytes[5] = (byte) (((c >>> 12) & 0x03) | ((d << 2) & 0xfc)); // 2 from c, 6 from d
    bytes[6] = (byte) ((d >>> 6) & 0xff); // 8 from d
    bytes[7] = (byte) (e & 0xff); // 8 from e
    bytes[8] = (byte) ((e >>> 8) & 0x01); // 1 from e
    return bytes;
  }

  public static String encode_part1(byte[] bytes) {
    int a = (bytes[0] & 0xff) | ((bytes[1] & 0x3f) << 8); // 8 + 6
    int b = ((bytes[1] & 0xc0) | ((bytes[2] & 0xff) << 8) | ((bytes[3] & 0x0f) << 16)) >>> 6; // 2 + 8 + 4
    int c = ((bytes[3] & 0xf0) | ((bytes[4] & 0xff) << 8) | ((bytes[5] & 0x03) << 16)) >>> 4; // 4 + 8 + 2
    int d = ((bytes[5] & 0xfc) | ((bytes[6] & 0xff) << 8)) >>> 2; // 6 + 8
    int e = ((bytes[7] & 0xff) | ((bytes[8] & 0x01) << 8)); // 8 + 1
    return encode3(a) + encode3(b) + encode3(c) + encode3(d) + encode2(e);
  }

  public static byte[] decode_part2(String str) {
    return decode_part2(new byte[5], str, 0);
  }

  public static byte[] decode_part2(byte[] bytes, String str, int beg) {
    int a = decode3(str, beg);
    int b = decode3(str, beg + 3);
    int c = decode2(str, beg + 6);
    bytes[0] = (byte) (a & 0xff); // 8 from a
    bytes[1] = (byte) (((a >>> 8) & 0x3f) | ((b << 6) & 0xc0)); // 6 from a + 2 from b
    bytes[2] = (byte) (((b >>> 2) & 0xff)); // 8 from b
    bytes[3] = (byte) (((b >>> 10) & 0x0f) | ((c << 4) & 0xf0)); // 4 from b, 4 from c
    bytes[4] = (byte) (((c >>> 4) & 0x1f)); // 5 from c
    return bytes;
  }

  public static String encode_part2(byte[] bytes) {
    int a = (bytes[0] & 0xff) | ((bytes[1] & 0x3f) << 8); // 8 + 6
    int b = ((bytes[1] & 0xc0) | ((bytes[2] & 0xff) << 8) | ((bytes[3] & 0x0f) << 16)) >>> 6; // 2 + 8 + 4
    int c = ((bytes[3] & 0xf0) | ((bytes[4] & 0x1f) << 8)) >> 4; // 4 + 8 + 2
    return encode3(a) + encode3(b) + encode2(c);
  }
}
