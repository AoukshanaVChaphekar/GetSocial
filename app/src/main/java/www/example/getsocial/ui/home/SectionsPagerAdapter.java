package www.example.getsocial.ui.home;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import www.example.getsocial.R;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_3,R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return  new post_fragment();
            case 1:
                return  new news_fragment();
            case 2:
                return new memes_fragment();
            default:
                return new post_fragment();

        }


    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;
        switch (position)
        {
            case 0:
                title="Posts";
                break;
            case 1:
                title="News";
                break;
            case 2:
                title="Memes";
                break;
        }
        return title;
      }

    @Override
    public int getCount() {
        return 3;
    }
}