#include "HTTPGet.h"
#include <android/log.h>

#define TAG  "----from---jni---"
#define M_ 1024*1024
#define K_ 1024


int GetHttpResponseHead(int sock , char *buf , int size)
{
	int i;
	char *code,*status;
	memset(buf,0,size);

	for(i = 0 ; i < size ; ++ i)
	{
		if( recv( sock , buf + i , 1 , 0 ) != 1)
		{
			perror("recv error");
			return -1;
		}
		if( strstr(buf, "\r\n\r\n") )
			break;
	}

	if( i >= size -1)
	{
		puts("Http response head too long ");
		return -2;
	}

	code = strstr(buf, " 200 ");
	status = strstr(buf, "\r\n");

	if( !code || code > status )
	{
		*status = 0 ;
		printf("Bad http response : \" %s \"\n",buf);
		return -3;
	}

	return i;
}

/**
 * HttpGet 测试
 *
 *
 */
int HttpGet(const char *server,const char *url,struct simple_tcp_info *psimple_tcp_info,int len_simple_tcp_info)
{
	__android_log_print(ANDROID_LOG_INFO,TAG,"Enter HttpGet Function!");

	struct sockaddr_in peerAddr;
	char *headBuf,*recvBuf;
	int sock,ret;
	int recv_data_count  = 0 ; //记录获取次数

	headBuf = (char*)malloc(K_*sizeof(char));
	recvBuf = (char*)malloc(M_*sizeof(char));

	sock= socket( PF_INET, SOCK_STREAM,0);

	__android_log_print(ANDROID_LOG_INFO,TAG,"%d",sock);

	peerAddr.sin_family = AF_INET;
	peerAddr.sin_port = htons(80);
	peerAddr.sin_addr.s_addr = inet_addr(server);

	ret = connect(sock, (struct sockaddr *)&peerAddr,sizeof(peerAddr));

	if(ret != 0 ){
		perror("connect failed");
		close(sock);
		return -1;
	}

	snprintf(headBuf,K_*sizeof(char),
			"GET %s HTTP/1.1\r\n"
			"Accept: */*\r\n"
			"User-Agent: jangwee1@sina.com.cn\r\n"
			"Host: %s\r\n"
			"Connection: Close\r\n\r\n",
			url,server);

	send(sock,headBuf,strlen(headBuf),0);

	if(GetHttpResponseHead(sock,headBuf,K_*sizeof(char)) < 1)
	{
		close(sock);
		return -1;
	}

	__android_log_print(ANDROID_LOG_INFO,TAG,"Ente HttpGet function's while !");

	struct tcp_info *ptcp_info = (struct tcp_info*)malloc(sizeof(struct tcp_info));
	socklen_t len_info = sizeof(struct tcp_info);

	while( (ret = recv(sock,recvBuf,M_*sizeof(recvBuf),0)) > 0)
	{
		__android_log_print(ANDROID_LOG_INFO,TAG,"Ente HttpGet function's .................. : %d",ret);

		//判断是否获取足够数据
		if( recv_data_count >= len_simple_tcp_info )
		{
			break;
		}
		//get tcp_info
		if(getsockopt(sock,SOL_TCP,TCP_INFO,ptcp_info,&len_info) == 0)
		{
			//__android_log_print(ANDROID_LOG_INFO,TAG,"Log Msg : %d ", ptcp_info ->tcpi_rcv_rtt);
			struct simple_tcp_info *ptr_n = psimple_tcp_info + recv_data_count ;

			ptr_n -> rtt = ptcp_info -> tcpi_rtt;
			ptr_n -> rttvar = ptcp_info -> tcpi_rttvar;
			ptr_n -> rcv_rtt = ptcp_info -> tcpi_rcv_rtt;

			ptr_n -> last_data_sent = ptcp_info -> tcpi_last_data_sent;
			ptr_n -> last_data_recv = ptcp_info -> tcpi_last_data_recv;
			ptr_n -> last_data_size  = ret ;
		}
		recv_data_count ++ ;
		//一秒读一次缓冲区
		sleep(1);
	}
	__android_log_print(ANDROID_LOG_INFO,TAG,"Ente HttpGet function's finish!");

	free(headBuf);
	free(recvBuf);
	free(ptcp_info);

	shutdown(sock,SHUT_RDWR);
	close(sock);
	return 0;
}

/*
 mmain(int test_count)
{
	__android_log_print(ANDROID_LOG_INFO,TAG,"Enter nmain functions !");
	char *head,*tail;
	char server[128] = {0};

	//use IP
	head = strstr("http://143.205.176.132/ftp/datasets/DASHDataset2014/BigBuckBunny/6sec/bunny_3858484bps/BigBuckBunny_6s16.m4s","//");
	if(!head)
	{
		puts("Bad url format");
		return -1;
	}
	head += 2;
	tail =strchr(head,'/');
	if(!tail)
	{
		__android_log_print(ANDROID_LOG_INFO,TAG,"Here2");
		return HttpGet(head,"/");
	}
	else if( tail - head > sizeof(server) - 1)
	{
		puts("Bad url format");
		return -1;
	}
	else
	{
		__android_log_print(ANDROID_LOG_INFO,TAG,"Here3");
		memcpy(server,head,tail-head);
		__android_log_print(ANDROID_LOG_INFO,TAG,"Here : %s --- %s ",server,tail);
		return HttpGet(server,tail);
	}
}
*/
