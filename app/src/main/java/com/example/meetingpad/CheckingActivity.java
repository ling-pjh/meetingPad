package com.example.meetingpad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.sdk_demo.DetecterActivity;
import com.example.meetingpad.Service.OKHTTP;
import com.guo.android_extend.tools.CameraHelper;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraSurfaceView.OnCameraListener;

import com.arcsoft.ageestimation.ASAE_FSDKAge;
import com.arcsoft.ageestimation.ASAE_FSDKEngine;
import com.arcsoft.ageestimation.ASAE_FSDKError;
import com.arcsoft.ageestimation.ASAE_FSDKFace;
import com.arcsoft.ageestimation.ASAE_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;
import com.arcsoft.genderestimation.ASGE_FSDKVersion;
import com.arcsoft.sdk_demo.FaceDB;
import com.example.meetingpad.Adapter.AttendMeetingListAdapter;
import com.example.meetingpad.entity.Meeting;
import com.example.meetingpad.entity.PersonLight;
import com.guo.android_extend.GLES2Render;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CheckingActivity extends AppCompatActivity implements OnCameraListener, View.OnTouchListener, Camera.AutoFocusCallback, View.OnClickListener{


    private final int FACE_GET_PEOPLE_SUCCESS=0;
    private final int FACE_GET_PEOPLE_FAIL=1;
    private final String TAG = this.getClass().getSimpleName();
    private int mWidth, mHeight, mFormat;
    private CameraSurfaceView mSurfaceView;
    private CameraGLSurfaceView mGLSurfaceView;
    private Camera mCamera;
    private int mNo;
    private PersonLight detectedPerson;
    private Meeting meetingDetail;//用来保存这个页面的一切动态信息

    AFT_FSDKVersion version = new AFT_FSDKVersion();
    AFT_FSDKEngine engine = new AFT_FSDKEngine();
    ASAE_FSDKVersion mAgeVersion = new ASAE_FSDKVersion();
    ASAE_FSDKEngine mAgeEngine = new ASAE_FSDKEngine();
    ASGE_FSDKVersion mGenderVersion = new ASGE_FSDKVersion();
    ASGE_FSDKEngine mGenderEngine = new ASGE_FSDKEngine();
    List<AFT_FSDKFace> result = new ArrayList<>();
    List<ASAE_FSDKAge> ages = new ArrayList<>();
    List<ASGE_FSDKGender> genders = new ArrayList<>();
    private UIHandler myHandler=new UIHandler();

    class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case FACE_GET_PEOPLE_FAIL:
                    Toast.makeText(CheckingActivity.this,"人脸获取Person失败",Toast.LENGTH_SHORT).show();
                    break;
                case FACE_GET_PEOPLE_SUCCESS:
                    Toast.makeText(CheckingActivity.this,"人脸获取Person成功",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }
    int mCameraID;
    int mCameraRotate;
    int mCameraMirror;
    byte[] mImageNV21 = null;
    FRAbsLoop mFRAbsLoop = null;
    AFT_FSDKFace mAFT_FSDKFace = null;
    Handler mHandler;
    boolean isPostted = false;

    Runnable hide = new Runnable() {
        @Override
        public void run() {
            mTextView.setAlpha(0.5f);
            mImageView.setImageAlpha(128);
            isPostted = false;
        }
    };
    private TextView mTextView;
    private TextView mTextView1;
    private ImageView mImageView;
    private ImageButton mImageButton;

    @Override
    public void onAutoFocus(boolean b, Camera camera) {
        if (b) {
            Log.d(TAG, "Camera Focus SUCCESS!");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageButton) {
            if (mCameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
                mCameraRotate = 180;
                mCameraMirror = GLES2Render.MIRROR_NONE;
            } else {
                mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
                mCameraRotate =  0;
                mCameraMirror = GLES2Render.MIRROR_NONE;
            }
            mSurfaceView.resetCamera();
            mGLSurfaceView.setRenderConfig(mCameraRotate, mCameraMirror);
            mGLSurfaceView.getGLES2Render().setViewDisplay(mCameraMirror, mCameraRotate);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        CameraHelper.touchFocus(mCamera, motionEvent, view, this);
        return false;
    }

    @Override
    public Camera setupCamera() {
        mCamera = Camera.open(mCameraID);
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mWidth, mHeight);
            parameters.setPreviewFormat(mFormat);

            for( Camera.Size size : parameters.getSupportedPreviewSizes()) {
                Log.d(TAG, "SIZE:" + size.width + "x" + size.height);
            }
            for( Integer format : parameters.getSupportedPreviewFormats()) {
                Log.d(TAG, "FORMAT:" + format);
            }

            List<int[]> fps = parameters.getSupportedPreviewFpsRange();
            for(int[] count : fps) {
                Log.d(TAG, "T:");
                for (int data : count) {
                    Log.d(TAG, "V=" + data);
                }
            }
//            parameters.setPreviewFpsRange(15000, 30000);
//            parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
//            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
//            parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
//            parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
//            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCamera != null) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;
        }
        return mCamera;
    }

    @Override
    public void setupChanged(int format, int width, int height) {

    }

    @Override
    public boolean startPreviewImmediately() {

        return true;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
        AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
        Log.d(TAG, "Face=" + result.size());



        for (AFT_FSDKFace face : result) {
            Log.d(TAG, "Face:" + face.toString());
        }
        if (mImageNV21 == null) {
            if (!result.isEmpty()) {
                mAFT_FSDKFace = result.get(0).clone();
                mImageNV21 = data.clone();
            } else {
                if (!isPostted) {
                    mHandler.removeCallbacks(hide);
                    mHandler.postDelayed(hide, 2000);
                    isPostted = true;
                }
            }
        }
        //copy rects
        Rect[] rects = new Rect[result.size()];
        for (int i = 0; i < result.size(); i++) {
            rects[i] = new Rect(result.get(i).getRect());
        }
        //clear result.
        result.clear();
        //return the rects for render.
        return rects;
    }

    @Override
    public void onBeforeRender(CameraFrameData data) {


    }

    @Override
    public void onAfterRender(CameraFrameData data) {

        mGLSurfaceView.getGLES2Render().draw_rect((Rect[])data.getParams(), Color.GREEN, 2);
    }

    class FRAbsLoop extends AbsLoop {

        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace result = new AFR_FSDKFace();
        List<FaceDB.FaceRegist> mResgist = FaceDB.mRegister;
        List<ASAE_FSDKFace> face1 = new ArrayList<>();
        List<ASGE_FSDKFace> face2 = new ArrayList<>();

        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
            error = engine.AFR_FSDK_GetVersion(version);
            Log.d(TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
        }

        @Override
        public void loop() {

            if (mImageNV21 != null) {
                final int rotate = mCameraRotate;

                long time = System.currentTimeMillis();
                AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
                Log.d(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() - time) + "ms");
                Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());

//                HashMap<String,String> faceRequestMap=new HashMap<>();
//
//                String base64Str = Base64.encodeToString(result.getFeatureData(), Base64.NO_WRAP);
//                faceRequestMap.put("face",base64Str);
//                OKHTTP.postForm("/checkin/recognize.do",faceRequestMap,faceRequestCallBack);


                AFR_FSDKMatching score = new AFR_FSDKMatching();
                float max = 0.0f;
                String name = null;



                if(meetingDetail!=null) {
                    for (PersonLight p : meetingDetail.getmAttendList()) {
                        AFR_FSDKFace face = new AFR_FSDKFace();
                        face.setFeatureData(p.getpFace());
                        error = engine.AFR_FSDK_FacePairMatching(result, face, score);
                        Log.d(TAG, "Score:" + score.getScore() + ",AFR_FSDK_FacePairMatching-" + error.getCode());
                        if(score.getScore()>=0.6){
                            for(PersonLight p1 : notCheckPeople){
                                if(p.getpId()==p1.getpId()) {
                                    checkedPeople.add(p1);
                                    checkedPeopleAdapter.setPersons(checkedPeople);
                                    checkedPeopleAdapter.notifyDataSetChanged();

                                    notCheckPeople.remove(p1);
                                    notCheckPeopleAdapter.setPersons(notCheckPeople);
                                    notCheckPeopleAdapter.notifyDataSetChanged();
                                }

                            }
                        }
                    }
                }
                for (FaceDB.FaceRegist fr : mResgist) {
                    for (AFR_FSDKFace face : fr.mFaceList.values()) {
                        error = engine.AFR_FSDK_FacePairMatching(result, face, score);
                        Log.d(TAG,  "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
                        if (max < score.getScore()) {
                            max = score.getScore();
                            name = fr.mName;
                        }
                    }
                }

                //age & gender
                face1.clear();
                face2.clear();
                face1.add(new ASAE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                face2.add(new ASGE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                ASAE_FSDKError error1 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face1, ages);
                ASGE_FSDKError error2 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face2, genders);
                Log.d(TAG, "ASAE_FSDK_AgeEstimation_Image:" + error1.getCode() + ",ASGE_FSDK_GenderEstimation_Image:" + error2.getCode());
                Log.d(TAG, "age:" + ages.get(0).getAge() + ",gender:" + genders.get(0).getGender());
                final String age = ages.get(0).getAge() == 0 ? "年龄未知" : ages.get(0).getAge() + "岁";
                final String gender = genders.get(0).getGender() == -1 ? "性别未知" : (genders.get(0).getGender() == 0 ? "男" : "女");

                //crop
                byte[] data = mImageNV21;
                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
                yuv.compressToJpeg(mAFT_FSDKFace.getRect(), 80, ops);
                final Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
                try {
                    ops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (max > 0.6f) {
                    //fr success.
                    final float max_score = max;
                    Log.d(TAG, "fit Score:" + max + ", NAME:" + name);
                    final String mNameShow = name;
                    mHandler.removeCallbacks(hide);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setAlpha(1.0f);
                            mTextView.setText(mNameShow);
                            mTextView.setTextColor(Color.RED);
                            mTextView1.setVisibility(View.VISIBLE);
                            mTextView1.setText("置信度：" + (float)((int)(max_score * 1000)) / 1000.0);
                            mTextView1.setTextColor(Color.RED);
                            mImageView.setRotation(rotate);
                            mImageView.setScaleY(-mCameraMirror);
                            mImageView.setImageAlpha(255);
                            mImageView.setImageBitmap(bmp);
                        }
                    });
                } else {
                    final String mNameShow = "未识别";
                    CheckingActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            mTextView.setAlpha(1.0f);
//                            mTextView1.setVisibility(View.VISIBLE);
//                            mTextView1.setText( gender + "," + age);
//                            mTextView1.setTextColor(Color.RED);
//                            mTextView.setText(mNameShow);
//                            mTextView.setTextColor(Color.RED);
//                            mImageView.setImageAlpha(255);
//                            mImageView.setRotation(rotate);
//                            mImageView.setScaleY(-mCameraMirror);
//                            mImageView.setImageBitmap(bmp);
                        }
                    });
                }
                mImageNV21 = null;
            }

        }

        @Override
        public void over() {

            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
            Log.d(TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
        }
    }

    private TextView tvMeetingRoomId;
    private RecyclerView checkedPeopleImages,notCheckPeopleImages;

    private List<PersonLight> checkedPeople=new ArrayList<PersonLight>(),notCheckPeople=new ArrayList<PersonLight>();
    private AttendMeetingListAdapter checkedPeopleAdapter=new AttendMeetingListAdapter(this,checkedPeople);
    private AttendMeetingListAdapter notCheckPeopleAdapter=new AttendMeetingListAdapter(this,notCheckPeople);
    private TextView infomationTV;
    private Meeting meeting;//用来保存这个页面的一切动态信息
    //CheckingActivity不断刷新签到名单，并在十分钟过后自动跳转到InMeetingActivity
    TimerTask toInMeetingState= new TimerTask() {//转换为签到中时的任务
        @Override
        public void run() {
            Intent intent=new Intent(CheckingActivity.this, InMeetingActivity.class);
            intent.putExtra("meetingRoomId","A001");
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            Log.i("CheckingActivity","toInMeetingState");
            intent.putExtra("mNo",mNo);
//            startActivity(intent);
//            finish();
        }
    };
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //获取参数
            mNo=getIntent().getIntExtra("mNo",2);
            //发请求查到meeting
            HashMap<String, String> map = new HashMap();
            map.put("mNo", mNo+"");//传进来的mNo
            //查询对应会议详情
            OKHTTP.postForm("/meeting/detail.do", map, fillMeetingDetail);

            //定时操作
            Timer timer = new Timer();
            //TODO 设置周期性刷新签到表
            timer.schedule(toInMeetingState,new Date(System.currentTimeMillis()+6000));//设置十分钟后转入InMeeting//现在设置的是6秒

            setContentView(R.layout.activity_checking);
            getSupportActionBar().setTitle("签到中");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            int i;
            tvMeetingRoomId=(TextView)findViewById(R.id.tv_meeting_room_id_checking);
            tvMeetingRoomId.setText(getIntent().getStringExtra("meetingRoomId"));




            checkedPeopleAdapter=new AttendMeetingListAdapter(this,checkedPeople);
            notCheckPeopleAdapter=new AttendMeetingListAdapter(this,notCheckPeople);


            checkedPeopleImages=(RecyclerView)findViewById(R.id.recycler_view_checked_in_checking);
            checkedPeopleImages.setLayoutManager(new GridLayoutManager(this,6));

            notCheckPeopleImages=(RecyclerView)findViewById(R.id.recycler_view_not_check_in_checking);
            notCheckPeopleImages.setLayoutManager(new GridLayoutManager(this,6));

            checkedPeopleImages.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.right=20;
                    outRect.bottom=20;
                }
            });

            notCheckPeopleImages.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.right=20;
                    outRect.bottom=20;
                }
            });

            checkedPeopleImages.setAdapter(checkedPeopleAdapter);
            notCheckPeopleImages.setAdapter(notCheckPeopleAdapter);

            mCameraID=Camera.CameraInfo.CAMERA_FACING_FRONT;
//        mCameraID = getIntent().getIntExtra("Camera", 0) == 0 ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
        //		mCameraRotate = getIntent().getIntExtra("Camera", 0) == 0 ? 90 : 270;
        mCameraRotate = 180;
//		mCameraMirror = getIntent().getIntExtra("Camera", 0) == 0 ? GLES2Render.MIRROR_NONE : GLES2Render.MIRROR_X;
        mCameraMirror = GLES2Render.MIRROR_NONE;
        mWidth = 640;
        mHeight = 38;
        mFormat = ImageFormat.NV21;
        mHandler = new Handler();

        mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
        mGLSurfaceView.setOnTouchListener(this);
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setOnCameraListener(this);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate);
        mSurfaceView.debug_print_fps(true, false);

        //snap
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setText("");
        mTextView1 = (TextView) findViewById(R.id.textView1);
        mTextView1.setText("");

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageButton = (ImageButton) findViewById(R.id.imageButton);
        mImageButton.setOnClickListener(this);

        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

        ASAE_FSDKError error = mAgeEngine.ASAE_FSDK_InitAgeEngine(FaceDB.appid, FaceDB.age_key);
        Log.d(TAG, "ASAE_FSDK_InitAgeEngine =" + error.getCode());
        error = mAgeEngine.ASAE_FSDK_GetVersion(mAgeVersion);
        Log.d(TAG, "ASAE_FSDK_GetVersion:" + mAgeVersion.toString() + "," + error.getCode());

        ASGE_FSDKError error1 = mGenderEngine.ASGE_FSDK_InitgGenderEngine(FaceDB.appid, FaceDB.gender_key);
        Log.d(TAG, "ASGE_FSDK_InitgGenderEngine =" + error1.getCode());
        error1 = mGenderEngine.ASGE_FSDK_GetVersion(mGenderVersion);
        Log.d(TAG, "ASGE_FSDK_GetVersion:" + mGenderVersion.toString() + "," + error1.getCode());

        mFRAbsLoop = new FRAbsLoop();
        mFRAbsLoop.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFRAbsLoop.shutdown();
        AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
        Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());

        ASAE_FSDKError err1 = mAgeEngine.ASAE_FSDK_UninitAgeEngine();
        Log.d(TAG, "ASAE_FSDK_UninitAgeEngine =" + err1.getCode());

        ASGE_FSDKError err2 = mGenderEngine.ASGE_FSDK_UninitGenderEngine();
        Log.d(TAG, "ASGE_FSDK_UninitGenderEngine =" + err2.getCode());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch(id){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //根据识别人脸识别结果来获取Person的回调函数
//    final Callback faceRequestCallBack=new Callback() {
//        @Override
//        public void onFailure(Call call, IOException e) {
//            myHandler.sendEmptyMessage(FACE_GET_PEOPLE_FAIL);
//            System.err.println("通过人脸获取Person请求出错");
//        }
//
//        @Override
//        public void onResponse(Call call, Response response) throws IOException {
//            myHandler.sendEmptyMessage(FACE_GET_PEOPLE_SUCCESS);
//            System.err.println("通过人脸获取Person请求成功");
//
//            if (response != null) {
//                String jsStr = response.body().string();//查看返回的response内容
//                System.err.println(jsStr);
//                try {
//                    JSONTokener jsonParser = new JSONTokener(jsStr);
//                    JSONObject noteResult = (JSONObject) jsonParser.nextValue();
//                    JSONObject joPersonLight = noteResult.getJSONObject("data");
//                    detectedPerson = PersonLight.fromJSONObject(joPersonLight);
//                    for(int i=0;i<notCheckPeople.size();i++){
//                        if(notCheckPeople.get(i).getpId()== detectedPerson.getpId()){
//                            PersonLight p=notCheckPeople.get(i);
//                            notCheckPeople.remove(i);
//                            checkedPeople.add(p);
//
//                            notCheckPeopleAdapter.setPersons((ArrayList)notCheckPeople);
//                            checkedPeopleAdapter.setPersons(checkedPeople);
//
//                            notCheckPeopleAdapter.notifyDataSetChanged();
//                            checkedPeopleAdapter.notifyDataSetChanged();
//                        }
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
////                toastHandler.sendEmptyMessage(REQUESTNULL);
//                System.err.println("null!");
//            }
//        }
//    };

    final Callback fillMeetingDetail = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

            //            toastHandler.sendEmptyMessage(REQUESTFAIL);
            System.err.println("请求出错");

        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
//            toastHandler.sendEmptyMessage(REQUESTSUCCESS);
            System.err.println("请求成功");
            if (response != null) {
                String jsStr = response.body().string();//查看返回的response内容
                System.err.println(jsStr);
                try {
                    JSONTokener jsonParser = new JSONTokener(jsStr);
                    JSONObject noteResult = (JSONObject) jsonParser.nextValue();
                    JSONObject joMeeting = noteResult.getJSONObject("data");
                    meetingDetail = Meeting.fromJSONObjectDetail(joMeeting);
                    notCheckPeople=meetingDetail.mAttendList;
                    notCheckPeopleAdapter.setPersons(notCheckPeople);
                    notCheckPeopleAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
//                toastHandler.sendEmptyMessage(REQUESTNULL);
                System.err.println("null!");
            }
        }
    };
}
