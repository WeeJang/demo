package commlab.weejang.demo.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * IPC 通信，客户端消息类
 * @author jangwee
 *
 */
public class ClientMessage extends MeasureData implements Parcelable
{

	public ClientMessage(long timeStamp, String dataType, String dataInfo)
	{
		super(timeStamp, dataType, dataInfo);
		// TODO Auto-generated method stub
	}

	public static final Parcelable.Creator<ClientMessage> CEATOR= new Parcelable.Creator<ClientMessage>(){

		@Override
		public ClientMessage createFromParcel(Parcel source)
		{
			return new ClientMessage(source.readLong(), source.readString(), source.readString());
		}

		@Override
		public ClientMessage[] newArray(int size)
		{
			return new ClientMessage[size];
		}
		
	};
	
	
	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		// TODO Auto-generated method stub
		dest.writeLong(timeStamp);
		dest.writeString(dataType);
		dest.writeString(dataInfo);
	}
	
	

}
