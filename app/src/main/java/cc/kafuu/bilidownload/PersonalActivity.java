package cc.kafuu.bilidownload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cc.kafuu.bilidownload.adapter.PersonalFragmentPagesAdapter;
import cc.kafuu.bilidownload.bilibili.Bili;
import cc.kafuu.bilidownload.bilibili.account.BiliFollow;
import cc.kafuu.bilidownload.fragment.personal.FollowFragment;
import cc.kafuu.bilidownload.fragment.personal.FavoriteFragment;
import cc.kafuu.bilidownload.fragment.personal.HistoryFragment;
import cc.kafuu.bilidownload.utils.DialogTools;

public class PersonalActivity extends BaseActivity {
    public static int RequestCode = 0x02;

    public static int ResultCodeLogout = 0x01;
    public static int ResultCodeVideoClicked = 0x02;

    //private CardView mLoginBiliCard;
    private ImageView mUserFace;
    private TextView mUserName;
    private TextView mUserSign;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initView();
    }

    public static void actionStartForResult(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), PersonalActivity.class);
        fragment.startActivityForResult(intent, RequestCode);
    }

    private void initView() {
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //mLoginBiliCard = findViewById(R.id.loginBiliCard);
        mUserFace = findViewById(R.id.userFace);
        mUserName = findViewById(R.id.userName);
        mUserSign = findViewById(R.id.userSign);

        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);

        mLogout = findViewById(R.id.logout);

        if (Bili.biliAccount == null) {
            finish();
            return;
        }

        //加载头像/昵称/个性签名
        Glide.with(this).load(Bili.biliAccount.getFace()).placeholder(R.drawable.ic_2233).into(mUserFace);
        mUserName.setText(Bili.biliAccount.getUserName());
        if (Bili.biliAccount.getSign() == null || Bili.biliAccount.getSign().length() == 0) {
            mUserSign.setText(getText(R.string.no_sign));
        } else {
            mUserSign.setText(Bili.biliAccount.getSign());
        }

        List<Pair<CharSequence, Fragment>> mFragments = new ArrayList<>();

        mFragments.add(new Pair<>(getString(R.string.history), HistoryFragment.newInstance()));
        mFragments.add(new Pair<>(getString(R.string.favorite), FavoriteFragment.newInstance()));

        mFragments.add(new Pair<>(getString(R.string.cartoon), FollowFragment.newInstance(BiliFollow.Type.Cartoon)));
        mFragments.add(new Pair<>(getString(R.string.teleplay), FollowFragment.newInstance(BiliFollow.Type.Teleplay)));


        mViewPager.setAdapter(new PersonalFragmentPagesAdapter(mFragments, getSupportFragmentManager(),  FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        mViewPager.setOffscreenPageLimit(Objects.requireNonNull(mViewPager.getAdapter()).getCount());

        mTabLayout.setupWithViewPager(mViewPager, false);

        mLogout.setOnClickListener(v -> logout());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 登出
     * */
    private void logout() {
        DialogTools.confirm(this, getString(R.string.exit_login), getString(R.string.exit_login_confirm), (dialog, which) -> {
            setResult(ResultCodeLogout);
            finish();
        }, null);

    }

}

