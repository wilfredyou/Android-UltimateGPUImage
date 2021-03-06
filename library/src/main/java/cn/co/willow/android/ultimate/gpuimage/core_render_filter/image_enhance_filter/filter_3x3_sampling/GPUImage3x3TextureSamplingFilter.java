package cn.co.willow.android.ultimate.gpuimage.core_render_filter.image_enhance_filter.filter_3x3_sampling;

import android.opengl.GLES30;

import cn.co.willow.android.ultimate.gpuimage.core_render_filter.GPUImageFilter;

/**
 * 3*3纹理采样滤镜基类
 */
public class GPUImage3x3TextureSamplingFilter extends GPUImageFilter {

    public static final String THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "\n" +
            "uniform highp float texelWidth; \n" +
            "uniform highp float texelHeight; \n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "varying vec2 leftTextureCoordinate;\n" +
            "varying vec2 rightTextureCoordinate;\n" +
            "\n" +
            "varying vec2 topTextureCoordinate;\n" +
            "varying vec2 topLeftTextureCoordinate;\n" +
            "varying vec2 topRightTextureCoordinate;\n" +
            "\n" +
            "varying vec2 bottomTextureCoordinate;\n" +
            "varying vec2 bottomLeftTextureCoordinate;\n" +
            "varying vec2 bottomRightTextureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "\n" +
            "    vec2 widthStep = vec2(texelWidth, 0.0);\n" +
            "    vec2 heightStep = vec2(0.0, texelHeight);\n" +
            "    vec2 widthHeightStep = vec2(texelWidth, texelHeight);\n" +
            "    vec2 widthNegativeHeightStep = vec2(texelWidth, -texelHeight);\n" +
            "\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "    leftTextureCoordinate = inputTextureCoordinate.xy - widthStep;\n" +
            "    rightTextureCoordinate = inputTextureCoordinate.xy + widthStep;\n" +
            "\n" +
            "    topTextureCoordinate = inputTextureCoordinate.xy - heightStep;\n" +
            "    topLeftTextureCoordinate = inputTextureCoordinate.xy - widthHeightStep;\n" +
            "    topRightTextureCoordinate = inputTextureCoordinate.xy + widthNegativeHeightStep;\n" +
            "\n" +
            "    bottomTextureCoordinate = inputTextureCoordinate.xy + heightStep;\n" +
            "    bottomLeftTextureCoordinate = inputTextureCoordinate.xy - widthNegativeHeightStep;\n" +
            "    bottomRightTextureCoordinate = inputTextureCoordinate.xy + widthHeightStep;\n" +
            "}";

    private int mUniformTexelWidthLocation;
    private int mUniformTexelHeightLocation;

    private boolean mHasOverriddenImageSizeFactor = false;
    private float mTexelWidth;
    private float mTexelHeight;
    private float mLineSize = 1.0f;

    public GPUImage3x3TextureSamplingFilter() {
        this(NO_FILTER_VERTEX_SHADER);
    }

    public GPUImage3x3TextureSamplingFilter(final String fragmentShader) {
        super(THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER, fragmentShader);
    }


    /*生命周期======================================================================================*/
    @Override
    public void onInit() {
        super.onInit();
        mUniformTexelWidthLocation = GLES30.glGetUniformLocation(getProgram(), "texelWidth");
        mUniformTexelHeightLocation = GLES30.glGetUniformLocation(getProgram(), "texelHeight");
        if (mTexelWidth != 0) {
            updateTexelValues();
        }
    }

    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        if (!mHasOverriddenImageSizeFactor) {
            setLineSize(mLineSize);
        }
    }


    /*参数方法======================================================================================*/
    public void setTexelWidth(final float texelWidth) {
        mHasOverriddenImageSizeFactor = true;
        mTexelWidth = texelWidth;
        setFloat(mUniformTexelWidthLocation, texelWidth);
    }

    public void setTexelHeight(final float texelHeight) {
        mHasOverriddenImageSizeFactor = true;
        mTexelHeight = texelHeight;
        setFloat(mUniformTexelHeightLocation, texelHeight);
    }

    public void setLineSize(final float size) {
        mLineSize = size;
        mTexelWidth = size / getOutputWidth();
        mTexelHeight = size / getOutputHeight();
        updateTexelValues();
    }

    private void updateTexelValues() {
        setFloat(mUniformTexelWidthLocation, mTexelWidth);
        setFloat(mUniformTexelHeightLocation, mTexelHeight);
    }
}
