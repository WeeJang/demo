package commlab.weejang.demo.aidl;

import commlab.weejang.demo.aidl.ClientMessage;

interface IMeasureService
{
	void collectClientMessage(int pid,in ClientMessage clientMessage);
}
