/*
 * =====================================
 *  Copyright (c) 2021 NextMove Software
 * =====================================
 */

package com.nextmovesoftware.contrib;

import org.junit.jupiter.api.Test;

import static com.nextmovesoftware.contrib.DecodeUtil.decode2;
import static com.nextmovesoftware.contrib.DecodeUtil.decode3;
import static com.nextmovesoftware.contrib.DecodeUtil.decode_part1;
import static com.nextmovesoftware.contrib.DecodeUtil.decode_part2;
import static com.nextmovesoftware.contrib.DecodeUtil.encode_part1;
import static com.nextmovesoftware.contrib.DecodeUtil.encode2;
import static com.nextmovesoftware.contrib.DecodeUtil.encode3;
import static com.nextmovesoftware.contrib.DecodeUtil.encode_part2;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DecodeUtilTest {

  @Test
  public void testTriples() {
    for (int i = 0; i < 16384; i++)
      assertThat(i + " => " + encode3(i) + " => " + decode3(encode3(i)),
                 decode3(encode3(i)), is(i));
  }

  @Test
  public void testDoubles() {
    for (int i = 0; i < 512; i++)
      assertThat(i + " => " + encode2(i) + " => " + decode2(encode2(i)),
                 decode2(encode2(i)), is(i));
  }

  // RYYVLZVUVIJVGH-UHFFFAOYSA-N
  @Test
  public void testRoundTripFirstPart() {
    assertThat(encode_part1(decode_part1("RYYVLZVUVIJVGH")),
               is("RYYVLZVUVIJVGH"));
  }

  @Test
  public void testRoundTripSecondPart() {
    assertThat(encode_part2(decode_part2("UHFFFAOYSA")),
               is("UHFFFAOY"));
  }

  @Test
  public void roundTrip() {
    assertThat(new InChIKey("RYYVLZVUVIJVGH-UHFFFAOYSA-N").toString(),
               is("RYYVLZVUVIJVGH-UHFFFAOYSA-N"));
    assertThat(new InChIKey("InChIKey=RYYVLZVUVIJVGH-UHFFFAOYSA-N").toString(),
               is("RYYVLZVUVIJVGH-UHFFFAOYSA-N"));
    assertThat(new InChIKey("INCHIKEY=RYYVLZVUVIJVGH-UHFFFAOYSA-N").toString(),
               is("RYYVLZVUVIJVGH-UHFFFAOYSA-N"));
  }

  private static void testPacking(String str) {
    InChIKey ikey = new InChIKey(str);
    byte[]   buf  = new byte[14];
    ikey.pack(buf, 0);
    InChIKey ikey2 = new InChIKey();
    ikey2.unpack(buf, 0);
    assertThat(ikey.toString(), is(str));
    assertThat(ikey.toString(), is(ikey2.toString()));
  }

  @Test
  public void packing() {
    testPacking("RYYVLZVUVIJVGH-UHFFFAOYSA-N");
  }

  @Test
  public void packing2() {
    testPacking("BTANRVKWQNVYAZ-SCSAIBSYSA-N");
  }
}