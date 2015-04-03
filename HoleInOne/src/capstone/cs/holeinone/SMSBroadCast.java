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

public class SMSBroadCast extends BroadcastReceiver { //BroadcastReceiver 클래스 정의

	public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED"; //SMS 수신권한
	
    @Override
    public void onReceive(Context mContext, Intent intent) {
    	if(intent.getAction() == ACTION){
    		
    		//SMS 파싱
    		Bundle bundle = intent.getExtras();
    		Object messages[] = (Object[])bundle.get("pdus");
    		SmsMessage smsMessage[] = new SmsMessage[messages.length]; //messages.length는 object갯수! pdu의 갯수!

    		for(int i = 0; i < messages.length; i++) {
    		   //PUD포맷으로 되어 있는 메세지 복원
    		    smsMessage[i] = SmsMessage.createFromPdu((byte[])messages[i]);
    		}
    		
    		//SMS 수신 시간
    		Date curDate = new Date(smsMessage[0].getTimestampMillis());
    		SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.KOREA);
    		String originDate = mDateFormat.format(curDate); //날짜 형식 변환
    		
    		//발신번호
    		String origNumber = smsMessage[0].getOriginatingAddress();
    		
    		//발신번호를 연락처에 저장된 이름으로 변경
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
            
    		//메세지 내용
    		String Message = smsMessage[0].getMessageBody().toString();
    		    		
    		Intent showSMSIntent = new Intent(mContext, ShowSMSActivity.class);
    		showSMSIntent.putExtra("originNum", displayName);
    		showSMSIntent.putExtra("smsDate", originDate);
    		showSMSIntent.putExtra("originText", Message);
    		
    		showSMSIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //브로드캐스트에서 Activity 실행하므로 setFlags 이용
    		
    		mContext.startActivity(showSMSIntent);
    	}
    }
}