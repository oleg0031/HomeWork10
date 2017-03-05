package com.olegsmirnov.homework10;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import java.io.InputStream;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.tv_city) TextView tvCity;
    @BindView(R.id.tv_birthday) TextView tvBirthday;
    @BindView(R.id.tv_country) TextView tvCountry;
    @BindView(R.id.tv_books) TextView tvBooks;
    @BindView(R.id.tv_interests) TextView tvInterests;
    @BindView(R.id.tv_last_online) TextView tvLastSeen;
    @BindView(R.id.tv_about) TextView tvAbout;

    @BindView(R.id.image_login) ImageView ivLogin;
    @BindView(R.id.image_logout) ImageView ivLogout;
    @BindView(R.id.iv_avatar) ImageView ivAvatar;

    @OnClick(R.id.image_login)
    public void loginClick() {
        VKSdk.login(this);
    }

    @OnClick(R.id.image_logout)
    public void logoutClick() {
        VKSdk.logout();
        ivAvatar.setVisibility(View.INVISIBLE);
        ivLogout.setVisibility(View.INVISIBLE);
        ivLogin.setVisibility(View.VISIBLE);
        tvName.setText("");
        tvCity.setText("");
        tvBirthday.setText("");
        tvCountry.setText("");
        tvBooks.setText("");
        tvInterests.setText("");
        tvLastSeen.setText("");
        tvAbout.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                VKRequest currentRequest = new VKRequest("users.get", VKParameters.from(
                        VKApiConst.FIELDS, "city,photo_100,bdate,country,interests,last_seen,games,about,books,status"),
                        VKUsersArray.class);
                currentRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        VKUsersArray array = (VKUsersArray) response.parsedModel;
                        VKApiUserFull user = array.get(0);
                        Glide.with(getApplicationContext()).load(user.photo_100).into(ivAvatar);
                        ivAvatar.setVisibility(View.VISIBLE);
                        ivLogin.setVisibility(View.INVISIBLE);
                        ivLogout.setVisibility(View.VISIBLE);
                        tvName.setText(user.first_name + " " + user.last_name);
                        tvCity.setText(user.city.title);
                        tvCountry.setText(user.country.title);
                        tvBooks.setText("Favourite books: " + user.books);
                        tvBirthday.setText("Birthday: " + user.bdate);
                        tvLastSeen.setText("Last visit: " + new Date(1000 * user.last_seen + 7200000).toString().substring(4, 16)); //convert timestamp to readable format
                        tvInterests.setText("Interests:" + user.interests);
                        tvAbout.setText("About: " + user.about);
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
