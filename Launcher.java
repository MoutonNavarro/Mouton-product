package ph.Maymay.Mouton.IndentConverter;

/**
 * @author Mouton Navarro
 * For the entry point of IndentConverter.
 */
class Launcher {
	static int TABS = 3;
	static int TABSM = 3;
	static int TABSS = 3;

	/**
	 * @param strings The launch argument: Input file or directory, Output file or directory, spaces with start of source(Optional), spaces with middle of source(Optional), Start of source with after space(Optional).
		<br>In default arguments are "" "" 3 3 3
		<br>Also, if you not given 5th argument then set same as 4th argument automatically.
	 * Only method of this launcher.<br>This launcher catches any Throwable. you should not call from other class.
	 */
	public static void main(String...strings) {
		if (strings.length < 1 || strings[0] == null || strings[0].equals("")) {
			System.err.println("Please set source directory name.(Not available current directory setting)");
			System.exit(1);
		}
		if (strings.length < 2 || strings[1] == null || strings[1].equals("")) {
			System.err.println("Please set target directory name.(Not available current directory setting)");
			System.exit(1);
		}

		if (strings.length >= 3 && !strings[2].equals("")) {
			try {
				TABS = Integer.parseInt(strings[2]);
				if (TABS < 0 || TABS > 255) {
					System.err.println("Illegal head tab size detected. set default size (3)");
					System.err.println("Input: " + TABS);
					TABS = 3;
				}
			}catch (NumberFormatException e) {
				System.err.println("Please set correctly number format at head tab size.");
				System.exit(1);
			}
		}
		if (strings.length >= 4 && !strings[3].equals("")) {
			try {
				TABSM = Integer.parseInt(strings[3]);
				if (TABSM < 0 || TABSM > 255) {
					System.err.println("Illegal body tab size detected. set default size (3)");
					System.err.println("Input: " + TABSM);
				}
			}catch (NumberFormatException e) {
				System.err.println("Please set correctly number format at body tab size.");
				System.exit(1);
			}
		}
		if (strings.length >= 6 && !strings[4].equals("")) {
			try {
				TABSM = Integer.parseInt(strings[4]);
				if (TABSS < 0 || TABSS > 255) {
					System.err.println("Illegal body tab size detected. set default size (3)");
					System.err.println("Input: " + TABSS);
				}
			}catch (NumberFormatException e) {
				System.err.println("Please set correctly number format at body tab size.");
				System.exit(1);
			}
		}else {TABSS = TABSM;}
		Iterator FileSequencialWidth(strings[0], strings[1]);



	}

}
