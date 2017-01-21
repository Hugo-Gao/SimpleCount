package com.program.gyf.simplecount;

/**
 * Created by Administrator on 2017/1/18.
 */

public class BillItem
{
    private String billName;
    private byte[] billPic;

    public String getBillName()
    {
        return billName;
    }

    public void setBillName(String billName)
    {
        this.billName = billName;
    }

    public byte[] getBillPic()
    {

        return billPic;
    }

    public void setBillPic(byte[] billPic)
    {
        this.billPic = billPic;
    }
}
