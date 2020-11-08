package com.example.dropdart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.dropdart.databinding.ActivityMainBinding;
import com.example.dropdart.notifications.Token;
import com.example.dropdart.utils.ConnectionCheckerApp;
import com.example.dropdart.utils.ConnectivityReceiver;
import com.example.dropdart.utils.InternetConnectionService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class DashboardActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    ActionBar actionbar;
    FirebaseAuth firebaseAuth;
    String myUid;
    LottieAnimationView lottieAnimationView, noNetworkAnimation;
    FrameLayout frameLayout;
    IntentFilter mIntentFilter;
    RelativeLayout noNetworkRelLaout;
    public static final String BroadcastStringForAction = "checkinternet";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (Build.VERSION.SDK_INT >=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorBlack));
            window.setNavigationBarColor(this.getResources().getColor(R.color.colorBlack));
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadcastStringForAction);
        Intent service = new Intent(this, InternetConnectionService.class);
        startService(service);

        noNetworkAnimation = findViewById(R.id.lottie_anim_no_internet_connection);
        noNetworkRelLaout = findViewById(R.id.noInternetRelLayout);
        noNetworkAnimation.playAnimation();

        if (!isOnline(getApplicationContext())){
            noNetworkRelLaout.setVisibility(View.VISIBLE);

        }
        else{
            noNetworkRelLaout.setVisibility(View.GONE);
        }

        actionbar = getSupportActionBar();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.colorBlack)));

        firebaseAuth = FirebaseAuth.getInstance();
        frameLayout = findViewById(R.id.content);
        lottieAnimationView = findViewById(R.id.lottie_anim);
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.setSpeed(2.5f);
        lottieAnimationView.playAnimation();

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                HomeFragment fragment1 = new HomeFragment();
                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.content, fragment1, "");
                ft1.commit();
                lottieAnimationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        checkUserStatus();

        updateToken(FirebaseInstanceId.getInstance().getToken());
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        navigationView.setItemIconSize(60);
    }
    public boolean isOnline(Context c){
        ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()){
            return true;
        }
        else
            return false;
    }
    public BroadcastReceiver MyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastStringForAction)){
                if (intent.getStringExtra("online_status").equals("true")){
                    noNetworkRelLaout.setVisibility(View.GONE);
                }
                else{
                    noNetworkRelLaout.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(MyReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(MyReceiver);
    }

    private void checkInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (activeNetwork==null){
            noNetworkAnimation.setVisibility(View.VISIBLE);
            noNetworkAnimation.playAnimation();
        }
        else{
            noNetworkAnimation.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
        registerReceiver(MyReceiver, mIntentFilter);
      //  final IntentFilter intentFilter = new IntentFilter();
    //    ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
  //      registerReceiver(connectivityReceiver, intentFilter);
//        ConnectionCheckerApp.getInstance().setConnectivityListener(this);
    }

    private void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(myUid).setValue(mToken);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            actionbar.setTitle("Home");
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    lottieAnimationView.setVisibility(View.GONE);
                                    HomeFragment fragment1 = new HomeFragment();
                                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                                    ft1.replace(R.id.content, fragment1, "");
                                    ft1.commit();
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });
                            return true;
                        case R.id.nav_profile:
                            actionbar.setTitle("Profile");
                            frameLayout.setBackgroundResource(R.color.colorBlack);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    lottieAnimationView.setVisibility(View.GONE);
                                    ProfileFragment fragment2 = new ProfileFragment();
                                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                                    ft2.replace(R.id.content, fragment2, "");
                                    ft2.commit();

                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });

                            return true;
                        case R.id.nav_users:
                            actionbar.setTitle("Users");
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    lottieAnimationView.setVisibility(View.GONE);
                                    UsersFragment fragment3 = new UsersFragment();
                                    FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                                    ft3.replace(R.id.content, fragment3, "");
                                    ft3.commit();
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });
                            return true;

                        case R.id.nav_chat:
                            actionbar.setTitle("Chats");
                            frameLayout.setBackgroundResource(R.color.colorBlack);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    UsersFragment fragment4 = new UsersFragment();
                                    FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                                    lottieAnimationView.setVisibility(View.GONE);
                                    ft4.replace(R.id.content, fragment4, "");
                                    ft4.commit();
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });
                            return true;
                    }

                    return false;
                }
            };

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            myUid = user.getUid();
            SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", myUid);
            editor.apply();

        }
        else{
            startActivity(new Intent(DashboardActivity.this, login.class));
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
       if (isConnected) {
           noNetworkAnimation.setVisibility(View.GONE);
       }
    }
}