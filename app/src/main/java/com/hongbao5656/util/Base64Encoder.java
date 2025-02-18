package com.hongbao5656.util;

import java.io.ByteArrayOutputStream;

public class Base64Encoder
{
  private static char[] a = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
  private static byte[] b = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };

  public static String encode(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = paramArrayOfByte.length;
    int j = 0;
    while (j < i)
    {
      int k = paramArrayOfByte[(j++)] & 0xFF;
      if (j == i)
      {

        localStringBuffer.append(a[(k >>> 2)]);
        localStringBuffer.append(a[((k & 0x3) << 4)]);
        localStringBuffer.append("==");
        break;
      }
      int m = paramArrayOfByte[(j++)] & 0xFF;
      if (j == i)
      {
        localStringBuffer.append(a[(k >>> 2)]);
        localStringBuffer.append(a[((k & 0x3) << 4 | (m & 0xF0) >>> 4)]);
        localStringBuffer.append(a[((m & 0xF) << 2)]);
        localStringBuffer.append("=");
        break;
      }
      int n = paramArrayOfByte[(j++)] & 0xFF;
      localStringBuffer.append(a[(k >>> 2)]);
      localStringBuffer.append(a[((k & 0x3) << 4 | (m & 0xF0) >>> 4)]);
      localStringBuffer.append(a[((m & 0xF) << 2 | (n & 0xC0) >>> 6)]);
      localStringBuffer.append(a[(n & 0x3F)]);
    }
    return localStringBuffer.toString();
  }

  public static byte[] decode(String paramString)
  {
    byte[] arrayOfByte = paramString.getBytes();
    int i = arrayOfByte.length;
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(i);
    int j = 0;
    while (j < i)
    {
      int k;
      do
        k = b[arrayOfByte[(j++)]];
      while ((j < i) && (k == -1));
      if (k == -1)
        break;
      int m;
      do
        m = b[arrayOfByte[(j++)]];
      while ((j < i) && (m == -1));
      if (m == -1)
        break;
      localByteArrayOutputStream.write(k << 2 | (m & 0x30) >>> 4);
      int n;
      do
      {
        n = arrayOfByte[(j++)];
        if (n == 61)
          return localByteArrayOutputStream.toByteArray();
        n = b[n];
      }
      while ((j < i) && (n == -1));
      if (n == -1)
        break;
      localByteArrayOutputStream.write((m & 0xF) << 4 | (n & 0x3C) >>> 2);
      int i1;
      do
      {
        i1 = arrayOfByte[(j++)];
        if (i1 == 61)
          return localByteArrayOutputStream.toByteArray();
        i1 = b[i1];
      }
      while ((j < i) && (i1 == -1));
      if (i1 == -1)
        break;
      localByteArrayOutputStream.write((n & 0x3) << 6 | i1);
    }
    return localByteArrayOutputStream.toByteArray();
  }
}