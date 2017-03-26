package com.program.gyf.simplecount;

/**
 * Created by Administrator on 2016/9/28.
 */

public class BillBean
{
    private int money;
    private String name;
    private String dateInfo;
    private String descripInfo;
    private byte[] picInfo;
    private byte[] oldpicInfo;
    private String picadress;
    private String miniPicAddress = null;

    private String picString=null;
    private String oldpicString=null;
    public String getOldpicString()
    {
        return oldpicString;
    }

    public void setOldpicString(String oldpicString)
    {
        this.oldpicString = oldpicString;
    }

    public String getPicString()
    {
        return picString;
    }

    public void setPicString(String picString)
    {
        this.picString = picString;
    }

    public String getPicadress()
    {
        return picadress;
    }

    public void setPicadress(String picadress)
    {
        this.picadress = picadress;
    }

    public BillBean(String dateInfo, byte[] picInfo, byte[] oldpicInfo,String descripInfo, String name, int money,String address)
    {
        this.dateInfo = dateInfo;
        this.picInfo = picInfo;
        this.descripInfo = descripInfo;
        this.name = name;
        this.money = money;
        this.oldpicInfo = oldpicInfo;
        this.picadress = address;
    }

    public BillBean()
    {
    }

    public String getDateInfo()
    {
        return dateInfo;
    }

    public void setDateInfo(String dateInfo)
    {
        this.dateInfo = dateInfo;
    }

    public String getDescripInfo()
    {
        return descripInfo;
    }

    public void setDescripInfo(String descripInfo)
    {
        this.descripInfo = descripInfo;
    }

    public byte[] getPicInfo()
    {
        return picInfo;
    }

    public void setPicInfo(byte[] picInfo)
    {
        this.picInfo = picInfo;
    }
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public int getMoney()
    {
        return money;
    }

    public String getMoneyString()
    {
        return Integer.toString(money);
    }

    public void setMoney(int money)
    {
        this.money = money;
    }
    public String toString()
    {
        return "name is " + name
                + "  Money" + getMoneyString()
                + " describe is " + descripInfo
                + "  date info is" + dateInfo
                +" pic info is"+picInfo
                +" old pic info is "+oldpicInfo;
    }
    public byte[] getOldpicInfo()
    {
        return oldpicInfo;
    }

    public void setOldpicInfo(byte[] oldpicInfo)
    {
        this.oldpicInfo = oldpicInfo;
    }

    public String getMiniPicAddress()
    {
        return miniPicAddress;
    }

    public void setMiniPicAddress(String miniPicAddress)
    {
        this.miniPicAddress = miniPicAddress;
    }
}
