package com.lxb.easyar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;

public class FileUtil {
	private static final String SEPARATOR = File.separator;// 路径分隔�?

	/**
	 * 复制res/raw中的文件到指定目�?
	 * 
	 * @param context
	 *            上下�?
	 * @param id
	 *            资源ID
	 * @param fileName
	 *            文件�?
	 * @param storagePath
	 *            目标文件夹的路径
	 */
	public static void copyFilesFromRaw(Context context, int id,
			String fileName, String storagePath) {
		InputStream inputStream = context.getResources().openRawResource(id);
		File file = new File(storagePath);
		if (!file.exists()) {// 如果文件夹不存在，则创建新的文件�?
			file.mkdirs();
		}
		readInputStream(storagePath + SEPARATOR + fileName, inputStream);
	}

	/**
	 * 读取输入流中的数据写入输出流
	 * 
	 * @param storagePath
	 *            目标文件路径
	 * @param inputStream
	 *            输入�?
	 */
	public static void readInputStream(String storagePath,
			InputStream inputStream) {
		File file = new File(storagePath);
		try {
			if (!file.exists()) {
				// 1.建立通道对象
				FileOutputStream fos = new FileOutputStream(file);
				// 2.定义存储空间
				byte[] buffer = new byte[inputStream.available()];
				// 3.�?始读文件
				int lenght = 0;
				while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
					// 将Buffer中的数据写到outputStream对象�?
					fos.write(buffer, 0, lenght);
				}
				fos.flush();// 刷新缓冲�?
				// 4.关闭�?
				fos.close();
				inputStream.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制assets中的文件到指定目�?
	 * 
	 * @param context
	 *            上下�?
	 * @param assetsPath
	 *            assets资源路径
	 * @param storagePath
	 *            目标文件夹的路径
	 */
	public static boolean copyFilesFromAssets(Context context,
			String assetsPath, String storagePath) {
		String temp = "";

		if (TextUtils.isEmpty(storagePath)) {
			return false;
		} else if (storagePath.endsWith(SEPARATOR)) {
			storagePath = storagePath.substring(0, storagePath.length() - 1);
		}

		if (TextUtils.isEmpty(assetsPath) || assetsPath.equals(SEPARATOR)) {
			assetsPath = "";
		} else if (assetsPath.endsWith(SEPARATOR)) {
			assetsPath = assetsPath.substring(0, assetsPath.length() - 1);
		}

		AssetManager assetManager = context.getAssets();
		try {
			File file = new File(storagePath);
			if (!file.exists()) {// 如果文件夹不存在，则创建新的文件�?
				file.mkdirs();
			}

			// 获取assets目录下的�?有文件及目录�?
			String[] fileNames = assetManager.list(assetsPath);
			if (fileNames.length > 0) {// 如果是目�? apk
				for (String fileName : fileNames) {
					if (!TextUtils.isEmpty(assetsPath)) {
						temp = assetsPath + SEPARATOR + fileName;// 补全assets资源路径
					}

					String[] childFileNames = assetManager.list(temp);
					if (!TextUtils.isEmpty(temp) && childFileNames.length > 0) {// 判断是文件还是文件夹：如果是文件�?
						copyFilesFromAssets(context, temp, storagePath
								+ SEPARATOR + fileName);
					} else {// 如果是文�?
						InputStream inputStream = assetManager.open(temp);
						readInputStream(storagePath + SEPARATOR + fileName,
								inputStream);
					}
				}
			} else {// 如果是文�? doc_test.txt或�?�apk/app_test.apk
				InputStream inputStream = assetManager.open(assetsPath);
				if (assetsPath.contains(SEPARATOR)) {// apk/app_test.apk
					assetsPath = assetsPath.substring(
							assetsPath.lastIndexOf(SEPARATOR),
							assetsPath.length());
				}
				readInputStream(storagePath + SEPARATOR + assetsPath,
						inputStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String getSDPath(Context context) {
		String path = Environment.getExternalStorageDirectory()
				+ "/Android/data/" + context.getPackageName() + "/download/";
		File file = new File(path);
		if (!file.exists() || !file.isDirectory())
			file.mkdirs();
		return path;
	}
}
