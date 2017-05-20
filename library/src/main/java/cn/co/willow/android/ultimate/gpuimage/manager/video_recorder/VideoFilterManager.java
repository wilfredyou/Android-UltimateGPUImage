package cn.co.willow.android.ultimate.gpuimage.manager.video_recorder;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;

import cn.co.willow.android.ultimate.gpuimage.core_config.OutputConfig;
import cn.co.willow.android.ultimate.gpuimage.core_config.RecordCoderState;
import cn.co.willow.android.ultimate.gpuimage.core_config.Rotation;
import cn.co.willow.android.ultimate.gpuimage.core_render.VideoRecorderRenderer;
import cn.co.willow.android.ultimate.gpuimage.core_render.BaseRenderer;
import cn.co.willow.android.ultimate.gpuimage.core_render_filter.GPUImageFilter;

/**
 * 视频录制滤镜管理器
 * <p>
 * Created by willow.li on 2016/10/26.
 */
public class VideoFilterManager {

    private VideoRecorderRenderer mRenderer;
    private GLSurfaceView         mGlSurfaceView;
    private GPUImageFilter        mFilter;


    /*初始化流程====================================================================================*/
    public VideoFilterManager(Context context) {
        supportsOpenGLES3(context);
    }

    /** 设置基本录制参数 */
    void initManager(OutputConfig.VideoOutputConfig mVideoConfig,
                     OutputConfig.AudioOutputConfig mAudioConfig) {
        mRenderer = new VideoRecorderRenderer(
                new GPUImageFilter(),
                mVideoConfig,
                mAudioConfig);
    }

    /** 初始化GLSurfaceView */
    void setGLSurfaceView(final GLSurfaceView view) {
        mGlSurfaceView = view;
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGlSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mGlSurfaceView.setRenderer(mRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGlSurfaceView.requestRender();
    }

    /** 设置相机，并切换角度 */
    void setUpCamera(final Camera camera, boolean isFrontCame) {
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mRenderer.setUpSurfaceTexture(camera);
        mRenderer.setRotation(isFrontCame ? Rotation.ROTATION_270 : Rotation.ROTATION_90, false, isFrontCame);
    }


    /*关键设置======================================================================================*/
    /** 检测是否支持OpenGl */
    private void supportsOpenGLES3(final Context context) {
        final ActivityManager   activityManager   = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        if (configurationInfo.reqGlEsVersion < 0x30000) {
            throw new IllegalStateException("OpenGL ES 3.0 is not supported on this phone.");
        }
    }

    /** 设置滤镜 */
    public void setFilter(final GPUImageFilter filter) {
        mFilter = filter;
        mRenderer.setFilter(mFilter);
        requestRender();
    }

    /** 请求刷新渲染器 */
    public void requestRender() {
        if (mGlSurfaceView != null) {
            mGlSurfaceView.requestRender();
        }
    }


    /*录制逻辑======================================================================================*/
    public RecordCoderState getCurrentState() {
        return mRenderer.getCurrentState();
    }

    public void create(File mOutputRecFile,
                       OutputConfig.VideoOutputConfig videoConfig,
                       OutputConfig.AudioOutputConfig audioConfig) {
        mRenderer.prepareCoder(mOutputRecFile, videoConfig, audioConfig);
    }

    public void start() {
        mRenderer.changeCoderState(true);
    }

    public void stop() {
        mRenderer.changeCoderState(false);
    }

    public void release() {
        mRenderer.releaseCoder();
    }

    public void destory() {
        mRenderer.clearAll();
        mRenderer = null;
    }


    /*渲染流程关联性监听===============================================================================*/
    public void setOnSurfaceSetListener(BaseRenderer.OnSurfaceSetListener mOnSurfaceSetListener) {
        mRenderer.setOnSurfaceSetListener(mOnSurfaceSetListener);
    }

    public void setOnRecordStateListener(VideoRecorderRenderer.OnRecordStateListener mOnRecordStateListener) {
        mRenderer.setOnRecordStateListener(mOnRecordStateListener);
    }

}