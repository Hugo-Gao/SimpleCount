package tool;

/**
 * Created by Administrator on 2017/3/24.
 */

public  class ServerIP
{
    private static final String HOST = "192.168.253.1:8060";

    public static final String LOGURL="http://"+HOST+"/user";
    public static final String SIGNURL="http://"+HOST+"/register";
    public static final String POSTURL = "http://"+HOST+"/postdata";
    public static final String GETBILLSNAMEURL = "http://"+HOST+"/getbillsname";
    public static final String POSTBILLNAMEURL = "http://"+HOST+"/postBillList";
    public static final String GETDATAURL = "http://"+HOST+"/getdata";
    //public static final String POST_TO_FRIEND_URL="http://"+HOST+"/posttofriend";
    public static final String POST_TO_FRIEND_URL=POSTURL;
    public static final String CHECK_ID_EXISTED="http://"+HOST+"/checkidexisted";
    public static final String HAS_NEW_MESSAGE_URL="http://"+HOST+"/hasnewmessage";
    public static final String DELETE_SERVER_BILLNAME_URL="http://"+HOST+"/deletebillname";
    public static final String TESTURL = "http://"+HOST;
}
