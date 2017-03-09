package tool;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.program.gyf.simplecount.BillItem;
import com.program.gyf.simplecount.R;

/**
 * Created by GIGAMOLE on 8/18/16.
 */
public class Utils
{

    public static void setupItem(final View view, final BillItem item) {
        final TextView txt = (TextView) view.findViewById(R.id.txt_item);
        txt.setText(item.getBillName());

        final ImageView img = (ImageView) view.findViewById(R.id.img_item);
        img.setImageBitmap(BitmapHandler.convertByteToBitmap(item.getBillPic()));
    }


}
