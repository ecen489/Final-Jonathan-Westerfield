package com.newsboi.jonathanwesterfield.newsboi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class LocationObj
{
    private double latitude, longitude;
    private String image;

    public LocationObj() { /* Required Empty Constructor */ }

    public LocationObj(double latitude, double longitude, String image)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public String getImage()
    {
        return image;
    }

    /**
     * Putting blobs into databases is never a good idea especially with bitmaps
     * so we are going to encode it into a base64 string so it is more stable
     * @param bmp
     * @return
     */
    public static String encodeToBase64(Bitmap bmp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); // Could be Bitmap.CompressFormat.PNG or Bitmap.CompressFormat.WEBP
        byte[] bai = baos.toByteArray();

        String base64Image = Base64.encodeToString(bai, Base64.DEFAULT);
        return base64Image;
    }

    /**
     * Decode the base64 string back into the original image bitmap
     * @param base64Img
     * @return
     */
    public static Bitmap decodeFromBase64(String base64Img)
    {
        byte[] data = Base64.decode(base64Img, Base64.DEFAULT);
        Bitmap bm;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        bm = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
        return bm;
    }
}
