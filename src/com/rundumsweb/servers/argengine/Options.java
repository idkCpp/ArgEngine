package com.rundumsweb.servers.argengine;

import java.util.LinkedList;

public class Options {
	private LinkedList<Option> options = new LinkedList<>();

	/**
	 * Adds an option to this container.<br>
	 * If there is already an option with this name it will be replaced.
	 * 
	 * @param o
	 *            The option
	 */
	public void addOption(Option o) {
		if (contains(o.name)) {
			removeOption(o.name);
		}
		options.add(o);
	}

	/**
	 * Check if this container contains a option with the given name.
	 * 
	 * @param o
	 *            The name
	 * @return true if contains
	 */
	public boolean contains(String o) {
		boolean c = false;
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).name.equals(o))
				c = true;
		}
		return c;
	}

	/**
	 * Remove an Option from this container by name.
	 * 
	 * @param o
	 *            The name
	 */
	public void removeOption(String o) {
		for (int i = 0; i < options.size(); i++) {
			Option e = options.get(i);
			if (e.name.equals(o))
				options.remove(e);
		}
	}

	/**
	 * Gets all options hold by this container.
	 * 
	 * @return An array of Options
	 */
	public Option[] getOptions() {
		Option[] ents = new Option[options.size()];
		for (int i = 0; i < options.size(); i++) {
			ents[i] = options.get(i).clone();
		}
		return ents;
	}

	/**
	 * Gets the value Object for this option name.
	 * 
	 * @param name
	 *            The name
	 * @return The Object hold by the option with the given name
	 */
	public Object get(String name) {
		Option o = getOption(name);
		if (o == null)
			return null;
		return o.value;
	}

	/**
	 * Gets the Option object for given name.
	 * 
	 * @param name
	 *            The name
	 * @return The Option object
	 */
	public Option getOption(String name) {
		Option o = null;
		for (int i = 0; i < options.size(); i++) {
			Option e = options.get(i);
			if (e.name.equals(name))
				o = e;
		}
		if (o == null)
			return null;
		return o;
	}

	/**
	 * Get the boolean value of this option.<br>
	 * If this option is a String then "0", "false" and "no" mean false and "1",
	 * "true" and "yes" mean true.<br>
	 * If this option cannot be identified as a boolean value this will return
	 * false.
	 * 
	 * @param name
	 *            The option's name
	 * @return true if this option's value is true, false otherwise
	 */
	public boolean getBoolean(String name) {
		Object o = get(name);
		if (o instanceof Boolean)
			return (boolean) o;
		if (o instanceof String) {
			String s = (String) o;
			if (s.equals("0") || s.equalsIgnoreCase("false")
					|| s.equalsIgnoreCase("no"))
				return false;
			if (s.equals("1") || s.equalsIgnoreCase("true")
					|| s.equalsIgnoreCase("yes"))
				return true;
		}
		try {
			if ((boolean) o == true)
				return true;
		} catch (Exception ignored) {}
		return false;
	}

	/**
	 * Gets the String value of the option with the given name.
	 * 
	 * @param name
	 *            The name
	 * @return The String value
	 */
	public String getString(String name) {
		Object o = get(name);
		if (o instanceof String)
			return (String) o;
		return o.toString();
	}

	public class Option {
		/**
		 * The name by which this value can be accessed.
		 */
		public String name;

		/**
		 * The type of this value
		 */
		public OptionType type;

		/**
		 * The value
		 */
		public Object value;

		public Option clone() {
			Option ret = new Option();
			ret.name = new String(name);
			ret.type = type;
			if (value instanceof String)
				ret.value = new String((String) value);
			else
				ret.value = String.valueOf((boolean) value);
			return ret;
		}
	}
}
