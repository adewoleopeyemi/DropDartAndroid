package com.example.dropdart.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.dropdart.R;
import com.example.dropdart.models.ModelPost;
import com.example.dropdart.theirProfileActivity;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.Calendar;
import java.util.Locale;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class postsViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {

    public View parent;
    public ImageView uPictureIv,pLikes, pThumbNail, albumCover;
    public TextView uNameTv, likeTv, ShareTv, CommentTv;
    public ImageView commentIv, shareIv;
    public RelativeLayout relativeLayout;
    public FrameLayout relVidView;
    public LottieAnimationView lottieAnimationView;
    public PlayerView playerView;
    public RequestManager requestManager;
    BlurView blurView;
    GestureDetector gestureDetector;
    private OverScroller mScroller;
    private ReadMoreTextView pTitleTv;

    public postsViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        gestureDetector = new GestureDetector(itemView.getContext(), this);
        albumCover = itemView.findViewById(R.id.albumCover);
        relVidView = itemView.findViewById(R.id.VideoHolder);
        uPictureIv = itemView.findViewById(R.id.uPictureIv);
        playerView = itemView.findViewById(R.id.pImageIv);
        uNameTv = itemView.findViewById(R.id.uNameTv);
        pTitleTv = itemView.findViewById(R.id.pTitleTv);
        pLikes = itemView.findViewById(R.id.loveCardView);
        likeTv = itemView.findViewById(R.id.LikesInNNumber);
        ShareTv = itemView.findViewById(R.id.SharesInNNumber);
        CommentTv = itemView.findViewById(R.id.CommentsInNNumber);
        commentIv = itemView.findViewById(R.id.Comment);
        shareIv = itemView.findViewById(R.id.Share);
        relativeLayout = itemView.findViewById(R.id.RelLayoutPost);
        lottieAnimationView = itemView.findViewById(R.id.RelVidView);
        pThumbNail = itemView.findViewById(R.id.pThumbNail);
        blurView = itemView.findViewById(R.id.blurredLayout);
        mScroller = new OverScroller(itemView.getContext());
    }

    public void onBind(ModelPost post, RequestManager requestManager, Context context) {
        this.requestManager = requestManager;
        parent.setTag(this);
        String uid = post.getUid();
        String uEmail = post.getuEmail();
        String uName = post.getuName();
        String uDp = post.getuDp();
        String pId = post.getpId();
        String pTitle = post.getptitle();
        String pVideo = post.getpVideo();
        String pTimeStamp = post.getpTime();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        itemView.setOnTouchListener(this);
        lottieAnimationView.playAnimation();
        uNameTv.setText(uName);
        pTitleTv.setText(pTitle);
        this.requestManager.load(uDp).into(uPictureIv);


        int repeat = 100000;
        this.requestManager.load(pVideo).thumbnail(0.1f).into(pThumbNail);
        this.requestManager.load(pVideo).thumbnail(0.1f).into(albumCover);
        Animation rotationAnimation;
        rotationAnimation = AnimationUtils.loadAnimation(context, R.anim.rotation);
        rotationAnimation.setRepeatCount(Animation.INFINITE);
        albumCover.startAnimation(rotationAnimation);
        pLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });
        CommentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });
        ShareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
            }
        });
        uNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, theirProfileActivity.class);
                intent.putExtra("hisUid", uid);
                context.startActivity(intent);
            }
        });
    }

    private void blurBackground(Context context) {
        float radius = 25f;

        View decorView =((Activity) context).getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(context))
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(true);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // Before flinging, abort the current animation.
        return true;
    }
}
