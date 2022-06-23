package com.example.earningapp.Model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.earningapp.R;
import com.example.earningapp.Spin.PieView;
import com.example.earningapp.Spin.SpinItem;

import java.util.List;

public class WheelView extends RelativeLayout implements PieView.pieRotateListener {

    private int mBackgroundColor,mTExtColor;
    private Drawable mCenterImage,mCursorImage;
    private PieView pieView;
    private ImageView imageCursor;

    private LuckyRoundItemSelectedListener itemSelectedListener;


    public void LuckyRoundItemSelectedListener(LuckyRoundItemSelectedListener itemSelectedListener)
    {
        this.itemSelectedListener=itemSelectedListener;
    }
    public interface LuckyRoundItemSelectedListener{
        void LuckyRoundItemSelectedListener(int index);
    }

    @Override
    public void rotateDone(int index) {

        if (itemSelectedListener!=null)
        {
            itemSelectedListener.LuckyRoundItemSelectedListener(index);
        }

    }

    public WheelView(Context context)
    {
        super(context);
        inits(context,null);
    }
    public WheelView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        inits(context,attributeSet);
    }

    private void inits(Context context,AttributeSet attributeSet)
    {

        if (attributeSet!=null)
        {
            TypedArray typedArray=context.obtainStyledAttributes(attributeSet, R.styleable.WheelView);
            mBackgroundColor=typedArray.getColor(R.styleable.WheelView_BackgroundColor,0xffffffff);
            mTExtColor=typedArray.getColor(R.styleable.WheelView_textColor,0xffffffff);

            mCursorImage=typedArray.getDrawable(R.styleable.WheelView_CursorImage);
            mCenterImage=typedArray.getDrawable(R.styleable.WheelView_CenterImage);

            typedArray.recycle();

        }

        LayoutInflater inflater=LayoutInflater.from(getContext());
        FrameLayout frameLayout=(FrameLayout) inflater.inflate(R.layout.wheel_layout,this,false);

         pieView=(PieView) frameLayout.findViewById(R.id.pieView);
         imageCursor=(ImageView) frameLayout.findViewById(R.id.cursorView);

         pieView.setPieRotateListener(this);
         pieView.setPieBackgroundColor(mBackgroundColor);
         pieView.setPieCenterImage(mCenterImage);
         pieView.setPieTextColor(mTExtColor);

         imageCursor.setImageDrawable(mCursorImage);

         addView(frameLayout);

    }

    public void setWheelBackgroundColor(int color)
    {
        pieView.setPieBackgroundColor(color);
    }

    public void setWheelCursorImage(int drawable){
        imageCursor.setBackgroundResource(drawable);
    }

    public void setWheelCenterImage(Drawable drawable){
        pieView.setPieCenterImage(drawable);
    }

    public void setWheelTextColor(int color){
        pieView.setPieTextColor(color);
    }
    public void setData(List<SpinItem> data){
        pieView.setData(data);
    }

    public void setRound(int numberOfRound){
        pieView.setRound(numberOfRound);
    }

    public void startWheelWithTargetIndex(int index){
        pieView.rotateTo(index);

    }
}
