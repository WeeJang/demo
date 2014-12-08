package commlab.weejang.demo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.R.string;
import android.os.Environment;
import android.text.InputFilter.LengthFilter;

/**
 * 工具类，用于对SD卡的操作 暂时没用
 * @author jangwee
 *
 */

public class FileUtils
{
	//sd卡目录路径
	private static String SDPath = null;
	
	static
	{
		SDPath = Environment.getExternalStorageDirectory() + "/";
	}
	
	public static String getSDPath()
	{
		return SDPath;
	}
	
	
	/**
	 * 在SD卡根目录上创建目录
	 * @param dirname :  目录名
	 * 
	 */
	
	public static  File createSDDir(String dirname)
	{
		File dir = new File(SDPath + dirname);
		dir.mkdir();
		return dir;
	}
	
	
	/**
	 * 在SD卡根目录上创建文件
	 * @param filename : 创建的文件名
	 * @throws IOException
	 */
	
	public static File createSDFile(String filename) throws IOException
	{
		File file = new File(SDPath + filename);
		file.createNewFile();
		return file;
	}
	
	/**
	 * 在SD卡某个文件夹下创建文件目录
	 * @param  dirname : 创建文件所属文件夹
	 * @param filename : 创建文件名称
	 * @parem isRenew: 是否覆盖曾经有的文件
	 * @throws IOException
	 */
	public static File createSDFile(String dirname,String filename)throws IOException
	{
		File file = new File(SDPath + dirname +"/" + filename);
		file.createNewFile();
		return file;

	}
	
	
	/**
	 * 判断SD卡上某一个文件夹或者文件是否存在
	 *@param fileOrDirAbsolutePath 文件或者文件夹的绝对路径
	 */
	public static boolean isFileOrDirExists(String fileOrDirAbsolutePath)
	{
		File fileOrDir = new File(fileOrDirAbsolutePath);
		return fileOrDir.exists();
	}
	
	/**
	 * 流操作
	 */
	public static void write2SDFromInput(OutputStream outputStream,InputStream inputStream)
	{	
		try
		{
			byte buffer[] = new byte[1024*4];
			int length  =  0 ;
			while ((length = inputStream.read(buffer)) != -1)
			{
				outputStream.write(buffer,0,length);	
			}
			outputStream.flush();
			
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}		
	}
	
	
	
	
}
