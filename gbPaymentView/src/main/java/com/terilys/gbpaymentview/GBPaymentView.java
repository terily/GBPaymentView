package com.terilys.gbpaymentview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 项目名称：GBPaymentView
 * 类描述：
 * 创建人：terilys
 * 创建时间：16/3/21 下午6:02
 * 修改人：terilys
 * 修改时间：16/3/21 下午6:02
 * 修改备注：
 *
 * @VERSION
 */
public class GBPaymentView extends View {

    private int passwordAmount;
    private FixedStack<Character> characters;
    private String[] passwordBoxes;

    private int borderColor;
    private float borderSize;
    private Paint borderPaint;

    private int textColor;
    private float textSize;
    private Paint textPaint;

    private OnCustomKeyboardListener onCustomKeyboardListener;

    private OnFinishInputListener onFinishInputListener;

    public GBPaymentView(Context context) {
        super(context);
        init(null);
    }

    public GBPaymentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GBPaymentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attributeset) {
        initDefaultAttributes();
        initCustomAttributes(attributeset);
        initPaint();
        initViewOptions();
        initDataStructures();
    }

    private void initDefaultAttributes() {
        passwordAmount = 6;
        borderColor = getContext().getResources().getColor(R.color.color_f3f3f3);
        borderSize = 4;

        textColor = getContext().getResources().getColor(R.color.color_666);
        textSize = 12;
    }

    private void initCustomAttributes(AttributeSet attributeset) {

        TypedArray attributes =
                getContext().obtainStyledAttributes(attributeset, R.styleable.GBPaymentView);
        passwordAmount = attributes.getInt(R.styleable.GBPaymentView_textAmount, passwordAmount);
        borderColor = attributes.getColor(R.styleable.GBPaymentView_borderColor, borderColor);
        borderSize = attributes.getDimension(R.styleable.GBPaymentView_borderSize, borderSize);

        textColor = attributes.getColor(R.styleable.GBPaymentView_textColor, textColor);
        textSize = attributes.getDimension(R.styleable.GBPaymentView_textSize, textSize);
        attributes.recycle();
    }

    private void initPaint() {
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderSize);
        borderPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setStrokeWidth(textSize);
        textPaint.setStyle(Paint.Style.FILL);
    }

    private void initDataStructures() {
        passwordBoxes = new String[passwordAmount];
        characters = new FixedStack<>();
        characters.setMaxSize(passwordAmount);
    }

    private void initViewOptions() {
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = measureDimension(dp2px((2 * columns + 1) * DEFAULT_SIZE), widthMeasureSpec);
//        int height;
//        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
//            height = (MeasureSpec.getSize(widthMeasureSpec) / (2 * columns + 1)) * (columns);
//        } else {
//            height = measureDimension(dp2px(columns * DEFAULT_SIZE), heightMeasureSpec);
//        }
//        Log.i(TAG, "width===" + width);
//        Log.i(TAG, "height===" + height);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (int) (((MeasureSpec.getSize(widthMeasureSpec) - ((passwordBoxes.length + 1) * borderSize)) / passwordBoxes.length) + borderSize * 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        float rectWidth = ((getWidth() - ((passwordBoxes.length + 1) * borderSize)) / passwordBoxes.length) + borderSize;
        float index = 0;
        float indexPoint = 0;

        for (int i = 0; i < passwordBoxes.length; i++) {
            //画矩形
            canvas.translate(index, 0);
            index = i == 0 ? rectWidth + borderSize : rectWidth;
            drawRect(canvas, i == 0 ? rectWidth + borderSize : rectWidth, rectWidth + borderSize, i == 0);
            if (characters.toArray().length > i && characters.size() != 0) {
                //保存原点位置
                canvas.save();
                //画圆点
                canvas.translate((rectWidth + borderSize) / 2, (rectWidth + borderSize) / 2);
                canvas.translate(indexPoint, 0);
                indexPoint = -borderSize / 2;
                drawText(canvas);
                //复原原点
                canvas.restore();
                if (onCustomKeyboardListener != null)
                    onCustomKeyboardListener.OnTextChange(characters.getText());
            }
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            requestFocus();
            if (onCustomKeyboardListener != null)
                onCustomKeyboardListener.OnCustomKeyboardOpen();
        }
        return super.onTouchEvent(event);
    }

    private void drawRect(Canvas canvas, float rectWidth, float rectHeight, Boolean isDrawleft) {

        //画上方的横线
        canvas.drawLine(0, 0, rectWidth, 0, borderPaint);
        //画右边的竖线
        canvas.drawLine(rectWidth, rectHeight, rectWidth, 0, borderPaint);
        //画下方的横线
        canvas.drawLine(rectWidth, rectHeight, 0, rectHeight, borderPaint);

        if (isDrawleft) {
            //画左边的竖线
            canvas.drawLine(0, rectHeight, 0, 0, borderPaint);
        }

    }

    private void drawText(Canvas canvas) {
        canvas.drawCircle(0, 0, textSize, textPaint);
    }

    public void setText(String text) {
        if (characters.size() == 5) {
            characters.push(text.charAt(0));
            if (onFinishInputListener != null)
                onFinishInputListener.onFinishInput(characters.getText());
        } else {
            characters.push(text.charAt(0));
        }
    }

    public String getText() {
        return characters.getText();
    }

    public void deleteText() {
        if (characters.size() != 0) {
            characters.pop();
        }
    }

    public void setCustomKeyboard(OnCustomKeyboardListener listener) {
        onCustomKeyboardListener = listener;
    }

    public void setOnFinishInputListener(OnFinishInputListener listener) {
        onFinishInputListener = listener;
    }

    public interface OnFinishInputListener {

        void onFinishInput(String text);

    }

    public interface OnCustomKeyboardListener {

        void OnCustomKeyboardOpen();

        void OnCustomKeyboardClose();

        void OnTextChange(String text);

    }
}

