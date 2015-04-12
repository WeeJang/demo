/**
 * HTTPGet.h
 * 封装与HTTP请求相关方法
 */

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<fcntl.h>
#include<ctype.h>
#include<unistd.h>
#include<netdb.h>
#include<sys/types.h>
#include<sys/stat.h>
#include <sys/socket.h>
#include<netinet/in.h>
#include<netinet/tcp.h>
#include<arpa/inet.h>

// 简化的tcp_info,只包含想要的信息
struct simple_tcp_info{
	int rtt;
	int rttvar;
	int rcv_rtt;
	int last_data_sent;
	int last_data_recv;
	int last_data_size;
} ;

int GetHttpResponseHead(int sock,char *buf,int size);
int HttpGet(const char *server ,const char *url,struct simple_tcp_info* pstcp_info,int len);

