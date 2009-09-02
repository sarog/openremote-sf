package org.openremote.console.web.client.def;

/**
 * Represents an button parsed from the iphone.xml.
 * 
 * @author David Reines
 */
public class ButtonDef {

	private final Integer id;

	private final String label;

	private final Integer x;

	private final Integer y;

	private final Integer width;

	private final Integer height;

	/**
	 * icon is optional.
	 */
	private String icon;

	public ButtonDef(Integer id, String label, Integer x, Integer y,
			Integer width, Integer height) {
		super();
		this.id = id;
		this.label = label;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "Button [height=" + height + ", icon=" + icon + ", id=" + id
				+ ", label=" + label + ", width=" + width + ", x=" + x + ", y="
				+ y + "]";
	}

}
