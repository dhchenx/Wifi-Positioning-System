package com.lbsserver.map;

/**
 * 
 * 此类用于获得文件或者目录大小
 * 
 * FileSize.toString()以KB，MB，GB形式返回字符串
 * 
 * getLongSize()以字节形式返回文件或者目录大小
 * 
 * 关键方法
 * 
 * File.length返回文件的字节
 * 
 * author:yehaizi
 * 
 */
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;


public class FileSize {
	// bt字节参考量
	public static final long SIZE_BT = 1024L;
	// KB字节参考量
	public static final long SIZE_KB = SIZE_BT * 1024L;
	// MB字节参考量
	public static final long SIZE_MB = SIZE_KB * 1024L;
	// GB字节参考量
	public static final long SIZE_GB = SIZE_MB * 1024L;
	// TB字节参考量
	public static final long SIZE_TB = SIZE_GB * 1024L;
	public static final int SACLE = 2;

	// 文件属性
	private File file;
	// 文件大小属性
	private long longSize;

	public FileSize(File file) {
		this.file = file;
	}

	// 返回文件大小
	private void getFileSize() throws RuntimeException, IOException {
		// 初始化文件大小为0；
		this.longSize = 0;

		// 如果文件存在而且是文件，直接返回文件大小
		if (file.exists() && file.isFile()) {
			this.longSize = file.length();

			// 文件存在而且是目录，递归遍历文件目录计算文件大小
		} else if (file.exists() && file.isDirectory()) {
			getFileSize(file);// 递归遍历
		} else {
			throw new RuntimeException("指定文件不存在");
		}
	}

	// 递归遍历文件目录计算文件大小
	private void getFileSize(File file) throws RuntimeException, IOException {
		// 获得文件目录下文件对象数组
		File[] fileArray = file.listFiles();
		// 如果文件目录数组不为空或者length!=0,即目录为空目录
		if (fileArray != null && fileArray.length != 0) {
			// 遍历文件对象数组
			for (int i = 0; i < fileArray.length; i++) {
				File fileSI = fileArray[i];
				// 如果是目录递归遍历
				if (fileSI.isDirectory()) {
					// 递归遍历
					getFileSize(fileSI);
				}
				// 如果是文件
				if (fileSI.isFile()) {
					this.longSize += fileSI.length();
				}
			}
		} else {
			// 如果文件目录数组为空或者length==0,即目录为空目录
			this.longSize = 0;
		}
	}
public long getKBSize(){
	return this.getLongSize()/this.SIZE_KB;
}
	public String toString() throws RuntimeException {
		try {
			// 调用计算文件或目录大小方法
			getFileSize();

			if (this.longSize >= 0 && this.longSize < SIZE_BT) {
				return this.longSize + "B";
			} else if (this.longSize >= SIZE_BT && this.longSize < SIZE_KB) {
				return this.longSize / SIZE_BT + "KB";
			} else if (this.longSize >= SIZE_KB && this.longSize < SIZE_MB) {
				return this.longSize / SIZE_KB + "MB";
			} else if (this.longSize >= SIZE_MB && this.longSize < SIZE_GB) {
				BigDecimal longs = new BigDecimal(Double.valueOf(
						this.longSize + "").toString());
				BigDecimal sizeMB = new BigDecimal(Double.valueOf(SIZE_MB + "")
						.toString());
				String result = longs.divide(sizeMB, SACLE,
						BigDecimal.ROUND_HALF_UP).toString();
				// double result=this.longSize/(double)SIZE_MB;
				return result + "GB";
			} else {
				BigDecimal longs = new BigDecimal(Double.valueOf(
						this.longSize + "").toString());
				BigDecimal sizeMB = new BigDecimal(Double.valueOf(SIZE_GB + "")
						.toString());
				String result = longs.divide(sizeMB, SACLE,
						BigDecimal.ROUND_HALF_UP).toString();
				return result + "TB";
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getLongSize() throws RuntimeException {
		try {
			// 调用计算文件或目录大小方法
			getFileSize();
			return longSize;
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
	}

}