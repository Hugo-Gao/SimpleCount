package tool;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.program.gyf.simplecount.BillItem;
import com.program.gyf.simplecount.R;

import java.util.List;

/**
 * Created by Administrator on 2017/1/18.
 */

public class BillViewAdapter extends RecyclerView.Adapter<BillViewAdapter.ViewHolder> implements View.OnClickListener
{
    private List<BillItem> itemList;
    private Context context;
    private LayoutInflater inflater;
    private onRecyclerViewItemClickListen listener;
    public BillViewAdapter(Context context, List<BillItem> itemList)
    {
        this.context = context;
        this.itemList = itemList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.each_bill_layout, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.billName.setText(itemList.get(position).getBillName());
        holder.billPic.setImageBitmap(
                BitmapHandler.convertByteToBitmap(itemList.get(position)
                        .getBillPic()));
        holder.itemView.setTag(itemList.get(position).getBillName());

    }

    @Override
    public int getItemCount()
    {
        return itemList.size();
    }

    @Override
    public void onClick(View v)
    {
        if(listener!=null)
        {
            listener.onitemView(v, (String) v.getTag());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private ImageView billPic;
        private TextView billName;
        public ViewHolder(View itemView)
        {
            super(itemView);
            billPic = (ImageView) itemView.findViewById(R.id.bill_pic);
            billName = (TextView) itemView.findViewById(R.id.bill_name);
        }
    }
    public interface onRecyclerViewItemClickListen
    {
        void onitemView(View view,String billName);
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListen listener)
    {
        this.listener = listener;
    }

}
