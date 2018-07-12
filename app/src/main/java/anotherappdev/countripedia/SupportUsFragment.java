package anotherappdev.countripedia;

import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class SupportUsFragment extends Fragment implements View.OnClickListener {
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_support_us, container, false);

        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("prefTheme", false)) {
            invert(R.id.btn_fb);
            invert(R.id.btn_twit);
            invert(R.id.btn_gplus);
            invert(R.id.btn_git);
        }

        ImageButton fb, twitter, git, gplus;
        fb = view.findViewById(R.id.btn_fb);
        fb.setOnClickListener(this);
        twitter = view.findViewById(R.id.btn_twit);
        twitter.setOnClickListener(this);
        git = view.findViewById(R.id.btn_git);
        git.setOnClickListener(this);
        gplus = view.findViewById(R.id.btn_gplus);
        gplus.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        String link;

        switch (view.getId()) {
            case R.id.btn_fb:
                link = "https://www.facebook.com/anotherapplicationdeveloper";
                break;

            case R.id.btn_twit:
                link = "https://twitter.com/anotherappdev";
                break;

            case R.id.btn_gplus:
                link = "https://plus.google.com/u/0/116584483256160099917";
                break;

            case R.id.btn_git:
                link = "https://www.github.com/anotherapplicationdeveloper";
                break;

            //case R.id.patreon:
            //  link = "https://www.patreon.com/anotherapplicationdeveloper";
            //break;

            default:
                link = null;
                break;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);

    }

    void invert(int imgViewId) {
        ImageView imageView = view.findViewById(imgViewId);
        Drawable drawable = imageView.getDrawable();

        final float[] NEGATIVE = {
                -1.0f, 0, 0, 0, 255, // red
                0, -1.0f, 0, 0, 255, // green
                0, 0, -1.0f, 0, 255, // blue
                0, 0, 0, 1.0f, 0  // alpha
        };

        imageView.setColorFilter(new ColorMatrixColorFilter(NEGATIVE));
        imageView.setImageDrawable(drawable);
    }
}
