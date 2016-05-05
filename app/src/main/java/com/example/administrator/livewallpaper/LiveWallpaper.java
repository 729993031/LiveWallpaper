package com.example.administrator.livewallpaper;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.ViewDebug;
import android.view.WindowManager;

import java.util.Random;

/**
 * Created by Administrator on 2016/5/5.
 */
public class LiveWallpaper extends WallpaperService {
    private Bitmap heart;
    @Override
    public Engine onCreateEngine()
    {
        heart= BitmapFactory.decodeResource(getResources()
        ,R.drawable.heart);
        return new MyEngine();
    }
    class MyEngine extends Engine
    {
        private boolean mVisible;
        private float mTouchX=-1;
        private float mTouchY=-1;
        private int count=1;
        private int originX,originY=100;
        private int cubeHeight,cubeWidth;
        private Paint mPaint=new Paint();
        Handler mHandler=new Handler();
        private final Runnable drawTarget=new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };
        @Override
        public void onCreate(SurfaceHolder surfaceHolder)
        {
            super.onCreate(surfaceHolder);
            mPaint.setARGB(76,0,0,255);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            WindowManager wm=(WindowManager)LiveWallpaper.this.
                    getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dis=new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dis);
            originX=dis.widthPixels/3;
            cubeHeight=dis.widthPixels/4;
            cubeWidth=dis.widthPixels/8;
            setTouchEventsEnabled(true);
        }
        @Override
        public void onDestroy()
        {
            super.onDestroy();
            mHandler.removeCallbacks(drawTarget);
        }
        @Override
        public void onVisibilityChanged(boolean visible)
        {
            mVisible=visible;
            if (visible)
            {
                drawFrame();
            }
            else
            {
                mHandler.removeCallbacks(drawTarget);
            }
        }
        @Override
        public void onOffsetsChanged(float xOffset,float yOffset,float xStep,
                                     float yStep,int xPixels,int yPixels)
        {
            drawFrame();
        }
        @Override
        public void onTouchEvent(MotionEvent event)
        {
            if (event.getAction()==MotionEvent.ACTION_MOVE)
            {
                mTouchX=event.getX();
                mTouchY=event.getY();
            }
            else
            {
                mTouchX=-1;
                mTouchY=-1;
            }
            super.onTouchEvent(event);
        }
        private void drawFrame()
        {
            final SurfaceHolder holder=getSurfaceHolder();
            Canvas c=null;
            try {
                c=holder.lockCanvas();
                if (c!=null)
                {
                    c.drawColor(0xfffffff);
                    drawTouchPoint(c);
                    mPaint.setAlpha(76);
                    c.translate(originX,originY);
                    for (int i=0;i<count;i++)
                    {
                        c.translate(cubeHeight*2/3,0);
                        c.scale(0.95f,0.95f);
                        c.rotate(20f);
                        c.drawRect(0,0,cubeHeight,cubeWidth,mPaint);
                    }
                }
            }
            finally {
                if (c!=null)holder.unlockCanvasAndPost(c);
            }
            mHandler.removeCallbacks(drawTarget);
            if (mVisible)
            {
                count++;
                if (count>=0)
                {
                    Random rand=new Random();
                    count=1;
                    originX+=(rand.nextInt(60)-30);
                    originY+=(rand.nextInt(60)-30);
                    try {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                mHandler.postDelayed(drawTarget,100);
            }
        }
        private void drawTouchPoint(Canvas c)
        {
            if (mTouchX>=0&&mTouchY>=0)
            {
                mPaint.setAlpha(255);
                c.drawBitmap(heart,mTouchX,mTouchY,mPaint);
            }
        }
    }
}
