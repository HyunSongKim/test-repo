package capstone.cs.holeinone;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;

public class SMSBroadCast extends BroadcastReceiver { //BroadcastReceiver Ŭ���� ����

	public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED"; //SMS ���ű���
	
    @Override
    public void onReceive(Context mContext, Intent intent) {
    	if(intent.getAction() == ACTION){
    		
    		//SMS �Ľ�
    		Bundle bundle = intent.getExtras();
    		Object messages[] = (Object[])bundle.get("pdus");
    		SmsMessage smsMessage[] = new SmsMessage[messages.length]; //messages.length�� object����! pdu�� ����!

    		for(int i = 0; i < messages.length; i++) {
    		   //PUD�������� �Ǿ� �ִ� �޼��� ����
    		    smsMessage[i] = SmsMessage.createFromPdu((byte[])messages[i]);
    		}
    		
    		//SMS ���� �ð�
    		Date curDate = new Date(smsMessage[0].getTimestampMillis());
    		SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy�� MM�� dd�� HH�� mm�� ss��", Locale.KOREA);
    		String originDate = mDateFormat.format(curDate); //��¥ ���� ��ȯ
    		
    		//�߽Ź�ȣ
    		String origNumber = smsMessage[0].getOriginatingAddress();
    		
    		//�߽Ź�ȣ�� ����ó�� ����� �̸����� ����
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(origNumber));
            String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME};
            String displayName = "";
                      
            Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()){
                    displayName = cursor.getString(0);              
                }        

                cursor.close();
            }
            
    		//�޼��� ����
    		String Message = smsMessage[0].getMessageBody().toString();
    		    		
    		Intent showSMSIntent = new Intent(mContext, ShowSMSActivity.class);
    		showSMSIntent.putExtra("originNum", displayName);
    		showSMSIntent.putExtra("smsDate", originDate);
    		showSMSIntent.putExtra("originText", Message);
    		
    		showSMSIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //��ε�ĳ��Ʈ���� Activity �����ϹǷ� setFlags �̿�
    		
    		mContext.startActivity(showSMSIntent);
    	}
    }
}