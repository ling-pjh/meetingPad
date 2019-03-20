package com.arcsoft.sdk_demo;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.example.meetingpad.HTTPService.OKHTTP;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

//a

/**
 * Created by gqj3375 on 2017/7/11.
 */

public class FaceDB {
	private static final String TAG = "FaceDB";

	public static String appid = "DenAtmLVpBnLbbv442zXhDV1PsRA7Qe6uQaB8c3siJkj";
	public static String ft_key = "Usho1DLSAk4nF2PPz6T1GprVHXY8ihJKtkM1fH2DN9g";
	public static String fd_key = "Usho1DLSAk4nF2PPz6T1Gpyegnky6gTUrjtU7MZCBGU";
	public static String fr_key = "Usho1DLSAk4nF2PPz6T1GqUJHqPPkHXtncHCAZRCguP";
	public static String age_key = "Usho1DLSAk4nF2PPz6T1Gqid6MiAcUJDC55LoAmwGvm";
	public static String gender_key = "Usho1DLSAk4nF2PPz6T1GqqnVcspV4EXr86nqD3MY7x";

	static String mDBPath;
	static List<FaceRegist> mRegister = new ArrayList<FaceRegist>();
	static AFR_FSDKEngine mFREngine;
	static AFR_FSDKVersion mFRVersion;
	static boolean mUpgrade;

	static class FaceRegist {
		String mName;
		Map<String, AFR_FSDKFace> mFaceList;

		public FaceRegist(String name) {
			mName = name;
			mFaceList = new LinkedHashMap<>();
		}
	}

	public FaceDB(String path) {
		mDBPath = path;
		mRegister = new ArrayList<>();
		mFRVersion = new AFR_FSDKVersion();
		mUpgrade = false;
		mFREngine = new AFR_FSDKEngine();
		AFR_FSDKError error = mFREngine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
		if (error.getCode() != AFR_FSDKError.MOK) {
			Log.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
		} else {
			mFREngine.AFR_FSDK_GetVersion(mFRVersion);
			Log.d(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString());
		}
	}

	public static void destroy() {
		if (mFREngine != null) {
			mFREngine.AFR_FSDK_UninitialEngine();
		}
	}

	private static boolean saveInfo() {
		try {
			FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt");
			ExtOutputStream bos = new ExtOutputStream(fs);
			bos.writeString(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel());
			bos.close();
			fs.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean loadInfo() {
		if (!mRegister.isEmpty()) {
			return false;
		}
		try {
			FileInputStream fs = new FileInputStream(mDBPath + "/face.txt");
			ExtInputStream bos = new ExtInputStream(fs);
			//load version
			String version_saved = bos.readString();
			if (version_saved.equals(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel())) {
				mUpgrade = true;
			}
			//load all regist name.
			if (version_saved != null) {
				for (String name = bos.readString(); name != null; name = bos.readString()){
					if (new File(mDBPath + "/" + name + ".data").exists()) {
						mRegister.add(new FaceRegist(new String(name)));
					}
				}
			}
			bos.close();
			fs.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean loadFaces(){
		if (loadInfo()) {
			try {
				for (FaceRegist face : mRegister) {
					Log.d(TAG, "load name:" + face.mName + "'s face feature data.");
					FileInputStream fs = new FileInputStream(mDBPath + "/" + face.mName + ".data");
					ExtInputStream bos = new ExtInputStream(fs);
					AFR_FSDKFace afr = null;
					do {
						if (afr != null) {
							if (mUpgrade) {
								//upgrade data.
							}
							String keyFile = bos.readString();
							face.mFaceList.put(keyFile, afr);
						}
						afr = new AFR_FSDKFace();
					} while (bos.readBytes(afr.getFeatureData()));
					bos.close();
					fs.close();
					Log.d(TAG, "load name: size = " + face.mFaceList.size());
				}
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}



    public	static void addFace(final String name, final AFR_FSDKFace face, Bitmap faceicon) {
        //试试发送给服务器
        new Thread() {
            public void run() {
                try {
					String base64Str = Base64.encodeToString(face.getFeatureData(), Base64.NO_WRAP);
//					String base64Str = Base64.encodeToString("啊啊啊啊".getBytes(), Base64.DEFAULT);
					System.out.println("base46Str="+base64Str);
					HashMap<String,String> map= new HashMap<>();
					map.put("pId",name);
					map.put("pFace",base64Str);
                    //表单提交
                    OKHTTP.postForm("/user/updateFace.do", map,new okhttp3.Callback() {
						@Override
						public void onFailure(Call call, IOException e) {
							//失败了怎么做
							System.err.println("失败test");
							e.printStackTrace();
						}
						@Override
						public void onResponse(Call call, Response response) throws IOException {
							//成功了怎么做
							System.err.println("成功");
							if (response != null) {
								System.err.println(response.body().string());//查看返回的response内容
							} else {
								System.err.println("null!");
							}
						}
					});
                    //根据响应内容做处理
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

	}

	public static boolean delete(String name) {
		try {
			//check if already registered.
			boolean find = false;
			for (FaceRegist frface : mRegister) {
				if (frface.mName.equals(name)) {
					File delfile = new File(mDBPath + "/" + name + ".data");
					if (delfile.exists()) {
						delfile.delete();
					}
					mRegister.remove(frface);
					find = true;
					break;
				}
			}

			if (find) {
				if (saveInfo()) {
					//update all names
					FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
					ExtOutputStream bos = new ExtOutputStream(fs);
					for (FaceRegist frface : mRegister) {
						bos.writeString(frface.mName);
					}
					bos.close();
					fs.close();
				}
			}
			return find;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean upgrade() {
		return false;
	}
}
