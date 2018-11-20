package view.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public class DoubleListener implements ChangeListener<String> {

	private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d*\\.?\\d*");

	private Consumer<Double> consumer;

	public DoubleListener(Consumer<Double> consumer) {
		this.consumer = consumer;
	}

	@Override
	public void changed(ObservableValue<? extends String> c, String o, String n) {
		if (n.length() > 0 && INTEGER_PATTERN.matcher(n).matches()) {
			consumer.accept(Double.parseDouble(n));
		}
	}
}
