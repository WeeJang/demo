package commlab.weejang.demo.aidl;

import commlab.weejang.demo.db.MeasureData;
import commlab.weejang.demo.db.ClientMessage;

interface IMeasureService
{
	void collectClientMessage(int pid,ClientMessage clientMessage);
}
