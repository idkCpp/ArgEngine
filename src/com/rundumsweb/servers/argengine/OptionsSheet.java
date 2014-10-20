package com.rundumsweb.servers.argengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class OptionsSheet {
	private LinkedList<OptionsSheetEntry> entries = new LinkedList<>();

	/**
	 * For those who like the etiquette of Linux CLI programs. <br>
	 * (where option -a -b is the same as -ab) <br>
	 * For non-Linux-like processing rules look at DevBukkit.
	 */
	public boolean linuxLike = false;

	/**
	 * Throw a exception if an argument was given which was not declared in the
	 * OptionsSheet
	 */
	public boolean exceptionOnUnknown = true;

	/**
	 * If you want to throw an exception on type mismatch. (can be overridden
	 * for specific options)
	 */
	public boolean exceptionOnTypeMismatch = false;

	/**
	 * The character by which key and value are separated.
	 */
	public String separator = "=";

	/**
	 * Returns all entries in this sheet.
	 * 
	 * @return An array of entries
	 */
	public OptionsSheetEntry[] getEntries() {
		OptionsSheetEntry[] ents = new OptionsSheetEntry[entries.size()];
		for (int i = 0; i < entries.size(); i++) {
			ents[i] = entries.get(i).clone();
		}
		return ents;
	}

	/**
	 * Gets a specific OptionsSheetEntry
	 * @param name	The entry's name
	 * @return		The entry
	 */
	public OptionsSheetEntry getEntry(String name) {
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).name.equals(name))
				return entries.get(i);
		}
		return null;
	}

	/**
	 * Checks if an option with this name exists.
	 * 
	 * @param name
	 *            The name
	 * @return True if exists
	 */
	public boolean contains(String name) {
		boolean c = false;
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).name.equals(name))
				c = true;
		}
		return c;
	}

	/**
	 * Adds an entry to this OptionSheet.<br>
	 * If the entry already exists by name it will be replaced.
	 * 
	 * @param entry
	 *            The entry to add
	 */
	public void addEntry(OptionsSheetEntry entry) {
		if (contains(entry.name)) {
			removeEntry(entry.name);
		}
		entries.add(entry);
	}

	/**
	 * Removes an entry by name.
	 * 
	 * @param name
	 *            The entry's name
	 */
	public void removeEntry(String name) {
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).name.equals(name))
				entries.remove(entries.get(i));
		}
	}

	/**
	 * To load the OptionSheet from a {@link Reader}.
	 * 
	 * @param reader
	 *            The reader
	 * @throws IOException
	 *             if the reader operation fails
	 * @throws ArgEngineException
	 *             if the parsing fails
	 */
	public void load(Reader reader) throws IOException, ArgEngineException {
		BufferedReader br = new BufferedReader(reader);
		String line = null;
		while ((line = br.readLine()) != null)
			loadSingle(line);
	}

	/**
	 * To load the OptionSheet from a string.
	 * 
	 * @param str
	 *            The String
	 * @throws ArgEngineException
	 *             if the parsing fails
	 */
	public void load(String str) throws ArgEngineException {
		String lines[] = str.split("\n");
		for (String line : lines)
			loadSingle(line);
	}

	private void loadSingle(String str) throws ArgEngineException {
		if (str.startsWith("+")) {
			String parts[] = str.substring(1).split("#");
			if (parts.length != 4)
				throw new ArgEngineException("Unable to load this option: "
						+ str);
			if (parts[0].equalsIgnoreCase("L"))
				linuxLike = true;
			else if (parts[0].equalsIgnoreCase("N"))
				linuxLike = false;
			else
				throw new ArgEngineException(
						"Unable to load this option, the linuxLike option is invalid: "
								+ str);
			if (parts[1].equalsIgnoreCase("null"))
				separator = null;
			else
				separator = parts[1];

			exceptionOnUnknown = Boolean.parseBoolean(parts[2]);

			exceptionOnTypeMismatch = Boolean.parseBoolean(parts[3]);
		} else {
			String parts[] = str.split("#");
			if (parts.length != 8)
				throw new ArgEngineException(
						"Unable to load this option, wrong argument count: "
								+ str);
			OptionsSheetEntry ent = new OptionsSheetEntry();

			ent.name = parts[0];

			if (OptionType.valueOf(parts[1]) != null)
				ent.type = OptionType.valueOf(parts[1]);
			else
				throw new ArgEngineException(
						"Unable to load this option, the OptionType is invalid: "
								+ str);

			ent.incompatible = new HashSet<>();
			String incomps[] = parts[2].split(",");
			for (String s : incomps)
				ent.incompatible.add(s);

			if (ent.type != OptionType.BOOLEAN)
				ent.defaultValue = parts[3];
			else
				ent.defaultValue = Boolean.parseBoolean(parts[3]);

			ent.allowed = new HashSet<>();
			String allow[] = parts[4].split(",");
			for (String s : allow)
				ent.allowed.add(s);

			ent.required = Boolean.parseBoolean(parts[5]);

			ent.mismatchOverride = Boolean.parseBoolean(parts[6]);

			ent.exceptionOnTypeMismatch = Boolean.parseBoolean(parts[7]);

			addEntry(ent);
		}
	}

	public class OptionsSheetEntry {
		/**
		 * The name of this option.<br>
		 * The query will be search for this string.
		 */
		public String name = null;
		/**
		 * Which type this option is.
		 */
		public OptionType type = null;

		// /**
		// * The name of the option within the {@link Options} object.
		// */
		// //public String optionName = null;

		/**
		 * A set of names which are incompatible to this option.<br>
		 */
		public Set<String> incompatible = null;

		/**
		 * The value if the option was not mentioned in the query string
		 */
		public Object defaultValue = null;

		/**
		 * For {@link OptionType} <b>SELECTION</b>.<br>
		 * Determines which values are allowed for this option.
		 */
		public Set<String> allowed = null;

		/**
		 * For {@link OptionType}s <b>KEY_VALUE_PAIR</b>, <b>SELECTION</b>.<br>
		 * Determines if this key needs a value or if its optional.<br>
		 * This field will be ignored if a defaultValue is not null.
		 * 
		 * @see #defaultValue
		 */
		public boolean required = false;

		/**
		 * Overrides the Sheet's setting for mismatch to option specific
		 */
		public boolean mismatchOverride = false;

		/**
		 * The overriding value (if enabled)
		 */
		public boolean exceptionOnTypeMismatch = false;

		public OptionsSheetEntry clone() {
			OptionsSheetEntry ret = new OptionsSheetEntry();
			ret.name = name;
			ret.type = type;
			if (incompatible != null) {
				ret.incompatible = new HashSet<>();
				ret.incompatible.addAll(incompatible);
			}
			if (type == OptionType.BOOLEAN)
				ret.defaultValue = String.valueOf((boolean) defaultValue);
			else
				ret.defaultValue = new String((String) defaultValue);
			if (allowed != null) {
				ret.allowed = new HashSet<>();
				ret.allowed.addAll(allowed);
			}
			ret.required = required;
			ret.mismatchOverride = mismatchOverride;
			ret.exceptionOnTypeMismatch = exceptionOnTypeMismatch;
			return ret;
		}
	}
}
