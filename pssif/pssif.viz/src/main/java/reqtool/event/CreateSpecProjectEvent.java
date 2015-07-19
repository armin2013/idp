package reqtool.event;

import java.awt.Component;

/**
 * The Class CreateSpecProjectEvent.
 */
public class CreateSpecProjectEvent implements Event {
	
	/** The frame component. */
	private Component frame;
	
	/**
	 * Instantiates a new specification project creation event.
	 *
	 * @param frame the frame
	 */
	public CreateSpecProjectEvent(Component frame) {
		this.setFrame(frame);
	}

	/**
	 * Gets the frame.
	 *
	 * @return the frame
	 */
	public Component getFrame() {
		return frame;
	}

	/**
	 * Sets the frame.
	 *
	 * @param frame the new frame
	 */
	private void setFrame(Component frame) {
		this.frame = frame;
	}

}
