package tool;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.program.gyf.jianji.BillBean;
import com.program.gyf.jianji.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/28.
 */

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.MyHolder> implements View.OnClickListener
{
    private List<BillBean> beanList;
    private Context context;
    private LayoutInflater mInflater;
    private onRecyclerViewItemClickListen mOnItemClickListen=null;
    private int[] cutePicArray = new int[]{R.drawable.icecream01, R.drawable.icecream02,
            R.drawable.icecream03, R.drawable.icecream04,
            R.drawable.icecream05,R.drawable.icecream06,
            R.drawable.icecream07,R.drawable.icecream08};


    public CardViewAdapter(List<BillBean> beenList, Context context)
    {
        this.beanList = beenList;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }



    /**
     * 找View
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.card_layout, parent,false);
        //返回到Holder
        view.setOnClickListener(this);
        return new MyHolder(view);
    }

    /**
     * 显示控件内容
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyHolder holder, int position)
    {
        BillBean bean = beanList.get(position);
        holder.picInfo.setImageURI(Uri.parse(bean.getMiniPicAddress()));
        holder.dateInfo.setText(bean.getDateInfo());
        holder.itemView.setTag(bean);
        int cuteID=position%cutePicArray.length;
        holder.cuteInfo.setImageResource(cutePicArray[cuteID]);
        holder.itemView.setTag(R.id.pic_address,holder.picInfo);
    }

    @Override
    public int getItemCount()
    {
        return beanList.size();
    }
    public void addItem(int position, BillBean bean)
    {
        beanList.add(position, bean);
        notifyItemInserted(position);
    }
    public void deleteItem(int position)
    {
        beanList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onClick(View v)
    {
        if(mOnItemClickListen!=null)
        {
            mOnItemClickListen.onItemClick(v,(BillBean) v.getTag(), (ImageView) v.getTag(R.id.pic_address));
        }
    }



    /**
     * 给控件绑定布局
     */
    public class MyHolder extends RecyclerView.ViewHolder
    {

        private TextView dateInfo;
        private ImageView picInfo;
        private ImageView cuteInfo;
        public MyHolder(View itemView)
        {
            super(itemView);
            dateInfo = (TextView) itemView.findViewById(R.id.date_bill);
            Typeface customFont = Typeface.createFromAsset(context.getAssets(), "SourceHanSansCN-Light.ttf");
            dateInfo.setTypeface(customFont);
            picInfo = (ImageView) itemView.findViewById(R.id.pic_address);
            cuteInfo = (ImageView) itemView.findViewById(R.id.cute_Pic);
        }
    }


    public  interface onRecyclerViewItemClickListen
    {
        void onItemClick(View view, BillBean bean,ImageView imageView);
    }
    public void setOnItemClickListener(onRecyclerViewItemClickListen listener)
    {
        mOnItemClickListen = listener;
    }


}

