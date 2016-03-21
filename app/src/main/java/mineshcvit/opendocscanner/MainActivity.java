/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mineshcvit.opendocscanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity    extends Activity {

    public static final String TAG = "MainActivity";

    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.PNG";



    public static final int REQUEST_CODE_GALLERY      = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE   = 0x3;

    private ImageView mImageView;
    private File      mFileTemp;
    private File       ocrOutFile;

    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;


    private String imagepath=null;









    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.activity_main);


        findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file= new File( mFileTemp.getPath());
                if(file.exists())
                {
                    file.delete();
                }

                openGallery();
            }
        });

        findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file = new File(mFileTemp.getPath());
                if (file.exists()) {
                    Log.w("myApp", "old image exists and is going to be deleted ");

                    file.delete();
                }

                takePicture();
            }
        });









        mImageView = (ImageView) findViewById(R.id.image);

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);

        }
        else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);


        }

    }

    private void takePicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            }
            else {
	        	/*
	        	 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
	        	 */
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {

            Log.d(TAG, "cannot take picture", e);
        }
    }

    private void openGallery() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    private void startCropImage() {

        Intent intent = new Intent(this,CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 0);
        intent.putExtra(CropImage.ASPECT_Y, 0);

        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {

            return;
        }

        Bitmap bitmap=null;

        switch (requestCode) {

            case REQUEST_CODE_GALLERY:

                try {



                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();

                    /// binarize the image before passing to the crop functionality

                     //   binarizeAndSaveIntheSameLocation();


                    //use opencv functions to binarize the image

                    startCropImage();



                } catch (Exception e) {

                    Log.e(TAG, "Error while creating temp file", e);
                }

                break;
            case REQUEST_CODE_TAKE_PICTURE:

                //binrize image before passing to crop functionality
               //binarizeAndSaveIntheSameLocation();


               // Mat grayImage=Imgcodecs.imread(mFileTemp.getPath(),Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
               // Imgproc.threshold(grayImage, grayImage, 0, 255, Imgproc.THRESH_OTSU);
                //Imgcodecs.imwrite(mFileTemp.getPath(), grayImage);
              //  startCropImage();
               startCropImage();
                break;
            case REQUEST_CODE_CROP_IMAGE:

                Log.w("myApp","returned from cropping activity");

                Log.w("myApp","mFileTempS PATH is ="+mFileTemp.getPath());
                Log.w("myApp","IMAGE_PATH is ="+CropImage.IMAGE_PATH);

                String path = data.getStringExtra(CropImage.IMAGE_PATH);
               // if (path == null) {
                    //Log.w("myApp","path is null");//PATH WOULD BE NULL WHEN ORIGINAL IMAGE IS CHOSEN  SO THIS IF CONDITION WAS REMOVED

                   // return;
                //}
                if(bitmap!=null)
                bitmap.recycle();



                Mat grayImage= Imgcodecs.imread(mFileTemp.getPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
               // Imgproc.equalizeHist(grayImage,grayImage);

               // Mat smoothed=new Mat();
              //  Mat laplac=new Mat();
                //Imgproc.GaussianBlur(grayImage, smoothed, new Size(3.0, 3.0), 3);//gaussian blurring
                //Imgproc.Laplacian(smoothed,laplac,3);//laplacian


                Imgproc.threshold(grayImage, grayImage, 0, 255, Imgproc.THRESH_OTSU);
                //Imgproc.Canny(grayImage,grayImage,100.0,100);
                Imgcodecs.imwrite(mFileTemp.getPath(), grayImage);

                bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
               // Log.w("myApp","mFileTemp is ="+mFileTemp.getPath());


                mImageView.setImageBitmap(bitmap);




             runOnUiThread(new Runnable() {

                    public void run() {

                        Toast.makeText(MainActivity.this, "Choose Language and Hit OCR, wait for few seconds for output", Toast.LENGTH_LONG).show();
                    }
                });


               // Toast.makeText(MainActivity.this, "Choose Language and Hit OCR", Toast.LENGTH_LONG).show();


                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }




}
