package commlab.weejang.demo.utils;

import java.security.PublicKey;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class EventHandler
{
		
		private static EventHandler mInstance;
		private ArrayList<Handler> mEventHandlers;
		
		/**
		 * singlon
		 * @return
		 */
		public static EventHandler getInstance(){
			if(mInstance == null){
				synchronized(EventHandler.class){
					if(mInstance == null){
						mInstance = new EventHandler();
					}
				}
			}
			return mInstance;
		}
		
		/**
		 * constructor
		 */
		private EventHandler()
		{
			mEventHandlers = new ArrayList<Handler>();
		}
		
		/**
		 * add 
		 */
		public void addHandler(Handler handler){
			if(!mEventHandlers.contains(handler)){
				mEventHandlers.add(handler);
			}
		}
			
		/**
		 * remove
		 */
		public void removeHandler(Handler handler){
			if(mEventHandlers .contains(handler)){
				mEventHandlers.remove(handler);
			}
		}
	
		/**
		 * post event
		 * @param
		 * 	eventData : {eventType:int;eventInfo:...}
		 */
		public void postEvent(Bundle eventData){
			for (Handler handler : mEventHandlers)
			{
				Message msg = handler.obtainMessage();
				msg.setData(eventData);
				handler.sendMessage(msg);
			}
		} 
			
}
