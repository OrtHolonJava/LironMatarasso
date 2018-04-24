import javax.swing.SwingUtilities;

/**
 * GameLoop class for running the game thread
 * 
 * @author liron
 *
 */
public class GameLoop implements Runnable
{
	private Thread _gameThread;
	private GamePanel _gamePanel;

	private final double _ups = 60.0, _timeBetweenUpdates = 1000000000 / _ups, _targetFPS = 60,
			_timeBetweenRenders = 1000000000 / _targetFPS;
	private final int _maxUpdatesBeforeRender = 5;
	private boolean _paused;

	/**
	 * Init a new GameLoop object with the following parameters:
	 * 
	 * @param gamePanel
	 */
	public GameLoop(GamePanel gamePanel)
	{
		_gamePanel = gamePanel;
		_paused = false;
	}

	/**
	 * starts the game thread
	 */
	public void startGame()
	{
		_gameThread = new Thread(this);
		_gameThread.start();
	}

	@Override
	public void run()
	{
		double lastUpdateTime = System.nanoTime(); // Store the time of the last
													// update call.
		double lastRenderTime = System.nanoTime(); // Store the time of the last
													// render call.
		double now;
		int updateCount;

		/**
		 * FPS Calculation Variables
		 */
		int lastSecondTime = (int) (lastUpdateTime / 1000000000);

		while (true)
		{
			now = System.nanoTime();
			updateCount = 0;

			/**
			 * Doing as many game updates as we currently need to.
			 */
			while (now - lastUpdateTime > _timeBetweenUpdates && updateCount < _maxUpdatesBeforeRender)
			{
				tick();
				lastUpdateTime += _timeBetweenUpdates;
				updateCount++;
			}

			// If for some reason an update takes forever, we don't want to do
			// an insane number of catchups.
			// If you were doing some sort of game that needed to keep EXACT
			// time, you would get rid of this.
			if (now - lastUpdateTime > _timeBetweenUpdates)
			{
				lastUpdateTime = now - _timeBetweenUpdates;
			}

			while (_paused)
			{
				Thread.yield();
			}
			/**
			 * Render the current (updated) state of the game.
			 */
			render();

			lastRenderTime = now;

			// Update the frames we got.
			int thisSecond = (int) (lastUpdateTime / 1000000000);
			if (thisSecond > lastSecondTime)
			{
				lastSecondTime = thisSecond;
			}

			/**
			 * The timing mechanism. The thread sleeps within this while loop
			 * until enough time has passed and another update or render call is
			 * required.
			 */
			while (now - lastRenderTime < _timeBetweenRenders && now - lastUpdateTime < _timeBetweenUpdates)
			{
				Thread.yield(); // Yield until it has been at least the target
								// time between renders. This saves the CPU from
								// hogging.
				now = System.nanoTime();
			}
		}
	}

	/**
	 * repaints the game panel
	 */
	private void render()
	{
		SwingUtilities.invokeLater(() -> _gamePanel.paintImmediately(_gamePanel.getBounds()));
	}

	/**
	 * does another cycle of computing everything
	 */
	private void tick()
	{
		_gamePanel.doLogic();
	}

	public void setPaused(boolean paused)
	{
		_paused = paused;
	}

	public boolean getPaused()
	{
		return _paused;
	}

	public void flipPaused()
	{
		_paused = !_paused;
	}

}
