package tool;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.program.gyf.jianji.BillItem;
import com.program.gyf.jianji.R;

import java.util.List;

/**
 * Created by Administrator on 2017/4/21.
 */

public class ScrollViewAdapter extends RecyclerView.Adapter implements View.OnClickListener
{
    private Context context;
    private List<BillItem> billItemList;
    private LayoutInflater mInflater;
    private OnCardItemClickListenner onCardItemClickListenner;

    public ScrollViewAdapter(Context mcontext, List<BillItem> billItems)
    {
        billItemList = billItems;
        this.context = mcontext;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.billlist_item, parent, false);
        //返回到Holder
        view.setOnClickListener(this);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.imageView.setImageBitmap(billItemList.get(position).getBillBitmapPic(context));
        myHolder.itemView.setTag(R.id.back_btn, position);
    }


    public boolean deleteBill(String billName)
    {
        int position = -1;
        for (BillItem item:billItemList)
        {
            position++;
            if (item.getBillName().equals(billName))
            {
                break;
            }
        }
        Log.d("haha", "position is " + position);
        if (position > billItemList.size() - 1 || position < 0)
        {
            return false;
        }
        billItemList.remove(position);
        notifyItemRemoved(position);
        return true;
    }

    @Override
    public int getItemCount()
    {
        return billItemList.size();
    }

    @Override
    public void onClick(View v)
    {
        if (onCardItemClickListenner != null)
        {
            onCardItemClickListenner.onItemClick(v, (Integer) v.getTag(R.id.back_btn));
        }
    }

    public interface OnCardItemClickListenner
    {
        void onItemClick(View v, int position);
    }

    public void setOnCardItemClickListenner(OnCardItemClickListenner listenner)
    {
        onCardItemClickListenner = listenner;
    }

    public class MyHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView;

        public MyHolder(View itemView)
        {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_item);
        }
    }
}
