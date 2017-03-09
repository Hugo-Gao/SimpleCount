package tool;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.program.gyf.simplecount.BillItem;
import com.program.gyf.simplecount.R;

import java.util.List;

import static tool.Utils.setupItem;

/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter implements View.OnClickListener
{

    private List<BillItem> LIBRARIES;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private onItemClickViewListener mListener;

    public HorizontalPagerAdapter(List<BillItem> libraries, final Context context)
    {
        LIBRARIES = libraries;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return LIBRARIES.size();
    }

    @Override
    public int getItemPosition(final Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position)
    {
        final View view;
        view = mLayoutInflater.inflate(R.layout.billlist_item, container, false);
        view.setOnClickListener(this);
        setupItem(view, LIBRARIES.get(position));
        container.addView(view);
        view.setTag(LIBRARIES.get(position).getBillName());
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object)
    {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public void onClick(View v)
    {
        if (mListener != null)
        {
            mListener.clickItem(v, (String) v.getTag());
        }
    }

    public interface onItemClickViewListener
    {
        void clickItem(View view, String billName);
    }

    public void setOnItemClickViewListener(onItemClickViewListener listener)
    {
        mListener = listener;
    }
}
