package com.lbsserver.db;

public class Base64
{ 
  private static final char[] ALPHABET = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
  private static int[] valueDecoding = new int[128];

  public String encodestr(byte[] data, int offset, int length)
  {
    int encodedLen = (length + 2) / 3 * 4;
    char[] encoded = new char[encodedLen];

    int i = 0; for (encodedLen = 0; encodedLen < encoded.length; )
    {
      encodeQuantum(data, offset + i, length - i, encoded, encodedLen);

      i += 3; encodedLen += 4;
    }

    return new String(encoded);
  }

  public void encodeQuantum(byte[] in, int inOffset, int len, char[] out, int outOffset)
  {
    byte a = 0; byte b = 0; byte c = 0;

    a = in[inOffset];
    out[outOffset] = ALPHABET[(a >>> 2 & 0x3F)];

    if (len > 2) {
      b = in[(inOffset + 1)];
      c = in[(inOffset + 2)];
      out[(outOffset + 1)] = ALPHABET[((a << 4 & 0x30) + (b >>> 4 & 0xF))];

      out[(outOffset + 2)] = ALPHABET[((b << 2 & 0x3C) + (c >>> 6 & 0x3))];

      out[(outOffset + 3)] = ALPHABET[(c & 0x3F)];
    } else if (len > 1) {
      b = in[(inOffset + 1)];
      out[(outOffset + 1)] = ALPHABET[((a << 4 & 0x30) + (b >>> 4 & 0xF))];

      out[(outOffset + 2)] = ALPHABET[((b << 2 & 0x3C) + (c >>> 6 & 0x3))];

      out[(outOffset + 3)] = '=';
    } else {
      out[(outOffset + 1)] = ALPHABET[((a << 4 & 0x30) + (b >>> 4 & 0xF))];

      out[(outOffset + 2)] = '=';
      out[(outOffset + 3)] = '=';
    }
  }

  public  byte[] decodestr(String encoded)
    
  { 
    return decode(encoded, 0, encoded.length());
  }

  public   byte[] decode(String encoded, int offset, int length)
    
  {
    if (length % 4 != 0) {
     System.out.println("error");
     return null;
    }

    int decodedLen = length / 4 * 3;
    if (encoded.charAt(offset + length - 1) == '=') {
      --decodedLen;
      if (encoded.charAt(offset + length - 2) == '=')
        --decodedLen;

    }

    byte[] decoded = new byte[decodedLen];
   
    int i = 0; for (decodedLen = 0; i < length; ) {
    	
      decodeQuantum(encoded.charAt(offset + i), encoded.charAt(offset + i + 1), encoded.charAt(offset + i + 2), encoded.charAt(offset + i + 3), decoded, decodedLen);

      i += 4; decodedLen += 3;
    }
  

    return decoded;
  }

  public void decodeQuantum(char in1, char in2, char in3, char in4, byte[] out, int outOffset)
    
  {
    int a = 0; int b = 0; int c = 0; int d = 0;
    int pad = 0;

    a = valueDecoding[(in1 & 0x7F)];
    b = valueDecoding[(in2 & 0x7F)];

    if (in4 == '=') {
      ++pad;
      if (in3 == '=')
        ++pad;
      else
        c = valueDecoding[(in3 & 0x7F)];
    }
    else {
      c = valueDecoding[(in3 & 0x7F)];
      d = valueDecoding[(in4 & 0x7F)];
    }

    if ((a < 0) || (b < 0) || (c < 0) || (d < 0)) {
     System.out.println("error!");
     
    }

    out[outOffset] = (byte)(a << 2 & 0xFC | b >>> 4 & 0x3);

    if (pad < 2)
    {
      out[(outOffset + 1)] = (byte)(b << 4 & 0xF0 | c >>> 2 & 0xF);

      if (pad < 1)
      {
        out[(outOffset + 2)] = (byte)(c << 6 & 0xC0 | d & 0x3F);
      }
    }
  }

  static
  {
    for (int i = 0; i < valueDecoding.length; ++i) {
      valueDecoding[i] = -1;
    }

    for (int i = 0; i < ALPHABET.length; ++i)
      valueDecoding[ALPHABET[i]] = i;
  }
}
