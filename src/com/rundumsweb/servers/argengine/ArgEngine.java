package com.rundumsweb.servers.argengine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

import com.rundumsweb.servers.argengine.Options.Option;
import com.rundumsweb.servers.argengine.OptionsSheet.OptionsSheetEntry;

public class ArgEngine extends JavaPlugin {

	/**
	 * A test to check functionality standalone.
	 */
	public static void main(String args[]) {

		OptionsSheet testSheet = new OptionsSheet();
		testSheet.exceptionOnUnknown = false;
		testSheet.exceptionOnTypeMismatch = true;
		OptionsSheetEntry entry = testSheet.new OptionsSheetEntry();
		entry.name = "brown";
		entry.type = OptionType.BOOLEAN;
		entry.defaultValue = false;
		entry.required = true;
		entry.exceptionOnTypeMismatch = false;
		entry.mismatchOverride = true;
		// entry.incompatible = new HashSet<>();
		// entry.incompatible.add("long-option");
		testSheet.addEntry(entry);

		try {
			// testSheet
			// .load("+N#=#false#false\nbrown#BOOLEAN####false#false#false");

			Options result = process(
					"--brown=joke --long-option \"mit einem value\"", testSheet);
			System.out.println("Results: " + result.getOptions().length);
			for (Option opt : result.getOptions()) {
				System.out.println("Option: " + opt.name + " Value: "
						+ opt.value);
			}
			if (result.getBoolean("brown"))
				System.out.println("brown = true");
			else
				System.out.println(result.get("brown"));
		} catch (ArgEngineException ignored) {
			ignored.printStackTrace();
		}
	}

	// */

	/**
	 * To process a query string.<br>
	 * The command should be filtered first.
	 * 
	 * @param line
	 *            The query
	 * @param sheet
	 *            The OptionsSheet
	 * @return Returns an {@link Options} object according to the query
	 * @throws RequirementUnsatisfiedException
	 *             if a required option was not set
	 * @throws TooManyArgumentsException
	 *             if an argument was parsed which is not defined in the
	 *             OptionsSheet (must be enabled in OptionsSheet)
	 * @throws TypeMismatchException
	 *             if type mismatch check is enabled and the type of an option
	 *             does not match
	 * @throws IncompatibleArgumentsException
	 *             if incompatible options were detected
	 */
	public static Options process(String line, OptionsSheet sheet)
			throws RequirementUnsatisfiedException, TooManyArgumentsException,
			TypeMismatchException, IncompatibleArgumentsException {
		return process(line, 0, sheet);
	}

	/**
	 * Same as process(String, OptionsSheet)
	 * 
	 * @param args
	 *            The query
	 * @param sheet
	 *            The OptionsSheet
	 * @return Returns an {@link Options} object according to the query
	 * @throws RequirementUnsatisfiedException
	 *             if a required option was not set
	 * @throws TooManyArgumentsException
	 *             if an argument was parsed which is not defined in the
	 *             OptionsSheet (must be enabled in OptionsSheet)
	 * @throws TypeMismatchException
	 *             if type mismatch check is enabled and the type of an option
	 *             does not match
	 * @throws IncompatibleArgumentsException
	 *             if incompatible options were detected
	 * @see #process(String, OptionsSheet)
	 */
	public static Options process(String args[], OptionsSheet sheet)
			throws RequirementUnsatisfiedException, TooManyArgumentsException,
			TypeMismatchException, IncompatibleArgumentsException {
		return process(ArgEngineArrayUtils.join(args), sheet);
	}

	/**
	 * Same as process(String, Integer, OptionsSheet)
	 * 
	 * @param args
	 *            The query
	 * @param ignore
	 *            An offset to ignore the first elements of the array
	 * @param sheet
	 *            The OptionsSheet
	 * @return Returns an {@link Options} object according to the query
	 * @throws RequirementUnsatisfiedException
	 *             if a required option was not set
	 * @throws TooManyArgumentsException
	 *             if an argument was parsed which is not defined in the
	 *             OptionsSheet (must be enabled in OptionsSheet)
	 * @throws TypeMismatchException
	 *             if type mismatch check is enabled and the type of an option
	 *             does not match
	 * @throws IncompatibleArgumentsException
	 *             if incompatible options were detected
	 * @see #process(String, Integer, OptionsSheet)
	 */
	public static Options process(String args[], Integer ignore,
			OptionsSheet sheet) throws RequirementUnsatisfiedException,
			TooManyArgumentsException, TypeMismatchException,
			IncompatibleArgumentsException {
		return process(
				ArgEngineArrayUtils.join(Arrays.copyOfRange(args,
						ignore.intValue(), args.length)), sheet);
	}

	/**
	 * To process a query string.
	 * 
	 * @param line
	 *            The query
	 * @param ignore
	 *            How many words should be ignored before processing the
	 *            arguments
	 * @param sheet
	 *            The OptionsSheet
	 * @return Returns an {@link Options} object according to the query
	 * @throws RequirementUnsatisfiedException
	 *             if a required option was not set
	 * @throws TooManyArgumentsException
	 *             if an argument was parsed which is not defined in the
	 *             OptionsSheet (must be enabled in OptionsSheet)
	 * @throws TypeMismatchException
	 *             if type mismatch check is enabled and the type of an option
	 *             does not match
	 * @throws IncompatibleArgumentsException
	 *             if incompatible options were detected
	 */
	public static Options process(String line, Integer ignore,
			OptionsSheet sheet) throws RequirementUnsatisfiedException,
			TooManyArgumentsException, TypeMismatchException,
			IncompatibleArgumentsException {
		if (ignore != null && ignore != 0) {
			String args[] = line.split(" ");
			return process(args, ignore, sheet);
		}
		// charwise parsing
		char chars[] = line.toCharArray();
		LinkedList<String> words = new LinkedList<>();
		String current = "";
		boolean inQuotes = false, escaped = false;
		for (char c : chars) {
			if (escaped) {
				if (c == 'n')
					current += "\n";
				else if (c == 't')
					current += "\t";
				else
					current += c;
				escaped = false;
			} else {
				if (c == '"') {
					inQuotes = !inQuotes;
				} else if (c == '\\')
					escaped = true;
				else if (c == ' ') {
					if (inQuotes)
						current += ' ';
					else {
						words.add(current);
						current = "";
					}
				} else
					current += c;

			}
		}
		if (!current.isEmpty())
			words.add(current);
		Options ret = new Options();

		// copy default values
		OptionsSheetEntry[] ents = sheet.getEntries();
		for (OptionsSheetEntry e : ents) {
			if (!e.required) {
				Option o = ret.new Option();
				o.name = new String(e.name);
				o.type = e.type;
				if (e.defaultValue instanceof String)
					o.value = new String((String) e.defaultValue);
				else if (e.type == OptionType.BOOLEAN)
					o.value = String.valueOf((boolean) e.defaultValue);
				else
					o.value = new String((String) e.defaultValue);
				ret.addOption(o);
			}
		}

		boolean lastWasLong = false;
		String longName = null;
		while (!words.isEmpty()) {
			String word = words.poll();
			Option o = ret.new Option();
			if (lastWasLong && !word.startsWith("-")) {
				// last word was long option
				lastWasLong = false;
				o.name = longName;
				o.type = OptionType.KEY_VALUE_PAIR;
				o.value = word;
			} else {
				if (lastWasLong) {
					o.name = longName;
					o.type = OptionType.BOOLEAN;
					o.value = true;
					lastWasLong = false;
				}
				if (word.startsWith("--")) {
					// long option
					lastWasLong = true;
					longName = word.substring(2);
					if (longName.contains(sheet.separator)) {
						lastWasLong = false; // This argument does not have a
												// second part.
						String parts[] = longName.split(sheet.separator, 2);
						if (parts.length != 2)
							throw new IllegalArgumentException(
									"The following option could not be processed: "
											+ longName);
						o.name = parts[0];
						o.type = OptionType.KEY_VALUE_PAIR;
						o.value = parts[1];
					} else
						continue;
				} else if (word.startsWith("-")) {
					// boolean false
					o.name = word.substring(1);
					o.type = OptionType.BOOLEAN;
					o.value = false;
				} else if (word.startsWith("+")) {
					// boolean true
					o.name = word.substring(1);
					o.type = OptionType.BOOLEAN;
					o.value = true;
				} else {
					o.name = word;
					o.type = OptionType.SETTING;
					o.value = true;
				}
			}
			if (sheet.exceptionOnUnknown) {
				if (!sheet.contains(o.name))
					throw new TooManyArgumentsException("The argument \""
							+ o.name + "\" is unknown!");
			}
			ret.addOption(o);
		}

		// test for required
		ents = sheet.getEntries();
		String opts = "";
		boolean all = true;
		for (OptionsSheetEntry e : ents) {
			if (e.required) {
				if (!ret.contains(e.name)) {
					all = false;
					opts += ", " + e.name;
				}
			}
		}

		if (!all)
			throw new RequirementUnsatisfiedException(
					"The following arguments are unsatisfied: "
							+ opts.substring(2));

		HashSet<String> optsS = new HashSet<>();
		Option optsO[] = ret.getOptions();
		for (Option o : optsO) {
			optsS.add(o.name);
		}
		HashSet<String> incomps = new HashSet<>();
		for (Option o : optsO) {
			OptionsSheetEntry ent = sheet.getEntry(o.name);
			if (ent == null)
				continue;
			Set<String> icmp = ent.incompatible;
			if (icmp == null)
				continue;
			incomps.addAll(icmp);
			for (String i : incomps)
				if (optsS.contains(i)) {
					all = false;
					opts += ", " + o.name + " and " + i;
				}
		}

		if (!all)
			throw new IncompatibleArgumentsException(
					"The following arguments are incompatible: "
							+ opts.substring(2));

		// Test for type match
		for (OptionsSheetEntry e : ents) {
			if ((e.mismatchOverride && e.exceptionOnTypeMismatch)
					|| (!e.mismatchOverride && sheet.exceptionOnTypeMismatch)) {
				Option o = ret.getOption(e.name);
				if (o == null)
					continue;
				if (e.type != OptionType.ANY && o.type != e.type) {
					// Type mismatch
					opts += ", " + o.name;
					all = false;
				} else {
					// Type match 
					// Check if type is SELECTION and if then check if value is valid
					if (e.type == OptionType.SELECTION) {
						if (!e.allowed.contains(o.value)) {
							// value is invalid
							opts += ", " + o.name + " (selection value is invalid)";
						}
					}
				}
			}
		}

		if (!all)
			throw new TypeMismatchException(
					"The following arguments have wrong types: "
							+ opts.substring(2));
		return ret;
	}
}
