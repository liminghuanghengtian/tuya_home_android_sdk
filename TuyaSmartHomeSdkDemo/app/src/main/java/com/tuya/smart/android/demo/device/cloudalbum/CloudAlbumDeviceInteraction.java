package com.tuya.smart.android.demo.device.cloudalbum;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.android.demo.R;
import com.tuya.smart.android.demo.base.utils.CommonUtil;
import com.tuya.smart.android.demo.base.utils.DialogUtil;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.uploader.api.IOssImageUploadApi;
import com.tuya.smart.uploader.api.IOssUploadPlugin;
import com.tuya.smart.uploader.api.IOssVideoUploadApi;
import com.tuya.smart.uploader.api.IUploadProcessCallback;
import com.tuya.smart.uploader.api.IVideoUploadProcessCallback;
import com.tuya.smart.uploader.api.bean.img.ImgBean;
import com.tuya.smart.uploader.api.bean.img.ImgsUploadCompleteBean;
import com.tuya.smart.uploader.api.bean.img.ImgsUploadProgressBean;
import com.tuya.smart.uploader.api.bean.video.VideoProcessTaskBean;
import com.tuya.smart.uploader.api.bean.video.VideoProgressBean;
import com.tuya.smart.uploader.api.bean.video.VideoUploadBean;
import com.tuya.smart.uploader.impl.OssUploadPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName: TuyaSmartHomeSdkDemo
 * Description:
 * CreateDate: 2021/1/11 8:31 PM
 * <p>
 *
 * @author: <a href="mailto:liming.huang@tuya.com">colin</a>
 * @version: 3.24.5
 * @since: 3.24.5
 */
public class CloudAlbumDeviceInteraction implements IAlbumDeviceApi, ActivityCompat.PermissionCompatDelegate,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "CloudAlbumDeviceInteraction";
    private static final int REQUEST_READ_STORAGE = 15001;
    private static final int REQUEST_SELECT_MULTI_IMAGE = 16001;
    private static final int REQUEST_SELECT_VIDEO = 16002;

    private int mSelectMaxSize = 9;
    private int mVideoMaxLength = 100 * 1000;
    private Boolean mIsShowImage = true;
    private Boolean mIsShowVideo = true;
    private Activity mActivity;
    private DeviceBean mDeviceBean;
    private SelectType mType;
    @Nullable
    private IOssImageUploadApi mImageUploadApi;
    @Nullable
    private IOssVideoUploadApi mVideoUploadApi;

    public CloudAlbumDeviceInteraction(Activity activity, DeviceBean deviceBean) {
        mActivity = activity;
        mDeviceBean = deviceBean;
        IOssUploadPlugin plugin = new OssUploadPlugin(activity.getApplicationContext());
        if (plugin != null) {
            mImageUploadApi = plugin.imageUploader();
            mVideoUploadApi = plugin.videoUploader();
        }
    }

    @Override
    public List<View> getUploadIndicatorViews(Context context) {
        List<View> list = new ArrayList<>();
        Button picture = new Button(context);
        picture.setText("Select Picture");
        picture.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType = SelectType.IMAGE;
                gotoAlbumToSelect(mActivity);
            }
        });
        list.add(picture);

        Button video = new Button(context);
        video.setText("Select Movie");
        video.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType = SelectType.VIDEO;
                gotoAlbumToSelect(mActivity);
            }
        });
        list.add(video);
        return list;
    }

    @Override
    public void gotoAlbumToSelect(Activity activity) {
        requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
    }

    @Override
    public void onDestroy() {
        if (mImageUploadApi != null) {
            mImageUploadApi.onDestroy();
        }
        if (mVideoUploadApi != null) {
            mVideoUploadApi.onDestroy();
        }
    }

    // private void generalOpen(Activity activity) {
    //     Uri uri = mType.externalContentUri;
    //     Intent intent = new Intent(Intent.ACTION_PICK, uri);
    //     activity.startActivityForResult(intent, REQUEST_SELECT_MULTI_IMAGE);
    // }

    private void openGallery(Activity activity) {
        if (activity == null) return;

        if (SelectType.IMAGE == mType) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(Intent.createChooser(intent, "Select Pictures"),
                    REQUEST_SELECT_MULTI_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, SelectType.VIDEO.externalContentUri);
            activity.startActivityForResult(intent, REQUEST_SELECT_VIDEO);
        }

        // Bundle args = new Bundle();
        // args.putInt("max_picker", mSelectMaxSize);
        // args.putInt("videoMaxLength", mVideoMaxLength);
        // args.putBoolean("isShowImage", mIsShowImage);
        // args.putBoolean("isShowVideo", mIsShowVideo);
        //
        // Intent intent = new Intent(activity, GalleryPickerAct.class);
        // intent.putExtras(args);
        //
        // activity.startActivityForResult(intent, REQUEST_SELECT_MULTI_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_STORAGE && permissions != null && grantResults != null) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery(mActivity);
            } else {
                showPermissionDialog();
            }
        }
    }

    @Override
    public boolean requestPermissions(@NonNull Activity activity, @NonNull String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
        return false;
    }

    @Override
    public boolean onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_SELECT_MULTI_IMAGE) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                Uri uri;
                List<ImgBean> fileList = new ArrayList<>();
                ClipData imageNames = data.getClipData();
                if (imageNames != null) {
                    for (int i = 0; i < imageNames.getItemCount(); i++) {
                        Uri imageUri = imageNames.getItemAt(i).getUri();
                        ImgBean bean = new ImgBean();
                        bean.setImage(imageUri.toString());
                        bean.setTitle("No." + i + "-" + System.currentTimeMillis());
                        fileList.add(bean);
                        L.d(TAG, String.format("Image[%d] uri: %s", i, imageUri.toString()));
                    }
                } else {
                    uri = data.getData();
                    if (uri != null) {
                        ImgBean bean = new ImgBean();
                        bean.setImage(uri.toString());
                        bean.setTitle("Image-1-" + System.currentTimeMillis());
                        fileList.add(bean);
                        L.d(TAG, String.format("Image uri: %s", uri.toString()));
                    }
                }
                if (mImageUploadApi != null) {
                    mImageUploadApi.batchUploadToDevice(mDeviceBean.getDevId(), fileList, new IUploadProcessCallback<ImgsUploadProgressBean, ImgsUploadCompleteBean>() {
                        @Override
                        public void onProcessStart(String s) {
                            L.d(TAG, "onProcessStart, taskId: " + s);
                        }

                        @Override
                        public void onProgress(ImgsUploadProgressBean imgsUploadProgressBean) {
                            L.d(TAG,
                                    "onProgress, current: " + imgsUploadProgressBean.getCurrentImage() + ", fileId: " + imgsUploadProgressBean.getEncryptFileId());
                        }

                        @Override
                        public void onComplete(ImgsUploadCompleteBean imgsUploadCompleteBean) {
                            L.i(TAG,
                                    "onComplete, success: " + imgsUploadCompleteBean.getUploaded() + ", failed: " + imgsUploadCompleteBean.getFailed());
                        }
                    });
                }
            }
        } else if (requestCode == REQUEST_SELECT_VIDEO) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                Uri selectedVideo = data.getData();
                if (selectedVideo == null) return false;

                String[] filePathColumn = {SelectType.VIDEO.data, MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DURATION, MediaStore.Video.Media.TITLE, MediaStore.Images.Media.DATA};
                Cursor cursor = activity.getContentResolver().query(selectedVideo,
                        filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    try {
                        // videoPath
                        int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
                        String videoPath = cursor.getString(columnIndex);
                        L.d(TAG, "videoPath: " + videoPath);
                        // size
                        columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[1]);
                        int size = cursor.getInt(columnIndex);
                        L.d(TAG, "videoSize: " + size);
                        // duration
                        columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[2]);
                        int duration = cursor.getInt(columnIndex);
                        L.d(TAG, "videoDuration: " + duration);
                        // title
                        columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[3]);
                        String title = cursor.getString(columnIndex);
                        L.d(TAG, "videoTitle: " + title);
                        // thumbnailPath
                        columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[4]);
                        String thumbPath = cursor.getString(columnIndex);
                        L.d(TAG, "videoThumbPath: " + thumbPath);
                        cursor.close();

                        VideoUploadBean bean = new VideoUploadBean();
                        bean.setVideoUri(selectedVideo.toString());
                        bean.setName(title + "-" + System.currentTimeMillis());
                        bean.setDuration(duration);
                        bean.setSize(size);
                        if (mVideoUploadApi != null) {
                            mVideoUploadApi.uploadVideoToDevice(mDeviceBean.getDevId(), bean, new IVideoUploadProcessCallback<VideoProgressBean, Object>() {
                                @Override
                                public void onProgress(VideoProgressBean videoProgressBean) {
                                    L.d(TAG,
                                            "onProgress, code: " + videoProgressBean.getCode() + ", extInfo: " + videoProgressBean.getExtInfo().toString());
                                }

                                @Override
                                public void onProcessStart(String s) {
                                    L.d(TAG, "onProcessStart, taskId: " + s);
                                }

                                @Override
                                public void onComplete(Object o) {
                                    L.i(TAG, "onComplete");
                                }

                                @Override
                                public void onProcessStart(VideoProcessTaskBean videoProcessTaskBean) {
                                    L.d(TAG, "onProcessStart, taskId: " + videoProcessTaskBean.taskId);
                                }
                            });
                        }
                    } catch (Exception e) {
                        L.e(TAG, e.getMessage(), e);
                    }
                }
            }
        }

        // ThreadEnv.io().execute(new Runnable() {
        //     @Override
        //     public void run() {
        //         WritableMap resultMap = Arguments.createMap();
        //         if (resultCode == Activity.RESULT_OK && data != null) {
        //             //获取图片集合
        //             ArrayList<String> images = data.getStringArrayListExtra("pickImgs");
        //             //获取视频集合
        //             String videoListJson = data.getStringExtra("videoListJson");
        //             //标记成功
        //             resultMap.putBoolean("success", true);
        //             if (images != null && images.size() > 0) {
        //                 //说明是图片选择
        //                 WritableArray array = Arguments.createArray();
        //                 for (String imageUri : images) {
        //                     WritableMap map = Arguments.createMap();
        //                     map.putString("name", String.valueOf(new Random().nextInt(100)));
        //                     map.putString("path", imageUri);
        //                     array.pushMap(map);
        //                 }
        //                 //加入到结果集
        //                 resultMap.putArray("result", array);
        //             } else if (!TextUtils.isEmpty(videoListJson)) {
        //                 //说明是视频选择
        //                 WritableArray array = Arguments.createArray();
        //                 try {
        //                     JSONArray jsonArray = new JSONArray(videoListJson);
        //                     for (int i = 0; i < jsonArray.length(); i++) {
        //                         WritableMap map = Arguments.createMap();
        //                         JSONObject jsonObject = jsonArray.getJSONObject(i);
        //                         map.putString("name", String.valueOf(new Random().nextInt(100)));
        //                         map.putString("path", jsonObject.getString("path"));
        //                         map.putBoolean("isVideo", true);
        //                         map.putString("videoUri", jsonObject.getString("videoUri"));
        //                         map.putInt("size", jsonObject.getInt("size"));
        //                         map.putInt("duration", jsonObject.getInt("duration"));
        //                         array.pushMap(map);
        //                     }
        //                 } catch (JSONException e) {
        //                     e.printStackTrace();
        //                 }
        //                 //加入到结果集
        //                 resultMap.putArray("result", array);
        //             } else {
        //                 //失败情况
        //                 resultMap.putBoolean("success", false);
        //                 resultMap.putString("error", "");
        //             }
        //         } else {
        //             //失败情况
        //             resultMap.putBoolean("success", false);
        //             resultMap.putString("error", "");
        //         }
        //         mCallback.invoke(resultMap);
        //         mCallback = null;
        //     }
        // });
        return false;
    }

    @SuppressLint("StringFormatInvalid")
    private void showPermissionDialog() {
        if (mActivity == null) return;

        String tip = String.format(mActivity.getString(R.string.ty_set_read_external_permission),
                CommonUtil.getApplicationName(mActivity, mActivity.getPackageName()));
        DialogUtil.simpleSmartDialog(mActivity, tip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
                    intent.setData(uri);
                    mActivity.startActivity(intent);
                }
            }
        });
    }

    public enum SelectType {
        VIDEO(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.DATA),
        IMAGE(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA);

        Uri externalContentUri;
        String data;

        SelectType(Uri externalContentUri,
                   String data) {
            this.externalContentUri = externalContentUri;
            this.data = data;
        }
    }

}
