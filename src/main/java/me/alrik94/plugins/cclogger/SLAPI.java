/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.alrik94.plugins.cclogger;

import java.io.*;

/**
 *
 * @author Alrik
 */
public class SLAPI
{
	public static void save(Object obj,File file) throws Exception
	{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}
	public static Object load(File file) throws Exception
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		Object result = ois.readObject();
		ois.close();
		return result;
	}
}
