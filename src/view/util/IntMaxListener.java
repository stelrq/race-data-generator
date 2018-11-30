package view.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public class IntMaxListener implements ChangeListener<String> {

	private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d*");

	private Consumer<Integer> consumer;
	private int myMax;

	public IntMaxListener(Consumer<Integer> consumer, int max) {
		this.consumer = consumer;
		myMax = max;
	}

	@Override
	public void changed(ObservableValue<? extends String> c, String o, String n) {
		if (n.length() > 0 && INTEGER_PATTERN.matcher(n).matches() && Integer.parseInt(n) <= myMax && Integer.parseInt(n) >= 1) {
			consumer.accept(Integer.parseInt(n));
		}
	}
}