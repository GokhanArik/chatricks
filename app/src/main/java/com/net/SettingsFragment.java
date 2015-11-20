package com.net;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.net.util.Common;

public class SettingsFragment extends Fragment{

	public static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int RESULT_OK = -1;
	private static final int SELECT_PHOTO = 2;

	private ImageView ivProfilePicture;
	private TextView  tvUsername;
	private Uri fileUri;
	private String mCurrentPhotoPath;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.settings_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// TODO Auto-generated method stub
		ivProfilePicture = (ImageView) getView().findViewById(R.id.ivProfilePicture);
		tvUsername = (TextView) getView().findViewById(R.id.tvUsername);
		tvUsername.setText(Common.getDisplayName());
		
		ivProfilePicture.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				
				// 2. Chain together various setter methods to set the dialog characteristics
				builder.setTitle("Choose Photo")
		           	   .setItems(R.array.photo_options, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		               // The 'which' argument contains the index position
		               // of the selected item
		            	   switch(which){
		            	   	case 0:		            	   		
		            	   		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		            	   		photoPickerIntent.setType("image/*");
		            	   		startActivityForResult(photoPickerIntent, SELECT_PHOTO);   
		            	   				            	   		
		            	   		break;
		            	   	case 1:
		            	   		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		            	   		
		            	   	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
		            	   	    	
		            	   	    	File photoFile = null;
		            	   	        try {
		            	   	            photoFile = createImageFile();
		            	   	        } catch (IOException ex) {
		            	   	            // Error occurred while creating the File		            	
		            	   	        }
		            	   	        // Continue only if the File was successfully created
		            	   	        if (photoFile != null) {
		            	   	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			            	   	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		            	   	        }	            	   	        
		            	   	    }
		            	   	    
		            	   		break;
		            	   	case 2:
		            	   		break;
		            	   }
		               }
		           });

				// 3. Get the AlertDialog from create()
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
		
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	   //     Bundle extras = data.getExtras();
	  //      Bitmap imageBitmap = (Bitmap) extras.get("data");
			galleryAddPic();
	        setPic();
	    }else if( requestCode == SELECT_PHOTO && resultCode == RESULT_OK ){
	    	Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(
                               selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            mCurrentPhotoPath = filePath;
            setPic();
            cursor.close();
	    }
		
	}
	
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath =  image.getAbsolutePath();
	    return image;
	}
	
	
	private void setPic() {
	    // Get the dimensions of the View
	    int targetW = ivProfilePicture.getWidth();
	    int targetH = ivProfilePicture.getHeight();

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoH/targetW, photoW/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    ivProfilePicture.setImageBitmap(bitmap);
	}
	
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    getActivity().sendBroadcast(mediaScanIntent);
	}
	
}
