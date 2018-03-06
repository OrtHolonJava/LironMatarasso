import java.util.HashMap;

import javax.swing.SwingUtilities;

public class RealTimeSwing
{

	/**
	 * Run this Runnable in the Swing Event Dispatching Thread, and return when
	 * done with execution. This method can be called whether or not the current
	 * thread is in the Swing thread.
	 * 
	 * @param runnable
	 *            This is the code to be executed in the Swing thread.
	 */
	public static void invokeNow(Runnable runnable)
	{
		if (runnable == null)
			return;
		try
		{
			if (SwingUtilities.isEventDispatchThread())
			{
				runnable.run();
			}
			else
			{
				SwingUtilities.invokeAndWait(runnable);
			}
		}
		catch (InterruptedException ie)
		{
			System.out.println("Swing thread interrupted");
			Thread.currentThread().interrupt();
		}
		catch (java.lang.reflect.InvocationTargetException ite)
		{
			ite.printStackTrace();
			throw new IllegalStateException(ite.getMessage());
		}
	}

	/**
	 * Use this method for handling events in realtime, when the events may be
	 * generated more quickly than the the handler can complete. Call this
	 * method from the appropriate Listener.
	 * 
	 * Executes Runnables inside and outside the Swing Thread. Returns
	 * immediately. If this method is called again with the same id after less
	 * than the specified pause, then the first call will be ignored. The most
	 * recent call with a given id will not be allowed to start until previous
	 * call finishes. When previous call finishes, only the most recent call
	 * with same id will run. Others will be discarded.
	 * 
	 * @param id
	 *            This string uniquely identifies this task. If this method is
	 *            called again with the same id within the specified number of
	 *            milliseconds, then the first call will not be executed. If
	 *            this id is already executing, then it will wait until either
	 *            the previous execution finishes, or until a later call with
	 *            the same id.
	 * 
	 * @param milliseconds
	 *            Wait this long in a separate thread before executing
	 *            Runnables. You can safely set this to zero. Set the time less
	 *            than the expected time to execute the Runnables. If you set to
	 *            0, then a series of calls will be executed at least twice. If
	 *            you set to greater than 0, then the first call may be ignored
	 *            if followed quickly by another call.
	 * 
	 * @param worker
	 *            This Runnable will be executed first outside the Swing thread.
	 *            Set to null to skip this step.
	 * 
	 * @param refresher
	 *            This Runnable will be executed inside the Swing thread after
	 *            the worker has completed. Activity that uses or changes the
	 *            state of Swing widgets should be included here. Set to null to
	 *            skip this step.
	 */
	public static void invokeOnce(String id, final long milliseconds, final Runnable worker, final Runnable refresher)
	{
		synchronized (s_timestamps)
		{
			if (!s_timestamps.containsKey(id))
			{ // call once for each id
				s_timestamps.put(id, new Latest());
			}
		}
		final Latest latest = s_timestamps.get(id);
		final long time = System.currentTimeMillis();
		latest.time = time;

		(new Thread("Invoke once " + id)
		{
			public void run()
			{
				if (milliseconds > 0)
				{
					try
					{
						Thread.sleep(milliseconds);
					}
					catch (InterruptedException e)
					{
						return;
					}
				}
				synchronized (latest.running)
				{ // can't start until previous finishes
					if (latest.time != time)
						return; // only most recent gets to run
					if (worker != null)
						worker.run(); // outside Swing thread
					if (refresher != null)
						invokeNow(refresher); // inside Swing thread
				}
			}
		}).start();
	}

	private static java.util.Map<String, Latest> s_timestamps = new HashMap<String, Latest>();

	private static class Latest
	{
		/** Last time for this event */
		public volatile long time = 0;
		/** for synchronization */
		public final Object running = new Object();
	}

}

/*
 * Copyright (c) Bill Harlan, 1999 All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - Neither the names of contributors, nor the names of their employers may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */